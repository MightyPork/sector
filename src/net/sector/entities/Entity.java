package net.sector.entities;


import java.util.Random;

import net.sector.Constants;
import net.sector.NullDamageSource;
import net.sector.collision.Collider;
import net.sector.collision.ColliderSphere;
import net.sector.collision.Scene;
import net.sector.effects.Effects;
import net.sector.entities.orbs.EntityOrbArtifact;
import net.sector.entities.player.EntityPlayerShip;
import net.sector.util.DeltaDoubleDeg;
import net.sector.util.Log;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Entity object: something that moves, reacts to collisions and does other cool
 * stuff.
 * 
 * @author MightyPork
 */
public abstract class Entity implements IPhysEntity, Comparable<Entity> {

	public static final IDamageable NO_SOURCE = new NullDamageSource();

	private int artifacts = 0;

	/** Delay before two following explosions. */
	protected static final long ExplodeCooldown = Constants.FPS_UPDATE / 3;

	/** Counts down until next explosion can be added == 0 */
	public long explodeCooldown = 0;

	public double healthMul = 1;

	/** Global movement disabled */
	private boolean globalMovement = true;

	/** Collide priority, entity with higher number handles collision. */
	public int collidePriority = 0;

	/** Speed limit */
	public double MAX_SPEED = 0.3;

	/** points added to counter when killed by player */
	public int scoreValue = 0;

	/** health used to add damage */
	public double health = 1;

	/** time left before death (in update ticks) */
	public int lifetime = Constants.FPS_UPDATE * 5;

	/** flag that entity is dead */
	protected boolean isDead = false;

	/** Motion - number of units to move per update tick */
	public Vec motion = new Vec();

	/** rotation vector for glRotate - the axis */
	public Vec rotDir = new Vec();

	/** Y rot angle */
	public DeltaDoubleDeg rotAngle = new DeltaDoubleDeg(0);

	/** Entity mass, to be used in reaction calculations */
	public double mass = 1.0;

	/** the scene */
	public Scene scene;

	/** Primary entity collider */
	public ColliderSphere collider;

	public double effectEmpTicks;
	public double effectFireTicks;
	public IDamageable fireSource = NO_SOURCE;

	/** RNG */
	public static Random rand = new Random();

	/**
	 * Get entity type
	 * 
	 * @return entity type
	 */
	@Override
	public abstract EEntity getType();

	/**
	 * Add artifact to this entity, dropped on death.
	 * 
	 * @param artifacts artifacts
	 */
	public final void addArtifacts(int artifacts) {
		this.artifacts += artifacts;
	}

	/**
	 * Get if entity has an artifact.
	 * 
	 * @return has artifact.
	 */
	public final int getArtifacts() {
		return artifacts;
	}

	/**
	 * Remove artifact, if any, from this entity.
	 */
	public final void removeArtifacts() {
		artifacts = 0;
	}

	/**
	 * Set global movement
	 * 
	 * @param flag global movement enabled; False if entity is player ship /
	 *            player's shot / boss
	 * @return this
	 */
	public final Entity setGlobalMovement(boolean flag) {
		globalMovement = flag;
		return this;
	}

	/**
	 * Get if has global movement
	 * 
	 * @return
	 */
	public boolean hasGlobalMovement() {
		return globalMovement;
	}

	@Override
	public final Coord getPos() {
		return collider.pos;
	}

	@Override
	public final Vec getMotion() {
		return motion;
	}

	@Override
	public final double getMass() {
		return mass;
	}

	@Override
	public boolean isDead() {
		return isDead;
	}

	@Override
	public final double getSpeed() {
		return motion.size();
	}

	@Override
	public final void setMotion(Vec newMotion) {
		motion.setTo(newMotion);
	}

	/**
	 * Set entity dead
	 */
	@Override
	public final void setDead() {
		isDead = true;
	}

	/**
	 * Assign scene to entity. Called when entity is added to scene.
	 * 
	 * @param scene the scene assigned
	 */
	@Override
	public final void setScene(Scene scene) {
		this.scene = scene;
		onAddedToScene();
	}

