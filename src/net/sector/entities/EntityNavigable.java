package net.sector.entities;


import static org.lwjgl.opengl.GL11.*;

import java.util.Random;
import java.util.Set;

import net.sector.collision.ColliderSphere;
import net.sector.level.drivers.INavigated;
import net.sector.level.drivers.Navigator;
import net.sector.level.drivers.TaskList;
import net.sector.models.PhysModel;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Polar;
import com.porcupine.math.PolarDeg;


/**
 * Navigable entity
 * 
 * @author MightyPork
 */
public abstract class EntityNavigable extends Entity implements INavigated {

	/** Velocity multiplier */
	public double speedMul1 = 1;

	/** Stable speed multiplier, set when spawning entity */
	public double speedMulStable = 1;

	/** Ship driver */
	public Navigator nav = new Navigator(this);

	/** scale (relative, 1 default) */
	public double scale = 1;

	/** render scale (how much is the model enlarged) */
	public double scaleRender = 1;

	/** Phys model */
	public PhysModel model = null;

	/** Desired motion speed */
	public double fullMoveSpeed = 0.04;

	private Entity[] fleet;

	private Entity cachedLeader = null;

	private EFormation formation = EFormation.NONE;

	private Entity targetEntity = null;

	@Override
	public void spawnArtifact(int num) {
		// if in formation, only the last one drops artifact.

		if (formation == EFormation.SHAPE) {
			for (Entity e : fleet) {
				if (e == this) continue;
				if (e.isDead()) continue;
				return;
			}

			super.spawnArtifact(num);

			return;
		}


		if (formation != EFormation.NONE) {
			if (formationIsLeader() && formationIsTail()) {
				super.spawnArtifact(num);
			}
			return;
		}
		super.spawnArtifact(num);
	}

	@Override
	public Entity getTargetEntity() {
		return targetEntity;
	}

	@Override
	public void setTargetEntity(Entity targetEntity) {
		this.targetEntity = targetEntity;
	}

	@Override
	public void setFormation(Entity[] fleet, EFormation formation) {
		this.fleet = fleet;
		this.formation = formation;
	}

	@Override
	public Entity getFormationLeader() {
		if (formation == EFormation.NONE) return null;
		if (cachedLeader == null || cachedLeader.isDead() || cachedLeader.getPos().z < -1) {
			cachedLeader = null;
			if (formation == EFormation.SNAKE) cachedLeader = formationSnakeGetLeader();
			if (formation == EFormation.SWARM) cachedLeader = formationSwarmGetLeader();
		}

		return cachedLeader;
	}

	@Override
	public boolean formationIsTail() {
		if (formation == EFormation.NONE) return false;
		if (formation == EFormation.SNAKE) {
			for (int i = 0; i < fleet.length; i++) {
				if (fleet[i] == null || fleet[i].isDead()) continue;
				return fleet[i] == this;
			}
		}
		if (formation == EFormation.SWARM) return false;
		return true;
	}

	@Override
	public boolean formationIsLeader() {
		return getFormationLeader() == null;
	}

	private Entity formationSnakeGetLeader() {
		boolean finding = false;
		int leader = -1;
		for (int i = 0; i < fleet.length; i++) {
			Entity e = fleet[i];
			if (e == null) continue;
			if (e == this) {
				finding = true;
				continue;
			}
			if (e.isDead()) continue;

			if (finding) {
				leader = i;
				break;
			}
		}

		if (leader != -1) {
			return fleet[leader];
		} else {
			return null;
		}
	}

	private Entity formationSwarmGetLeader() {
		int leader = -1;

		for (int i = fleet.length - 1; i >= 0; i--) {
			Entity e = fleet[i];
			if (e == null) continue;
			if (e.isDead()) continue;
			leader = i;
			break;
		}

		if (leader != -1) {
			return fleet[leader];
		} else {
			return null;
		}
	}



	public boolean formationContains(Entity drone) {
		if (fleet != null) {
			for (Entity e : fleet)
				if (e == drone) return true;
		}
		return false;
	}

