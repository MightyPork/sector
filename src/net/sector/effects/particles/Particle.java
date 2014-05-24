package net.sector.effects.particles;


import java.util.Random;

import net.sector.Constants;
import net.sector.effects.EParticle;
import net.sector.util.DeltaDoubleDeg;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Particle pseudo-entity
 * 
 * @author MightyPork
 */
public abstract class Particle implements Comparable<Particle> {

	private boolean hasGlobalMovement = false;

	/**
	 * Set whether particle should move together with asteroids (global
	 * movement)
	 * 
	 * @param state
	 * @return this
	 */
	public Particle setGlobalMovement(boolean state) {
		hasGlobalMovement = state;
		return this;
	}

	/**
	 * Should move with asteroids? (global movement)
	 * 
	 * @return has global movement
	 */
	public boolean hasGlobalMovement() {
		return hasGlobalMovement;
	}

	/** RNG */
	public static Random rand = new Random();

	/** particle quad size when rendered */
	public double size = 1;

	/** size of a new particle. */
	public double sizeOrig = 1;

	/** position in 3D space */
	public Coord pos = new Coord(0, 0, 0);

	/** Motion, added each tick to position */
	public Vec motion = new Vec(0, 0, 0);

	/** Angle (deg) of Z-axis rotation */
	public DeltaDoubleDeg rotAngle = new DeltaDoubleDeg(0);

	/** Rotation speed, added each tick to rotAngle */
	public double rotSpeed = 2;

	/** Particle age */
	public long age = 0;

	/** Max particle age */
	public long maxAge = 100;

	/** Flag that this particle should be removed from manager next tick. */
	public boolean isDead = false;

	/** Color multiplier for the particle rendering */
	public RGB renderColor = new RGB(1, 1, 1);

	/** Particle alpha 0-1 */
	public double renderAlpha = 1.0;

	/**
	 * @return particle ID, from ParticleType.
	 */
	public abstract EParticle getType();

	/**
	 * Set particle dead → will be removed from manager next update tick.
	 */
	public void setDead() {
		isDead = true;
	}

	/**
	 * Create new particle
	 * 
	 * @param pos position
	 * @param motion motion
	 */
	public Particle(Coord pos, Vec motion) {
		this.pos.setTo(pos);
		this.motion.setTo(motion);
	}

	/**
	 * Update the particle
	 */
	public final void update() {
		if (isDead) return;

		pos.pushLast();
		rotAngle.pushLast();

		pos.add_ip(motion.mul(Constants.SPEED_MUL));
		rotAngle.add(rotSpeed * Constants.SPEED_MUL);

		age++;
		if (age >= maxAge) {
			setDead();
			return;
		}

		onUpdate();

		pos.update();
	}

	/**
	 * Called each update tick. You can check age and set isDead here, do some
	 * additional animation etc.
	 */
	public abstract void onUpdate();

	@Override
	public int compareTo(Particle o) {
		if (this == o) return 0;
		return Double.compare(new Double(o.pos.z), new Double(pos.z));
//		if(o.pos.z > pos.z) return -1;
//		if(o.pos.z < pos.z) return 1;
//		return 0;
	}
}
