package net.sector.entities.orbs;


import static org.lwjgl.opengl.GL11.*;

import java.util.Set;

import net.sector.Constants;
import net.sector.collision.ColliderSphereFake;
import net.sector.effects.Effects;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.IDamageable;
import net.sector.entities.player.EntityPlayerShip;
import net.sector.level.SuperContext;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class EntityOrbShield extends EntityNavigable {

	private double scale = 0.25;
//	protected double rotSpeed = 2;

	private static RenderModel model = Models.orbShield;
	private double scaleRender = 0.25;

	private Coord target;

	private double shieldPoints = 100;


	public EntityOrbShield(Coord pos, double shieldPoints) {
		super(pos);

		shieldPoints = Calc.clampd(shieldPoints, 0, 650);

		setDriver(SuperContext.basicDrivers.getDriver("powerup_shield"));

		fullMoveSpeed = 0.06;

		this.target = new Coord(-1 + rand.nextDouble() * 2, 0, -1);

		this.scaleRender *= (shieldPoints / 700);

		this.scoreValue = 0;
		this.health = 1000;
		this.mass = 0.6;
		this.collidePriority = 2002;

		this.lifetime = Constants.FPS_UPDATE * 500;
		this.motion.setTo(0, 0, -0.003);

		this.rotDir.setTo(-1 + rand.nextDouble() * 2, -1 + rand.nextDouble() * 2, -1 + rand.nextDouble() * 2);
		this.rotAngle.set(rand.nextDouble() * 360);
		//this.rotSpeed = -3 + rand.nextDouble() * 6;

		this.collider = new ColliderSphereFake(pos, scale);

		this.shieldPoints = shieldPoints;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		motion.norm_ip(fullMoveSpeed);
		addEffect();


		Set<Entity> ents = scene.getEntitiesInRange(getPos(), 1);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == scene.playerShip) {
					EntityPlayerShip sh = (EntityPlayerShip) e;
					sh.body.shieldSystem.addShieldPoints(shieldPoints);
					Sounds.powerup1.playEffect(1f, 0.4f, false);
					Effects.addOrbBurst(scene.particles, getPos(), getMotion(), 0.4, 60, 0, false, true);
					setDead();
					return;
				}
			}
		}
	}


	@Override
	public void onAddedToScene() {}

	public void addEffect() {
		Effects.addOrbBurst(scene.particles, getPos(), getMotion(), scaleRender, 4, 0, false, false);
	}

	@Override
	public void render(double delta) {
		glLoadIdentity();

		Coord p = getPos().getDelta(delta);
		glTranslated(p.x, p.y, -p.z);
		glRotated(rotAngle.delta(delta), rotDir.x, rotDir.y, rotDir.z);
		glScaled(scaleRender, scaleRender, scaleRender);

		model.render();
		TextureManager.unbind();
	}

	@Override
	public void addDamage(IDamageable source, double points) {}

	@Override
	public EEntity getType() {
		return EEntity.BONUS;
	}

	@Override
	public void onImpact(Entity hitBy) {
		defaultOnImpact(hitBy);
	}


	@Override
	public void onDeath() {
		System.out.println("Bonus died.");
	}

	@Override
	public boolean belongsToZone(double zFrom, double zTo) {
		return Calc.inRange(collider.pos.z, zFrom - 2 * collider.radius, zTo + 2 * collider.radius);
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
	public double getHealthMax() {
		return 10;
	}

	@Override
	public void setShipLevel(int level) {}

	@Override
	public void shootOnce(int gunIndex) {}

	@Override
	public Vec getGunShotDir(int gunIndex) {
		return getMotion();
	}

	@Override
	public double getHealthPercent() {
		return 100;
	}

	@Override
	public boolean hasGlobalMovement() {
		return true;
	}

}