	/**
	 * @param model
	 * @param desiredSpeed
	 * @param scale
	 * @param pos
	 */
	protected EntityNavigable(PhysModel model, double desiredSpeed, double scale, Coord pos) {
		this.fullMoveSpeed = desiredSpeed;
		this.collidePriority = 1;

		this.scale = scale;

		collider = new ColliderSphere(pos, model.colliderRadius * scale);

		this.motion.setTo(0, 0, -desiredSpeed);

		this.lifetime = -1;

		this.MAX_SPEED = 0.3;

		// rotateable around Y axis
		this.rotDir.setTo(0, 1, 0);
		// rotate towards the player
		this.rotAngle.set(270);

		setModel(model);
	}

	/**
	 * Set the model (alter entity)
	 * 
	 * @param model
	 */
	public void setModel(PhysModel model) {
		this.model = model;

		this.scaleRender = model.renderScale * scale;

		this.mass = model.getMass(scale);
		this.collider.radius = model.colliderRadius * scale;

		this.health = model.getHealth(scale);

		this.scoreValue = (int) (model.getScore(scale));
	}

	/**
	 * Adjust strength, radius, score and mass for scale
	 * 
	 * @param scale relative scale (1 = normal)
	 * @return this
	 */
	public EntityNavigable adjustForScale(double scale) {
		this.scale = scale;
		this.scaleRender = model.renderScale * scale;

		this.mass = model.getMass(scale);
		collider.radius = model.colliderRadius * scale;

		this.health = model.getHealth(scale);

		this.scoreValue = (int) (model.getScore(scale));

		return this;
	}

	/**
	 * Get direction based on entity rotation
	 * 
	 * @return direction vector
	 */
	public Vec getRotateAimVector() {
		double deg = getRotAngle().get();
		PolarDeg pl = new PolarDeg(deg + 90, 1);
		return new Vec(pl.toCoordXZ());
	}

	/**
	 * Get direction based on entity rotation with random deviation
	 * 
	 * @param deviationDeg max angular deviation (degrees)
	 * @return direction vector
	 */
	public Vec getRotateAimVector(double deviationDeg) {
		double deg = getRotAngle().get();
		PolarDeg pl = new PolarDeg(deg + 90 - deviationDeg + rand.nextDouble() * deviationDeg * 2, 1);
		return new Vec(pl.toCoordXZ());
	}

	/**
	 * Get direction based on entity rotation with degrees deviation
	 * 
	 * @param addAngle angle added
	 * @return direction vector
	 */
	public Vec getRotateAimVectorPlusDeg(double addAngle) {
		double deg = getRotAngle().get();
		PolarDeg pl = new PolarDeg(deg + addAngle, 1);
		return new Vec(pl.toCoordXZ());
	}

	/**
	 * Get vector from this entity to player
	 * 
	 * @return vector to player
	 */
	public Vec getVectorToPlayer() {
		return getPos().vecTo(getScene().getPlayerShip().getPos());
	}

	/**
	 * Get vector from this entity to player
	 * 
	 * @param deviationDeg max angular deviation (degrees)
	 * @return vector to player
	 */
	public Vec getVectorToPlayer(double deviationDeg) {
		Vec v = getVectorToPlayer();
		PolarDeg p = PolarDeg.fromCoord(v.x, v.z);
		PolarDeg p2 = new PolarDeg(p.angle - deviationDeg + rand.nextDouble() * deviationDeg * 2, p.distance);
		return new Vec(p2.toCoordXZ());
	}

