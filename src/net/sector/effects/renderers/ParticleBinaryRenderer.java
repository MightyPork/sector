package net.sector.effects.renderers;


import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;
import net.sector.effects.particles.ParticleBinary;

import com.porcupine.coord.Coord;


/**
 * Binary particle renderer
 * 
 * @author MightyPork
 */
public class ParticleBinaryRenderer extends ParticleRendererPlain {
	/**
	 * Binary particle renderer
	 */
	public ParticleBinaryRenderer() {
		super(new Coord(0, 1));
	}

	@Override
	public void renderParticle(Particle part, double delta) {
		texCoord.x = 1 + ((ParticleBinary) part).type;
		super.renderParticle(part, delta);
	}

	@Override
	public EParticle getType() {
		return EParticle.BINARY;
	}
}
