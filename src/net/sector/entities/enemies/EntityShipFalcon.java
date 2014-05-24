package net.sector.entities.enemies;


import net.sector.effects.Effects;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.shots.EntityLaser;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.models.PhysModel;
import net.sector.util.Utils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Enemy ship entity
 * 
 * @author MightyPork
 */
public class EntityShipFalcon extends EntityNavigable {

	private static PhysModel shipModel = Models.enemyFalcon;

	private static final double mMoveSpeed = 0.15;

	/**
	 * Enemy ship
	 * 
	 * @param scale
	 * @param pos
	 */
	public EntityShipFalcon(double scale, Coord pos) {
		super(shipModel, mMoveSpeed, scale, pos);
		setDefaultDriver();
	}

	@Override
	public double getEmpSensitivity() {
		return 0.3;
	}

	@Override
	public double getFireSensitivity() {
		return 0.8;
	}

	/**
	 * Enemy ship, scale=1
	 * 
	 * @param pos
	 */
	public EntityShipFalcon(Coord pos) {
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
		setDriver(SuperContext.basicDrivers.getDriver("falcon"));
	}

//	@Override
//	public void setShipLevel(int level) {
//		this.level = level;
//		super.adjustForScale(1 + level * 0.5);
//	}

	@Override
	public void shootOnce(int gunIndex) {
		if (Utils.canSkipRendering(getPos())) return;

		Coord left = getShotPos(-2, 2);
		Coord right = getShotPos(2, 2);

		Vec motion = getGunShotDir(gunIndex);
		RGB red = RGB.PURPLE;

		if (collider.pos.z > 0) {
			scene.add(new EntityLaser(left, motion, this, red, 5));
			scene.add(new EntityLaser(right, motion, this, red, 5));
		}
	}

	@Override
	public Vec getGunShotDir(int gunIndex) {
		return getRotateAimVector();
	}

	@Override
	public void onImpact(Entity hitBy) {
		defaultOnImpact(hitBy);
	}

	@Override
	public void onDeath() {
		explodeForce(getPos(), mass*2, true);
	}

	@Override
	public double getHealthMax() {
		return model.getHealth(scale);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}

}