	/**
	 * Get vector to closest asteroid, or null if no target found.
	 * 
	 * @return vector to player
	 */
	public Vec getVectorToClosestAsteroid() {
		Set<Entity> ents = getScene().getEntitiesInRange(getPos(), 6);
		double shortest = 100;
		Coord shortestPos = null;
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e.getType() == EEntity.NATURAL) {
					double d;

					if ((d = getPos().vecTo(e.getPos()).size()) < shortest) {
						shortest = d;
						shortestPos = e.getPos();
					}
				}
			}

			if (shortestPos != null) {
				return getPos().vecTo(shortestPos);
			}
		}

		return null;
	}

	/**
	 * MUST BE OVERRIDEN AND USED
	 * 
	 * @param scale scale 1 = normal
	 * @param pos coord center
	 */
	//public EntityNavigable(double scale, Coord pos) {}

	/**
	 * Constructor without model.<br>
	 * !!! You must define things like score, health, lifetime and mass
	 * yourself.
	 * 
	 * @param desiredSpeed desired motion speed for AI
	 * @param pos position
	 * @param radius collider radius
	 */
	public EntityNavigable(double desiredSpeed, Coord pos, double radius) {
		this.fullMoveSpeed = desiredSpeed;
		this.collidePriority = 1;

		collider = new ColliderSphere(pos, radius * scale);

		this.motion.setTo(0, 0, -desiredSpeed);

		this.lifetime = -1;

		// rotateable around Y axis
		this.rotDir.setTo(0, 1, 0);
		// rotate towards the player
		this.rotAngle.set(270);
	}

	/**
	 * Constructor without model.<br>
	 * !!! You must define things like score, collider, rotDir, rotAngle,
	 * collider priority, health, lifetime and mass yourself.
	 * 
	 * @param pos position
	 */
	public EntityNavigable(Coord pos) {
		this.fullMoveSpeed = 0.001;
		this.collidePriority = 1;

		collider = new ColliderSphere(pos, 0.5);

		this.motion.setTo(0, 0, -fullMoveSpeed);

		this.lifetime = -1;

		// rotateable around Y axis
		this.rotDir.setTo(0, 1, 0);
		// rotate towards the player
		this.rotAngle.set(270);
	}

	@Override
	public void setDriver(TaskList driver) {
		this.nav.setDriver(driver);
	}

	@Override
	public Navigator getNavigator() {
		return nav;
	}

	@Override
	public double getDesiredSpeed() {
		return fullMoveSpeed * speedMul1 * speedMulStable;
	}

	@Override
	public void setDesiredSpeed(double speed) {
		fullMoveSpeed = speed;
	}

	@Override
	public void setShipLevel(int level) {}

	@Override
	public abstract void shootOnce(int gunIndex);

	@Override
	public abstract Vec getGunShotDir(int gunIndex);

	/**
	 * Get data for shot - position and direction.
	 * 
	 * @param x relative X coordinate (affected by scale factor)
	 * @param z relative Z coordinate (affected by scale factor)
	 * @return struct of (real position, motion vector)
	 */
	protected Coord getShotPos(double x, double z) {
		Vec zplus = motion.norm(z * scale);
		Coord mtn = motion.norm(x * scale);
		Polar pr = Polar.fromCoord(mtn.x, mtn.z);
		pr.angle += Math.PI / 2;

		Vec ro = new Vec(pr.toCoord());

		Coord pos = getPos().add(ro.x, 0, ro.z).add(zplus);

		return pos;
	}

	@Override
	public double getHealMultiplier() {
		return 1;
	}

	@Override
	public EEntity getType() {
		return EEntity.ENEMY;
	}

	@Override
	public abstract void onImpact(Entity hitBy);

	@Override
	public double getEmpSensitivity() {
		return 1;
	}

	@Override
	public double getFireFlammability() {
		return 1;
	}

	@Override
	public double getFireSensitivity() {
		return 0.85;
	}

	@Override
	public void onUpdate() {
		if (nav != null && !isDead) nav.onUpdate();
	}

	@Override
	public abstract void onDeath();

	@Override
	public void render(double delta) {
		if (isDead) return;
		if (model != null) {
			glLoadIdentity();

			Coord p = getPos().getDelta(delta);
			glTranslated(p.x, p.y, -p.z);
			glRotated(rotAngle.delta(delta), rotDir.x, rotDir.y, rotDir.z);
			glScaled(scaleRender, scaleRender, scaleRender);

			model.render();
		}
	}

	@Override
	public abstract double getHealthMax();

	@Override
	public double getHealthPercent() {
		return Math.round((getHealth() / getHealthMax()) * 100);
	}

	@Override
	public boolean hasGlobalMovement() {
		return false;
	}

	/**
	 * Set ship variant - used for level building.
	 * 
	 * @param variant variant number
	 */
	public void setShipVariant(int variant) {}


	public double getSpeedMultiplier() {
		return speedMulStable;
	}

	public void setSpeedMultiplier(double speedMul) {
		this.speedMulStable = speedMul;
	}

	public void setStableSpeedMultiplier(double speed) {
		this.speedMul1 = speed;
	}

	public double getStableSpeedMultiplier() {
		return speedMul1;
	}

	public double getSpeedMultiplierTotal() {
		return getSpeedMultiplier() * getStableSpeedMultiplier();
	}

}
