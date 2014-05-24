package net.sector.entities.enemies;


import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.shots.EntityPlasma;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.util.Utils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Enemy cube entity
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EntityShipCube extends EntityNavigable {

	private int type = 0;

	private static final double mMoveSpeed = 0.07;

	/**
	 * Enemy Cube
	 * 
	 * @param scale scale (1)
	 * @param pos center pos
	 * @param texture texture index 0-4
	 */
	public EntityShipCube(double scale, Coord pos, int texture) {
		super(Models.enemyCube[Calc.clampi(texture, 0, Models.enemyCube.length - 1)], mMoveSpeed, scale, pos);
		type = Calc.clampi(texture, 0, Models.enemyCube.length - 1);
		setDefaultDriver();
	}

	/**
	 * Enemy Cube scale=1
	 * 
	 * @param pos center pos
	 * @param texture texture index 0-4
	 */
	public EntityShipCube(Coord pos, int texture) {
		super(Models.enemyCube[Calc.clampi(texture, 0, Models.enemyCube.length - 1)], mMoveSpeed, 1, pos);
		type = Calc.clampi(texture, 0, Models.enemyCube.length - 1);
		setDefaultDriver();
	}


	/**
	 * Enemy Cube scale=1
	 * 
	 * @param pos center pos
	 * @param texture texture index 0-4
	 */
	public EntityShipCube(Coord pos) {
		this(pos, 0);
	}

	@Override
	public void setShipVariant(int variant) {
		type = Calc.clampi(variant, 0, Models.enemyCube.length - 1);
		setModel(Models.enemyCube[Calc.clampi(type, 0, Models.enemyCube.length - 1)]);
	}

	private void setDefaultDriver() {
		setDriver(SuperContext.basicDrivers.getDriver("cube_snake"));
	}

//	@Override
//	public void setShipLevel(int level) {
//		this.level = level;
//		super.adjustForScale(1 + level * 0.5);
//	}

	@Override
	public void shootOnce(int gunIndex) {
		if (Utils.canSkipRendering(getPos())) return;

		Coord pos;
		Vec dir = getGunShotDir(gunIndex);
		if (dir == null) return;

		pos = getPos();


		EntityPlasma shot = new EntityPlasma(pos, dir, this, gunIndex == 100 ? 3 : 1 + 0.3 * scale);
		shot.setColor(colors[type]).setScale(0.1 + 0.05 * scale);
		scene.add(shot);
	}

	RGB[] colors = { new RGB(0.0, 1.0, 0.0), new RGB(1.0, 0.0, 0.0), new RGB(0.2, 0.5, 1.0), new RGB(0.6, 0.0, 1.0), new RGB(1.0, 1.0, 0.0), };


	@Override
	public Vec getGunShotDir(int gunIndex) {
		if (gunIndex == 0) return getVectorToPlayer();
		if (gunIndex == 100) return getVectorToClosestAsteroid();
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
	
	@Override
	public double getEmpSensitivity() {
		return 0.7;
	}

	@Override
	public double getFireSensitivity() {
		return 0.6;
	}
}