	/**
	 * Check if this entity collides with other entity
	 * 
	 * @param other the other entity
	 * @return does collide
	 */
	public boolean collidesWith(Entity other) {
		ColliderSphere c = getColliderFor(other.collider);
		if (c == null) return false;
		return c.collidesWith(other.collider);
	}

	/**
	 * React to collision with other entity (eg. bullet hitting asteroid)
	 * 
	 * @param hitBy
	 */
	public final void react(Entity hitBy) {
		onImpact(hitBy);
	}

	/**
	 * Called right after entity was added to scene.
	 */
	public void onAddedToScene() {}

	/**
	 * Handle collision and do reaction here.
	 * 
	 * @param hitBy
	 */
	public abstract void onImpact(Entity hitBy);

	/**
	 * Default reaction on impact.
	 * 
	 * @param hitBy
	 */
	public final void defaultOnImpact(Entity hitBy) {
		try {
			if (hitBy.isDead()) return;

			ColliderSphere mycol = getColliderFor(hitBy.collider);
			ColliderSphere ocol = hitBy.getColliderFor(this.collider);
			if (mycol == null || ocol == null) return;

			Vec move = getPos().vecTo(hitBy.getPos());

			Coord midpoint = getPos().add(move.norm(mycol.radius));

			if (hitBy.getType() == EEntity.PLAYER && !((EntityPlayerShip) hitBy).body.isShieldRunning()) {

				//move.scale_ip(0.2);

			}


			move.norm_ip((mycol.radius + ocol.radius) - mycol.getPos().distTo(ocol.getPos()));

			hitBy.getPos().add_ip(move.scale(0.3));
			getPos().add_ip(move.scale(0.3).neg());

			//this.motion.offset_ip(move.neg().scale(40));		

			Vec added = move.scale(1 / hitBy.mass);


			hitBy.getMotion().add_ip(added);
			getMotion().add_ip(move.neg().scale(1 / mass));

			hitBy.getMotion().add_ip(getMotion().scale((1 / hitBy.mass) * 0.1));
			getMotion().add_ip(hitBy.getMotion().scale((1 / mass) * 0.1));

			double damageGot = hitBy.mass * hitBy.getSpeed();
			if (!Double.isNaN(damageGot)) addDamage(hitBy, damageGot);
			double damageGiven = mass * getSpeed();
			if (!Double.isNaN(damageGiven)) hitBy.addDamage(this, damageGiven);


			if (!isDead) {
				explode(midpoint, 0.01, false);
			}
		} catch (Throwable t) {
			Log.e(t);
		}
	}

	/**
	 * Explode, if not cooled down yet, do nothing.
	 * 
	 * @param pos position of explosion
	 * @param strength strength of explosion
	 * @param shards has shards
	 */
	public final void explode(Coord pos, double strength, boolean shards) {
		if (!Utils.canSkipRendering(pos) && explodeCooldown == 0) {
			Effects.addExplosion(scene.particles, pos, getMotion(), strength, shards, hasGlobalMovement());
			explodeCooldown = ExplodeCooldown;
		}
	}

	/**
	 * Explode, ignore explodeCooldown
	 * 
	 * @param pos position of explosion
	 * @param strength strength of explosion
	 * @param shards has shards
	 */
	public final void explodeForce(Coord pos, double strength, boolean shards) {
		if (!Utils.canSkipRendering(pos)) {
			Effects.addExplosion(scene.particles, pos, getMotion(), strength, shards, hasGlobalMovement());
			explodeCooldown += ExplodeCooldown;
		}
	}

	public boolean allowVerticalMovement() {
		return false;
	}

	private Coord posBackup = null;
	private Vec motionBackup = null;

