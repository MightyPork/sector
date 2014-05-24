package net.sector.effects.particles;


import net.sector.Constants;
import net.sector.effects.EParticle;

import com.porcupine.color.HSV;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;



/**
 * "EMP" particle
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ParticleOrb extends Particle {

	private Vec origMotion = null;
	private boolean slow = false;

	/** Type (0-5) */
	public int type = 0;

	/**
	 * EMP particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param slowDown can slow down gradually
	 */
	public ParticleOrb(Coord pos, Vec motion, boolean slowDown, int color) {
		super(pos, motion);
		origMotion = motion.copy();
		slow = slowDown;
		this.rotAngle.set(rand.nextDouble() * 360);
		this.rotSpeed = -10 + rand.nextDouble() * 20;
		this.maxAge = (long) (Constants.FPS_UPDATE * (0.2 + rand.nextDouble() * 1));
		this.size = this.sizeOrig = 0.6 + rand.nextDouble();
		double h = 0;

		if (color == 0) h = 0.5 + rand.nextDouble() * 0.3;
		if (color == 1) h = rand.nextBoolean() ? rand.nextDouble() * 0.1 : 1 - rand.nextDouble() * 0.1;
		if (color == 2) h = 0.2 + rand.nextDouble() * 0.3;

		this.renderColor.setTo(new HSV(h, 0.6 + rand.nextDouble() * 0.4, 1).toRGB());
		this.renderAlpha = 1;
		type = rand.nextInt(6);
	}

	/**
	 * EMP particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param scale render size 0.001-2
	 */
	public ParticleOrb(Coord pos, Vec motion, double scale, int color) {
		this(pos, motion, true, color);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 1);
	}

	/**
	 * EMP particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param scale render size 0.001-2
	 * @param slowDown can slow down gradually
	 */
	public ParticleOrb(Coord pos, Vec motion, double scale, boolean slowDown, int color) {
		this(pos, motion, slowDown, color);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 1);
	}

	@Override
	public EParticle getType() {
		return EParticle.ORB;
	}

	@Override
	public void onUpdate() {
		size = Calc.square(1F - ((float) age / (float) maxAge)) * sizeOrig;

		if (slow) {
			motion.setTo(origMotion.scale(Calc.square(1 - (float) age / (float) maxAge)));
			if (size < sizeOrig * 0.2) setDead();
		} else {
			motion.scale_ip(0.90);
			if (size < 0.2) setDead();
		}
	}
}
