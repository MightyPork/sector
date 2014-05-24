package net.sector.effects.renderers;


import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;
import net.sector.effects.particles.ParticleFire;

import com.porcupine.coord.Coord;


/**
 * Fire particle renderer
 * 
 * @author MightyPork
 */
public class ParticleFireRenderer extends ParticleRendererBlend {
	/**
	 * Fire particle renderer
	 */
	public ParticleFireRenderer() {
		super(new Coord(0, 0));
	}

	@Override
	public void renderParticle(Particle part, double delta) {
		switch (((ParticleFire) part).type) {
			case 0:
				texCoord.setTo(0, 0);
				break;
			case 1:
				texCoord.setTo(4, 1);
		}
		super.renderParticle(part, delta);
	}

	@Override
	public EParticle getType() {
		return EParticle.FIRE;
	}
}
