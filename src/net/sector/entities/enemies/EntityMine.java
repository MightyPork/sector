package net.sector.entities.enemies;


import java.util.Set;

import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.orbs.EntityOrbShield;
import net.sector.entities.player.EntityPlayerShip;
import net.sector.entities.shots.EntityShotBase;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.models.PhysModel;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;



/**
 * Mine entity
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EntityMine extends EntityNavigable {

	private double scale = 1;
	private double healthMax;

	private static PhysModel shipModel = Models.spaceMine;

	private static final double mMoveSpeed = 0.00001;

	@Override
	public boolean hasGlobalMovement() {
		return true;
	}

	/**
	 * Enemy ship
	 * 
	 * @param scale
	 * @param pos
	 */
	public EntityMine(double scale, Coord pos) {
		super(shipModel, mMoveSpeed, scale, pos);
		setDefaultDriver();
		this.rotDir.setTo(0, 1, 0);
		this.rotAngle.set(rand.nextDouble() * 360);
		this.collidePriority = 2000;
		this.lifetime = -1;
		this.MAX_SPEED = 0.2 * (0.3 / mass);
	}

	/**
	 * Enemy ship, scale=1
	 * 
	 * @param pos
	 */
	public EntityMine(Coord pos) {
		super(shipModel, mMoveSpeed, 1, pos);
		setDefaultDriver();
		this.rotDir.setTo(0, 1, 0);
		this.rotAngle.set(rand.nextDouble() * 360);
		this.collidePriority = 2000;
		this.lifetime = -1;
		this.MAX_SPEED = 0.2 * (0.3 / mass);
	}

	private void setDefaultDriver() {
		setDriver(SuperContext.basicDrivers.getDriver("mine"));
	}


	@Override
	public double getHealthMax() {
		return healthMax;
	}

	@Override
	public void onImpact(Entity hitBy) {
		if (hitBy == null) return;

		boolean dead = false;
		if (hitBy.getType() == EEntity.SHOT_BAD || hitBy.getType() == EEntity.SHOT_GOOD) {
			if (rand.nextInt(2) == 0) {
				dead = true;
				if (((EntityShotBase) hitBy).scoreCounter != null) {
					((EntityShotBase) hitBy).scoreCounter.addScore(scoreValue);
				}

				boom(hitBy);

			}


		} else {
			dead = true;
			boom(hitBy);
		}

		if (!hitBy.isDead()) defaultOnImpact(hitBy);

		if (dead) setDead();
	}

	public void boom(Entity hitBy) {
		explodeForce(getPos(), 30 * scale, true);

		double range = 6;
		double distMultiplier = 7; // to make it fade away faster

		double damage = 50 * scale;

		if (hitBy != null) {
			if (!(hitBy instanceof EntityPlayerShip)) {
				hitBy.addDamage(this, damage);
			} else {
				((EntityPlayerShip) hitBy).piecesAddDamageSquare(collider, damage, distMultiplier, range);
			}
		}

		Set<Entity> ents = getScene().getEntitiesInRange(getPos(), range);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == this) continue;
				if (e.isDead()) continue;
				if (e instanceof EntityMine) continue;
				if (e == hitBy) continue;

				double dist = e.getPos().distTo(getPos()) - e.getRadius() - getRadius();

				dist *= distMultiplier;

				if (dist < 1) dist = 1;

				if (e.getType() == EEntity.PLAYER) {
					((EntityPlayerShip) e).piecesAddDamageSquare(collider, damage, distMultiplier, range);
				}

				e.addDamage(this, damage / (dist * dist));
				e.getMotion().add_ip(getPos().vecTo(e.getPos()).norm(0.1 / (dist * dist)));
			}
		}
	}

	@Override
	public void addDamage(IDamageable source, double points) {
		super.addDamage(source, points);
	}

	@Override
	public void onDeath() {
		if (lastDamageSource.getType() == EEntity.SHOT_GOOD && rand.nextInt(4) == 0) {
			if (scene.playerShip.body.shieldSystem.getLoadRatio() < 1) {
				scene.add(new EntityOrbShield(getPos(), 400 * (0.6 + rand.nextDouble() * 0.7)));
			}
		}
	}

	@Override
	public EEntity getType() {
		return EEntity.MINE;
	}

	@Override
	public void shootOnce(int gunIndex) {}

	@Override
	public Vec getGunShotDir(int gunIndex) {
		return getMotion();
	}

}
