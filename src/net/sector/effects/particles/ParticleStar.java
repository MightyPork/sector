package net.sector.effects.particles;


import net.sector.Constants;
import net.sector.effects.EParticle;

import com.porcupine.color.HSV;
import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;



/**
 * Experimental (= ugly) star particle
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ParticleStar extends Particle {

	private Vec origMotion = null;
	private boolean slow = false;

	private HSV hsv = new HSV(0, 1, 1);

	/**
	 * Star particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 * @param slowDown can slow down gradually
	 */
	public ParticleStar(Coord pos, Vec motion, double rotSpeed, boolean slowDown) {
		super(pos, motion);
		origMotion = motion.copy();
		slow = slowDown;
		this.rotAngle.set(rand.nextDouble() * 360);
		this.rotSpeed = rotSpeed;
		this.maxAge = (long) (Constants.FPS_UPDATE * (0.4 + rand.nextDouble() * 1.5));
		this.size = this.sizeOrig = 0.6 + rand.nextDouble();
		this.renderColor.setTo(new RGB(1, 1, 1));
		this.renderAlpha = 1;
	}

	/**
	 * Star particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 * @param scale render size 0.001-2
	 */
	public ParticleStar(Coord pos, Vec motion, double rotSpeed, double scale) {
		this(pos, motion, rotSpeed, true);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 2);
	}

	/**
	 * Star particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 * @param scale render size 0.001-2
	 * @param slowDown can slow down gradually
	 */
	public ParticleStar(Coord pos, Vec motion, double rotSpeed, double scale, boolean slowDown) {
		this(pos, motion, rotSpeed, slowDown);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 2);
	}

	@Override
	public EParticle getType() {
		return EParticle.STAR;
	}

	@Override
	public void onUpdate() {
		renderAlpha = Calc.square(1F - (float) age / (float) maxAge);

		this.renderColor.setTo(hsv.toRGB());
		hsv.h -= 0.005;
		if (hsv.h < 0) hsv.h = 1 - hsv.h;

		size = Calc.square(1F - Calc.clampd((age / ((float) maxAge + 1)), 0, 1)) * sizeOrig;

		if (slow) {
			motion.setTo(origMotion.scale(Calc.square(1 - (float) age / (float) maxAge)));
			//if(size < sizeOrig*0.2) setDead();
		} else {
			motion.scale_ip(0.90);
			//if(size < 0.2) setDead();
		}
	}

}
