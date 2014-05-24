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
import net.sector.level.ship.modules.pieces.Piece;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;
import net.sector.util.Log;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class EntityOrbArtifact extends EntityNavigable {

	private double scale = 0.25;
//	protected double rotSpeed = 2;

	private static RenderModel model = Models.orbArtifact;
	private double scaleRender = 0.5;

	private Coord target;

	private int discoveryPoints;


	public EntityOrbArtifact(Coord pos, int artifacts) {
		super(pos);
		this.discoveryPoints = artifacts;

		setDriver(SuperContext.basicDrivers.getDriver("powerup_artifact"));

		fullMoveSpeed = 0.06;

		this.target = new Coord(-1 + rand.nextDouble() * 2, 0, -1);

		this.scoreValue = 0;
		this.health = 1000;
		this.mass = 0.6;
		this.collidePriority = 2003;

		this.lifetime = Constants.FPS_UPDATE * 5000;
		this.motion.setTo(0, 0, -0.003);

		this.rotDir.setTo(-1 + rand.nextDouble() * 2, -1 + rand.nextDouble() * 2, -1 + rand.nextDouble() * 2);
		this.rotAngle.set(rand.nextDouble() * 360);
		//this.rotSpeed = -3 + rand.nextDouble() * 6;

		this.collider = new ColliderSphereFake(pos, scale);
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
					Log.f3("Adding artifact to player.");
					if (discoveryPoints > 0) {
						sh.cursor.addArtifact(discoveryPoints);
						discoveryPoints = 0;
					}


					sh.body.energySystem.fill();
					sh.body.shieldSystem.fill();

					for (Piece p : sh.body.allPieces) {
						if (!p.isDead) {
							p.addHealth(p.getHealthMax() / 3);
						}
					}


					Sounds.powerup2.playEffect(1f, 0.5f, false);
					Effects.addOrbBurst(scene.particles, getPos(), getMotion(), scaleRender * 1.5, 200, 1, false, true);
					setDead();
					return;
				}
			}
		}
	}


	@Override
	public void onAddedToScene() {
		Sounds.appear.playEffect(1f, 0.2f, false);
	}

	public void addEffect() {
		Effects.addOrbBurst(scene.particles, getPos(), getMotion(), scaleRender * 1.5, 20, 1, false, false);
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
	public void addDamage(IDamageable source, double points) {
		// dont add damage
	}

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
		System.out.println("Artifact died. That sucks.");
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

}
