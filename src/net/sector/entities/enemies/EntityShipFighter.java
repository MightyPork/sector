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
public class EntityShipFighter extends EntityNavigable {

	private static PhysModel shipModel = Models.enemyFighter;
	private int level = 1;

	private static final double mMoveSpeed = 0.1;

	/**
	 * Enemy ship
	 * 
	 * @param scale
	 * @param pos
	 */
	public EntityShipFighter(double scale, Coord pos) {
		super(shipModel, mMoveSpeed, scale, pos);
		setDefaultDriver();
	}

	/**
	 * Enemy ship, scale=1
	 * 
	 * @param pos
	 */
	public EntityShipFighter(Coord pos) {
		super(shipModel, mMoveSpeed, 1, pos);
		setDefaultDriver();
	}


	private void setDefaultDriver() {
		setDriver(SuperContext.basicDrivers.getDriver("fighter"));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!Utils.canSkipRendering(getPos())) {
			Vec back = getRotateAimVector().neg();
			Coord pos = getShotPos(-0.23, -0.55);
			Effects.addEngineFire(getScene().particles, pos, back.norm(0.025), 2, 0, scale);
			pos = getShotPos(0.23, -0.55);
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

		Coord left = getShotPos(-0.3, 2);
		Coord right = getShotPos(0.3, 2);

		Vec motion = getGunShotDir(gunIndex);
		RGB red = RGB.RED;

		if (collider.pos.z > 0) {
			scene.add(new EntityLaser(left, motion, this, red, level));
			scene.add(new EntityLaser(right, motion, this, red, level));
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
		explodeForce(getPos(), mass, true);
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
