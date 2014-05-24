package net.sector.entities.shots;


import java.util.Set;

import net.sector.Constants;
import net.sector.collision.ColliderSphere;
import net.sector.effects.Effects;
import net.sector.entities.Entity;
import net.sector.sounds.Sounds;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.PolarDeg;


public class EntityFireball extends EntityShotBase {

	private static final double SPEED = 0.35;

	private int level = 1;

	public EntityFireball(Coord pos, Vec speed, Entity origin, int techLevel) {
		super(pos, speed, origin, SPEED);
		this.collidePriority = 1001;
		this.mass = 0.3;
		this.lifetime = Constants.FPS_UPDATE * 7;
		this.collider = new ColliderSphere(pos, 0.30);
		this.health = 0.4 * techLevel * techLevel;

		this.shotDamage = 0.1 * Math.pow(techLevel, 1.2);
		this.level = techLevel;
	}

	@Override
	public void onUpdate() {
		PolarDeg polar = PolarDeg.fromCoordXZ(motion);
		polar.angle += -0.2 + rand.nextDouble() * 0.4;
		motion.setTo(polar.toCoordXZ());
		motion.norm_ip(shotSpeed);
		addEffect();
	}

	private static long lastSoundTime = 0;
	private static long lastExplTime = 0;

	@Override
	public void onAddedToScene() {
		if (System.currentTimeMillis() - lastSoundTime > 100) {
			Sounds.shot_fireball.playEffect(1f, 0.2f, false, getPos().setY(Constants.LISTENER_POS.y - 2));
			lastSoundTime = System.currentTimeMillis();
		}
	}

	public void addEffect() {
		if (rand.nextInt(6) == 0) {//+0.03*(level)
			Effects.addExplosion(scene.particles, getPos(), getMotion(), 0.02 + 0.03 * (level), false, false, false);
		}
	}

	@Override
	public void render(double delta) {}

	@Override
	public void onHitTarget(Entity target) {
		if (System.currentTimeMillis() - lastExplTime > 150) {
			Effects.addExplosion(scene.particles, collider.pos, target.getMotion(), 6 * level, true, target.hasGlobalMovement());
			lastExplTime = System.currentTimeMillis();
		}


		Set<Entity> ents = scene.getEntitiesInRange(getPos(), 0.8 * level);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == this) continue;
				if (e != target) e.addDamage(this, shotDamage);
				e.addFire(origin, level * Constants.FPS_UPDATE * 0.3);
			}
		}
	}

}
