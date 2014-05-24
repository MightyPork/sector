package net.sector.entities.enemies;


import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.shots.EntityLaser2;
import net.sector.entities.shots.EntityPlasma;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.models.PhysModel;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Enemy ship entity
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EntityShipShark extends EntityNavigable {

	private static PhysModel shipModel = Models.enemyShark;

	private static final double mMoveSpeed = 0.14;

	/**
	 * Enemy ship
	 * 
	 * @param scale
	 * @param pos
	 */
	public EntityShipShark(double scale, Coord pos) {
		super(shipModel, mMoveSpeed, scale, pos);
		setDefaultDriver();
	}

	@Override
	public double getEmpSensitivity() {
		return 0.1;
	}

	@Override
	public double getFireSensitivity() {
		return 0.7;
	}

	/**
	 * Enemy ship, scale=1
	 * 
	 * @param pos
	 */
	public EntityShipShark(Coord pos) {
		super(shipModel, mMoveSpeed, 1, pos);
		setDefaultDriver();
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


	private void setDefaultDriver() {
		setDriver(SuperContext.basicDrivers.getDriver("shark"));
	}

//	@Override
//	public void setShipLevel(int level) {
//		this.level = level;
//		super.adjustForScale(1 + level * 0.5);
//	}

	@Override
	public void shootOnce(int gunIndex) {
		if (Utils.canSkipRendering(getPos())) return;

		Coord left = getShotPos(-2, 1);
		Coord right = getShotPos(2, 1);

		Vec motion = getGunShotDir(gunIndex);

		if (collider.pos.z > 0) {
			if (gunIndex == 0) {
				scene.add(new EntityPlasma(left, motion, this, 3 + 0.3 * scale).setGlobalMovement(false));
				scene.add(new EntityPlasma(right, motion, this, 3 + 0.3 * scale).setGlobalMovement(false));
			}
			if (gunIndex == 1) {
				scene.add(new EntityLaser2(left, motion, this, 3).setGlobalMovement(false));
				scene.add(new EntityLaser2(right, motion, this, 3).setGlobalMovement(false));
			}
		}
	}

	@Override
	public Vec getGunShotDir(int gunIndex) {
		if (gunIndex == 0) return getRotateAimVector(15);
		if (gunIndex == 1) return getRotateAimVector(10);
		return getMotion();
	}

	@Override
	public void onImpact(Entity hitBy) {
		defaultOnImpact(hitBy);
	}

	@Override
	public void onDeath() {
		explodeForce(getPos(), mass * 6, true);
	}

	@Override
	public double getHealthMax() {
		return model.getHealth(scale);
	}

}
