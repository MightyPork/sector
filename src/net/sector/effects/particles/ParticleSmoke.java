package net.sector.effects.particles;


import net.sector.Constants;
import net.sector.effects.EParticle;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;



/**
 * Smoke particle
 * 
 * @author MightyPork
 */
public class ParticleSmoke extends Particle {

	/** Texture type */
	public int type = 0;

	/**
	 * Smoke particle
	 * 
	 * @param pos position
	 * @param motion motion
	 * @param rotSpeed rotation speed
	 */
	public ParticleSmoke(Coord pos, Vec motion, double rotSpeed) {
		super(pos, motion);
		this.rotAngle.set(rand.nextDouble() * 360);
		this.rotSpeed = rotSpeed;
		this.maxAge = (long) (Constants.FPS_UPDATE * (0.6 + rand.nextDouble() * 1));
		this.size = this.sizeOrig = rand.nextDouble() * 0.5;
		this.renderColor.setTo(new RGB(1, 1, 1));
		this.renderAlpha = 1;
		type = rand.nextInt(2);
	}

	@Override
	public EParticle getType() {
		return EParticle.SMOKE;
	}

	@Override
	public void onUpdate() {
		renderAlpha = 0.5 * Calc.square(1F - (float) age / (float) maxAge);
	}

}