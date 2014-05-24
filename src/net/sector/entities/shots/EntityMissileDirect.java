package net.sector.entities.shots;


import static org.lwjgl.opengl.GL11.*;

import java.util.Set;

import net.sector.Constants;
import net.sector.collision.ColliderSphere;
import net.sector.effects.Effects;
import net.sector.entities.Entity;
import net.sector.models.Models;
import net.sector.models.PhysModel;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;
import net.sector.util.DeltaDoubleDeg;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class EntityMissileDirect extends EntityShotBase {

	private static final double SPEED = 0.4;

	private double scale = 0.25;
	protected DeltaDoubleDeg rot = new DeltaDoubleDeg(0);
	protected double rotSpeed = 2;

	protected static PhysModel model = Models.rocketThin;
	protected double scaleRender = model.renderScale;

	private int level;


	public EntityMissileDirect(Coord pos, Vec speed, Entity origin, int techLevel) {
		super(pos, speed, origin, SPEED);
		this.collidePriority = 1001;
		this.mass = 0.3;
		this.lifetime = Constants.FPS_UPDATE * 7;
		this.collider = new ColliderSphere(pos, scale);
		this.health = 0.4 * techLevel * techLevel;

		this.shotDamage = 4 * Math.pow(techLevel, 1.5);
		this.level = techLevel;
	}

	@Override
	public void onUpdate() {
		motion.norm_ip(shotSpeed);
		rot.pushLast();
		rot.add(rotSpeed);
		//collider.pos.add_ip(motion);
		addEffect();

	}

	private static long lastSoundTime = 0;

	@Override
	public void onAddedToScene() {
		if (System.currentTimeMillis() - lastSoundTime > 50) {
			Sounds.rocket.playEffect(1f, 0.4f, false, getPos().setY(Constants.LISTENER_POS.y - 2));
			lastSoundTime = System.currentTimeMillis();
		}
	}

	public void addEffect() {
		Coord firePos = getPos().add(getMotion().neg().norm(collider.radius));
		motion.norm_ip(shotSpeed);

		Effects.addEngineFire(scene.particles, firePos, getMotion(), 2);
	}

	@Override
	public void render(double delta) {
		glLoadIdentity();

		Coord p = getPos().getDelta(delta);
		glTranslated(p.x, p.y, -p.z);
		glRotated(rotAngle.delta(delta), rotDir.x, rotDir.y, rotDir.z);
		glRotated(rot.delta(delta), 0, 0, 1);
		glScaled(scaleRender, scaleRender, scaleRender);

		model.render();
		TextureManager.unbind();
	}

	@Override
	public void onHitTarget(Entity target) {
		Effects.addExplosion(scene.particles, collider.pos, target.getMotion(), 10 * level, true, target.hasGlobalMovement());

		Set<Entity> ents = scene.getEntitiesInRange(getPos(), 3);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == this) continue;
				if (e != target) {
					e.addDamage(this, shotDamage);
					e.getMotion().add_ip(motion.scale(Calc.clampd(mass, 0, 1)));
					if (e.isDead() && scoreCounter != null) scoreCounter.addScore(e.scoreValue);
				}
				
				e.addFire(origin, level * Constants.FPS_UPDATE * 0.5);
			}
		}
	}

}
