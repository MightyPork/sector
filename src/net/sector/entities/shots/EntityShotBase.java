package net.sector.entities.shots;


import net.sector.Constants;
import net.sector.effects.particles.ParticleFire;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.IScoreCounter;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Rad;
import com.porcupine.math.Polar;


/**
 * Base for all shots
 * 
 * @author MightyPork
 */
public abstract class EntityShotBase extends Entity {

	public double shotDamage = 0.8;
	public double colliderScale = 0.1;
	public Entity origin = null;
	public IScoreCounter scoreCounter = null;
	public double shotSpeed = 0.5;

	public EntityShotBase(Coord pos, Vec motion, Entity origin, double shotSpeed) {
		this.shotSpeed = shotSpeed;
		this.scoreValue = 0;
		this.health = 5;
		this.lifetime = Constants.FPS_UPDATE * 10;
		this.motion.setTo(motion.norm(shotSpeed));

		Polar p = Polar.fromCoord(motion.x, motion.z);
		rotAngle.set(-90 + Rad.toDeg(p.angle));
		rotDir.setTo(0, 1, 0);

		this.origin = origin;

		if (origin instanceof IScoreCounter) this.scoreCounter = (IScoreCounter) origin;
	}

	@Override
	public final EEntity getType() {
		return (origin != null && origin.getType() == EEntity.PLAYER) ? EEntity.SHOT_GOOD : EEntity.SHOT_BAD;
	}

	@Override
	public void onImpact(Entity hitBy) {
		if (hitBy == origin) return;
		EEntity type = hitBy.getType();
		if (type != EEntity.SHOT_BAD && type != EEntity.SHOT_GOOD) {

			hitBy.addDamage(this, shotDamage);
			onHitTarget(hitBy);

			hitBy.getMotion().add_ip(motion.scale(Calc.clampd(mass, 0, 1)));

			if (hitBy.isDead() && scoreCounter != null) scoreCounter.addScore(hitBy.scoreValue);

			setDead();
		}
	}

	@Override
	public void onDeath() {}

	@Override
	public final boolean belongsToZone(double zFrom, double zTo) {
		return Calc.inRange(collider.pos.z, zFrom - 2 * collider.radius, zTo + 2 * collider.radius);
	}

	/**
	 * On target hit
	 * 
	 * @param target
	 */
	public void onHitTarget(Entity target) {
		scene.particles.add(new ParticleFire(collider.pos, target.motion, 0, false).setGlobalMovement(target.hasGlobalMovement()));
	}

	@Override
	public double getEmpSensitivity() {
		return 0;
	}

	@Override
	public double getFireFlammability() {
		return 0;
	}

	@Override
	public double getFireSensitivity() {
		return 0;
	}

	@Override
	public abstract void onUpdate();

	@Override
	public abstract void render(double delta);

	@Override
	public double getHealthMax() {
		return 100;
	}

}
