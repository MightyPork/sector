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
 * @author MightyPork
 */
public class ParticleEMP extends Particle {

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
	public ParticleEMP(Coord pos, Vec motion, boolean slowDown) {
		super(pos, motion);
		origMotion = motion.copy();
		slow = slowDown;
		this.rotAngle.set(rand.nextDouble() * 360);
		this.rotSpeed = -10 + rand.nextDouble() * 20;
		this.maxAge = (long) (Constants.FPS_UPDATE * (0.2 + rand.nextDouble() * 1));
		this.size = this.sizeOrig = 0.6 + rand.nextDouble();
		this.renderColor.setTo(new HSV(0.7 - 0.2 + rand.nextDouble() * 0.4, 0.8 - 0.2 + rand.nextDouble() * 0.4, 1).toRGB());
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
	public ParticleEMP(Coord pos, Vec motion, double scale) {
		this(pos, motion, true);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 2);
	}

	/**
	 * EMP particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param scale render size 0.001-2
	 * @param slowDown can slow down gradually
	 */
	public ParticleEMP(Coord pos, Vec motion, double scale, boolean slowDown) {
		this(pos, motion, slowDown);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 2);
	}

	@Override
	public EParticle getType() {
		return EParticle.EMP;
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
