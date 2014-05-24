package net.sector.effects.renderers;


import net.sector.effects.EParticle;

import com.porcupine.coord.Coord;


/**
 * Star particle renderer
 * 
 * @author MightyPork
 */
public class ParticleStarRenderer extends ParticleRendererPlain {
	/**
	 * Star particle renderer
	 */
	public ParticleStarRenderer() {
		super(new Coord(0, 1));
	}

	@Override
	public EParticle getType() {
		return EParticle.STAR;
	}
}
