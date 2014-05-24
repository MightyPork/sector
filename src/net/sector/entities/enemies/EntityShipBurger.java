package net.sector.entities.enemies;


import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.shots.EntityPlasma;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.models.PhysModel;
import net.sector.util.Utils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Enemy burger entity
 * 
 * @author MightyPork
 */
public class EntityShipBurger extends EntityNavigable {
	private static PhysModel burgerModel = Models.enemyBurger;


	private static final double mMoveSpeed = 0.075;

	/**
	 * Enemy Burger
	 * 
	 * @param scale scale (1)
	 * @param pos center pos
	 */
	public EntityShipBurger(double scale, Coord pos) {
		super(burgerModel, mMoveSpeed, scale, pos);
		setDefaultDriver();
	}

	/**
	 * Enemy Burger scale=1
	 * 
	 * @param pos center pos
	 */
	public EntityShipBurger(Coord pos) {
		super(burgerModel, mMoveSpeed, 1, pos);
		setDefaultDriver();
	}

	private void setDefaultDriver() {
		setDriver(SuperContext.basicDrivers.getDriver("burger_zone"));
	}

	@Override
	public double getFireSensitivity() {
		return 0.8;
	}

//	@Override
//	public void setShipLevel(int level) {
//		this.level = level;
//		super.adjustForScale(1 + level * 0.5);
//	}

	@Override
	public void shootOnce(int gunIndex) {
		if (Utils.canSkipRendering(getPos())) return;
		Vec dir = getGunShotDir(gunIndex);
		if (dir == null) return;
		double slevel = 1 + 0.3 * scale;
		EntityPlasma shot = new EntityPlasma(getPos(), dir, this, gunIndex == 100 ? 3 : slevel);
		shot.setColor(new RGB(1, 0.6, 0.6, 1)).setScale(0.1 + 0.05 * scale);
		//shot.getMotion().add_ip(getMotion());
		scene.add(shot);
	}

	@Override
	public Vec getGunShotDir(int gunIndex) {
		// 0 to player
		// 1 in direction of rotation
		// other in dir of motion
		if (gunIndex == 0) return getVectorToPlayer();
		if (gunIndex == 1) return getRotateAimVector();
		if (gunIndex == 100) return getVectorToClosestAsteroid();

		return getMotion();
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