	/**
	 * Update entity position and other things. Called each update tick.
	 */
	public final void update() {

		if (posBackup == null) posBackup = getPos().copy();
		if (motionBackup == null) motionBackup = getMotion().copy();

		if (!allowVerticalMovement()) {
			getPos().setY_ip(0);
			getMotion().setY_ip(0);
		}


		if (effectEmpTicks > 0) effectEmpTicks -= 1 * Constants.SPEED_MUL;
		if (effectFireTicks > 0) effectFireTicks -= 1 * Constants.SPEED_MUL;


		getPos().pushLast();
		rotAngle.pushLast();

		fixNans();

		if (explodeCooldown > 0) explodeCooldown--;
		getPos().add_ip(motion.mul(Constants.SPEED_MUL));

		if (hasGlobalMovement()) {
//			if(!(this instanceof EntityAsteroid))System.out.println("Global movement of: "+getClass().getSimpleName());
			getPos().add_ip(scene.getGlobalMovement().mul(Constants.SPEED_MUL));
		}

		if (lifetime > 0) {
			lifetime--;
			if (lifetime == 0) {
				setDead();
				return;
			}
		}

		if (isEmpParalyzed()) {
			if (rand.nextInt(5) == 0)
				Effects.addEMPExplosion(scene.particles, getPos(), getMotion(), 1.6 * collider.radius, hasGlobalMovement(), false);
		}

		if (isOnFire()) {
			if (rand.nextInt(3) == 0) {
				Effects.addFireBurst(scene.particles, getPos(), getMotion(), collider.radius * 0.9, 6, hasGlobalMovement(), false);
			}

			addDamage(fireSource, 0.09 * Constants.SPEED_MUL * getFireSensitivity());
		}

		if (!isDead) onUpdate();

		double sp = getSpeed();
		if (sp > MAX_SPEED) getMotion().norm_ip(MAX_SPEED);

		fixNans();

		getPos().update();

	}

	private void fixNans() {
		boolean correction = false;
		Coord pos = getPos().copy();

		if (Double.isNaN(pos.x) || Double.isNaN(pos.y) || Double.isNaN(pos.z)) {
			correction = true;

			getPos().x = Calc.fixNan(pos.x, posBackup.x);
			getPos().y = Calc.fixNan(pos.y, posBackup.y);
			getPos().z = Calc.fixNan(pos.z, posBackup.z);

			getPos().pushLast();
			getPos().update();
		}

		if (correction) {
			Log.f3("\n!!! Correction: position of " + Calc.className(this) + ":\n" + pos + " -> " + getPos());
		}



		correction = false;

		Vec motion = getMotion().copy();

		if (Double.isNaN(motion.x) || Double.isNaN(motion.y) || Double.isNaN(motion.z)) {
			correction = true;

			getMotion().x = Calc.fixNan(motion.x, motionBackup.x);
			getMotion().y = Calc.fixNan(motion.y, motionBackup.y);
			getMotion().z = Calc.fixNan(motion.z, motionBackup.z);

			getMotion().pushLast();
			getMotion().update();
		}

		if (correction) {
			Log.f3("\n!!! Correction: motion of " + Calc.className(this) + ":\n" + motion + " -> " + getMotion());
		}

		posBackup.setTo(getPos());
		motionBackup.setTo(getMotion());
	}

	/**
	 * Get if is EMP paralyzed (unable to move,shootm etc)
	 * 
	 * @return is emp paralyzed
	 */
	public final boolean isEmpParalyzed() {
		return effectEmpTicks > 0;
	}

	/**
	 * Get if is on fire
	 * 
	 * @return is on fire
	 */
	public final boolean isOnFire() {
		return effectFireTicks > 0;
	}

//	/**
//	 * Get if this entity is electronic and affected by EMP missiles
//	 * 
//	 * @return is EMP sensitive
//	 */
//	public abstract boolean isEmpSensitive();

	/**
	 * Get EMP sensitivity (1 is normal, 0 is EMP-protected)
	 * 
	 * @return EMP sensitivity
	 */
	public abstract double getEmpSensitivity();

	/**
	 * Get fire sensitivity (1 is full, 0 is fire-protected)
	 * 
	 * @return fire sensitivity
	 */
	public abstract double getFireSensitivity();

	/**
	 * Get fire flammability (1 is full, 0 is fire-protected) - how much fire
	 * can be added by a fireball.
	 * 
	 * @return fire sensitivity
	 */
	public abstract double getFireFlammability();

