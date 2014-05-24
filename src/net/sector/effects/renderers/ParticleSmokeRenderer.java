package net.sector.effects.renderers;


import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;
import net.sector.effects.particles.ParticleSmoke;

import com.porcupine.coord.Coord;


/**
 * Smoke particle renderer
 * 
 * @author MightyPork
 */
public class ParticleSmokeRenderer extends ParticleRendererPlain {
	/**
	 * Smoke particle renderer
	 */
	public ParticleSmokeRenderer() {
		super(new Coord(0, 0));
	}

	@Override
	public void renderParticle(Particle part, double delta) {
		texCoord.x = ((ParticleSmoke) part).type;

		super.renderParticle(part, delta);
	}

	@Override
	public EParticle getType() {
		return EParticle.SMOKE;
	}
}
