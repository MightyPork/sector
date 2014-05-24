package net.sector.entities.natural;


import static org.lwjgl.opengl.GL11.*;
import net.sector.Constants;
import net.sector.collision.ColliderSphere;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.orbs.EntityOrbShield;
import net.sector.models.Models;
import net.sector.models.PhysModel;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;



/**
 * Asteroid entity
 * 
 * @author MightyPork
 */
public class EntityAsteroid extends EntityNatural {


	private int texture = 0;
	/** Rotation speed */
	protected double rotSpeed = 0;

	private PhysModel rock;

	private double renderScale = 0;
	private double scale = 1;
	private double healthMax;

	/**
	 * Asteroid entity
	 * 
	 * @param scale asteroid scale
	 * @param pos asteroid center position
	 * @param motion asteroid motion
	 * @param texture asteroid texture index
	 */
	public EntityAsteroid(double scale, Coord pos, Vec motion, int texture) {

		this.texture = texture;
		this.rotDir.setTo(-1 + rand.nextDouble() * 2, -1 + rand.nextDouble() * 2, -1 + rand.nextDouble() * 2);
		this.rotAngle.set(rand.nextDouble() * 360);
		this.rotSpeed = -5 + rand.nextDouble() * 10;

		this.collidePriority = 0;

		this.scale = scale;
		rock = Models.pickAsteroidOfType(texture);

		this.renderScale = rock.renderScale * scale;

		this.healthMax = this.health = rock.health * scale;
		this.lifetime = -1;
		this.mass = rock.getMass(scale);
		collider = new ColliderSphere(pos, rock.colliderRadius * scale);
		this.motion.setTo(motion);
		this.scoreValue = (int) Math.round(rock.getScore(scale));

		this.MAX_SPEED = 0.2 * (0.3 / mass);

		setGlobalMovement(true);
	}

	@Override
	public double getHealthMax() {
		return healthMax;
	}

	//private static Object3D model = new Object3D("res/models/asteroid03.obj", true);
	/**
	 * Asteroid entity
	 * 
	 * @param scale asteroid scale
	 * @param pos asteroid position
	 * @param motion asteroid motion
	 */
	public EntityAsteroid(double scale, Coord pos, Vec motion) {
		this(scale, pos, motion, rand.nextInt(Models.rockTypes.length));
	}


	@Override
	public void onImpact(Entity hitBy) {
		defaultOnImpact(hitBy);
		this.rotSpeed = Calc.clampd(this.rotSpeed + rand.nextGaussian(), -5, 5);

		if (hitBy instanceof EntityAsteroid) {
			((EntityAsteroid) hitBy).rotSpeed = Calc.clampd(((EntityAsteroid) hitBy).rotSpeed + rand.nextGaussian(), -5, 5);
		}
	}

	@Override
	public void onUpdate() {
		rotAngle.add(rotSpeed * Constants.SPEED_MUL);
	}

	@Override
	public void render(double delta) {
		glPushMatrix();

		glLoadIdentity();
		Coord p = getPos().getDelta(delta);
		glTranslated(p.x, p.y, -p.z);
		glRotated(rotAngle.delta(delta), rotDir.x, rotDir.y, rotDir.z);
		glScaled(renderScale, renderScale, renderScale);
		rock.model.render();

		glPopMatrix();

	}

	@Override
	public boolean belongsToZone(double zFrom, double zTo) {
		return Calc.inRange(collider.pos.z, zFrom - collider.radius, zTo + collider.radius);
	}

	@Override
	public void onDeath() {
		explodeForce(getPos(), getRadius() * 14, true);

		//explodeForce(getPos(), mass / 2, true);

		if (scale > 0.4) {
			int pieces = 2 + rand.nextInt(7);

			double vol = Calc.sphereGetVolume(scale) * 0.4;

			double approxPerPart = vol / pieces;

			double[] volumes = new double[pieces];

			for (int i = 0; i < pieces; i++) {
				double v = 0.05 + ((rand.nextDouble() + rand.nextDouble()) / 2) * approxPerPart;
				if (v > vol) v = vol;
				vol -= v;
				if (v < 0) v = Calc.sphereGetVolume(0.05 + rand.nextDouble() * 0.1);
				volumes[i] = v;
			}


			for (int i = 0; i < pieces; i++) {
				double newScale = Calc.sphereGetRadius(volumes[i]);

				if (newScale < 0.05) newScale = 0.05;

				Coord apos = this.getPos();

				double r = getRadius() * 0.6;

				apos.add_ip(-r + rand.nextDouble() * (r) * 2, 0, -r + rand.nextDouble() * (r) * 2);

				Vec amotion = getPos().vecTo(apos).norm(0.05 + rand.nextDouble() * 0.1);
				Entity e;
				scene.add(e = new EntityAsteroid(newScale, apos, amotion, texture));
				e.health *= 0.1;
				e.scoreValue *= 0.6;
				e.healthMul = healthMul * 0.8;

				// tiny rocks will eventually disappear.
				if (newScale < 0.15) e.lifetime = (int) (Constants.FPS_UPDATE * (0.3 + rand.nextDouble() * 5));
			}
		}

		if (lastDamageSource.getType() == EEntity.SHOT_GOOD && rand.nextInt(3) == 0 && scale > 0.5) {
			if (scene.playerShip.body.shieldSystem.getLoadRatio() < 1) {
				scene.add(new EntityOrbShield(getPos(), scale * 600 * (0.6 + rand.nextDouble() * 0.7)));
			}
		}

	}

	@Override
	public EEntity getType() {
		return EEntity.NATURAL;
	}

}
