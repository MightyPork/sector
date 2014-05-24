package net.sector.entities.enemies;


import net.sector.effects.Effects;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.orbs.EntityOrbShield;
import net.sector.entities.shots.EntityLaser2;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.models.PhysModel;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Enemy ship entity
 * 
 * @author MightyPork
 */
public class EntityShipBird extends EntityNavigable {

	private static PhysModel shipModel = Models.enemyBird;

	private static final double mMoveSpeed = 0.11;

	/**
	 * Enemy ship
	 * 
	 * @param scale
	 * @param pos
	 */
	public EntityShipBird(double scale, Coord pos) {
		super(shipModel, mMoveSpeed, scale, pos);
		setDefaultDriver();
	}

	/**
	 * Enemy ship, scale=1
	 * 
	 * @param pos
	 */
	public EntityShipBird(Coord pos) {
		super(shipModel, mMoveSpeed, 1, pos);
		setDefaultDriver();
	}


	private void setDefaultDriver() {
		setDriver(SuperContext.basicDrivers.getDriver("bird"));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!Utils.canSkipRendering(getPos())) {
			Vec back = getRotateAimVector().neg();
			Coord pos = getShotPos(0, -0.45);
			Effects.addEngineFire(getScene().particles, pos, back.norm(0.025), 2, 0, scale);
		}
	}

//	@Override
//	public void setShipLevel(int level) {
//		this.level = level;
//		super.adjustForScale(1 + level * 0.5);
//	}

	@Override
	public void shootOnce(int gunIndex) {
		if (Utils.canSkipRendering(getPos())) return;


		Coord pos = getShotPos(0, 1);

		Vec motion = getGunShotDir(gunIndex);

		if (collider.pos.z > 0) {
			scene.add(new EntityLaser2(pos, motion, this, 1));
		}
	}

	@Override
	public Vec getGunShotDir(int gunIndex) {
		return getVectorToPlayer();
	}

	@Override
	public void onImpact(Entity hitBy) {
		defaultOnImpact(hitBy);
	}

	@Override
	public void onDeath() {
		explodeForce(getPos(), mass, true);

		if (lastDamageSource.getType() == EEntity.SHOT_GOOD && rand.nextInt(4) == 0) {
			if (scene.playerShip.body.shieldSystem.getLoadRatio() < 1) {
				scene.add(new EntityOrbShield(getPos(), 500 * (0.6 + rand.nextDouble() * 0.7)));
			}
		}
	}

	@Override
	public double getHealthMax() {
		return model.getHealth(scale);
	}

	@Override
	public void addDamage(IDamageable source, double points) {
		if (!isEmpParalyzed()) {
			if (source.getType() == EEntity.ENEMY) return;
			if (source.getType() == EEntity.NATURAL) return;
			if (source.getType() == EEntity.SHOT_BAD) return;
			if (source.getType() == EEntity.MINE) return;
		}

		super.addDamage(source, points);
	}

}
