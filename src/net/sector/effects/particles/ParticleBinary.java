package net.sector.effects.particles;


import net.sector.Constants;
import net.sector.effects.EParticle;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;



/**
 * "Binary" particle (animated green 0s and 1s in splash screen)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ParticleBinary extends Particle {

	private Vec origMotion = null;
	private boolean slow = false;

	/** Type (0,1) */
	public int type = 0;

	/**
	 * Binary particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 * @param slowDown can slow down gradually
	 */
	public ParticleBinary(Coord pos, Vec motion, double rotSpeed, boolean slowDown) {
		super(pos, motion);
		origMotion = motion.copy();
		slow = slowDown;
		this.rotAngle.set(rand.nextDouble() * 360);
		this.rotSpeed = rotSpeed;
		this.maxAge = (long) (Constants.FPS_UPDATE * (0.4 + rand.nextDouble() * 1.5));
		this.size = this.sizeOrig = 0.6 + rand.nextDouble();
		this.renderColor.setTo(new RGB(rand.nextDouble() * 0.3, 0.7 + rand.nextDouble() * 0.3, rand.nextDouble() * 0.3));
		this.renderAlpha = 1;
		type = rand.nextInt(2);
	}

	/**
	 * Binary particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 * @param scale render size 0.001-2
	 */
	public ParticleBinary(Coord pos, Vec motion, double rotSpeed, double scale) {
		this(pos, motion, rotSpeed, true);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 2);
	}

	/**
	 * Binary particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 * @param scale render size 0.001-2
	 * @param slowDown can slow down gradually
	 */
	public ParticleBinary(Coord pos, Vec motion, double rotSpeed, double scale, boolean slowDown) {
		this(pos, motion, rotSpeed, slowDown);
		this.size = this.sizeOrig = Calc.clampd(scale, 0.001, 2);
	}

	@Override
	public EParticle getType() {
		return EParticle.BINARY;
	}

	@Override
	public void onUpdate() {
		renderAlpha = Calc.square(1F - (float) age / (float) maxAge);

		//size = Calc.square(1F - Calc.clampd( ((float) age / ((float) maxAge)) , 0, 1))*sizeOrig;

		if (slow) {
			motion.setTo(origMotion.scale(Calc.square(1 - (float) age / (float) maxAge)));
			//if(size < sizeOrig*0.2) setDead();
		} else {
			motion.scale_ip(0.90);
			//if(size < 0.2) setDead();
		}
	}

}