	/**
	 * Add EMP ticks
	 * 
	 * @param ticks
	 */
	public final void addEmp(double ticks) {
		effectEmpTicks += ticks * getEmpSensitivity();
	}

	/**
	 * Add fire ticks
	 * 
	 * @param ticks
	 */
	public final void addFire(IDamageable source, double ticks) {
		effectFireTicks += ticks * getFireFlammability();
		fireSource = source;
	}

	/**
	 * Called each update tick, for position update and AI.
	 */
	public abstract void onUpdate();

	/**
	 * Called when entity dies - for explosion effects etc.
	 */
	public abstract void onDeath();

	/**
	 * Render this entity
	 * 
	 * @param delta
	 */
	public abstract void render(double delta);

	/** Entity which gave last damage (for scoring) */
	protected IDamageable lastDamageSource = null;


	@Override
	public void addDamage(IDamageable source, double points) {
		if (isDead()) return;
		lastDamageSource = source;
		health -= points / healthMul;
		if (health <= 0) {
			setDead();
			health = 0;

			if (artifacts > 0) {
				spawnArtifact(artifacts);
				removeArtifacts();
			}

			onDeath();
		}
	}

	/**
	 * Do spawn artifact at ship pos (on death)
	 */
	public void spawnArtifact(int points) {
		scene.add(new EntityOrbArtifact(getPos(), points));
	}


	/**
	 * Check if this entity belongs to a zone in Scene - zone map.<br>
	 * Typically done by calculating lowest and highest Z coordinate of the
	 * entity and comparing them to the boundaries.
	 * 
	 * @param zFrom start z
	 * @param zTo end z
	 * @return belongs to zone (at least partially)
	 */
	public boolean belongsToZone(double zFrom, double zTo) {
		return Calc.inRange(collider.pos.z, zFrom - collider.radius - 0.5, zTo + collider.radius + 0.5);
	}

	@Override
	public void setPos(Coord pos) {
		collider.pos.setTo(pos);
	}

	@Override
	public void setMaxSpeed(double maxSpeed) {
		MAX_SPEED = maxSpeed;
	}

	@Override
	public Scene getScene() {
		return scene;
	}

	@Override
	public double getRadius() {
		return collider.radius;
	}

	@Override
	public ColliderSphere getColliderFor(Collider hitBy) {
		return collider;
	}

	/**
	 * Get score value when killed by player
	 * 
	 * @return score points
	 */
	public int getScoreValue() {
		return scoreValue;
	}

	/**
	 * Set entity score value
	 * 
	 * @param scoreValue score value
	 */
	public void setScoreValue(int scoreValue) {
		this.scoreValue = scoreValue;
	}

	/**
	 * Get health remaining
	 * 
	 * @return points of health remaining
	 */
	@Override
	public double getHealth() {
		return health;
	}

	/**
	 * Get health max
	 * 
	 * @return points of health remaining
	 */
	@Override
	public abstract double getHealthMax();

	/**
	 * Set health - if used to count damage.<br>
	 * Modular ships don't use this.
	 * 
	 * @param health
	 */
	public void setHealth(double health) {
		this.health = health;
	}

	/**
	 * Get remaining life in update ticks
	 * 
	 * @return lifetime life time remaining
	 */
	public int getLifetime() {
		return lifetime;
	}

	/**
	 * Set initial life time
	 * 
	 * @param lifetime ticks of life
	 */
	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	@Override
	public Vec getRotDir() {
		return rotDir;
	}

	@Override
	public DeltaDoubleDeg getRotAngle() {
		return rotAngle;
	}


	/**
	 * Compare by Z position, sorting for particle rendering.
	 */
	@Override
	public int compareTo(Entity o) {
		if (this == o) return 0;
		return Double.valueOf(getPos().z).compareTo(o.getPos().z);
	}

	/**
	 * heal this entity
	 * 
	 * @param add health points to add
	 */
	public void addHealth(double add) {
		this.health = Calc.clampd(this.health + add, 0, getHealthMax());
	}
}
