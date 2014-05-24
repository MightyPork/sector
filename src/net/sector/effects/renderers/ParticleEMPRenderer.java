package net.sector.effects.renderers;


import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;
import net.sector.effects.particles.ParticleEMP;

import com.porcupine.coord.Coord;


/**
 * Fire particle renderer
 * 
 * @author MightyPork
 */
public class ParticleEMPRenderer extends ParticleRendererBlend {
	/**
	 * Fire particle renderer
	 */
	public ParticleEMPRenderer() {
		super(new Coord(1, 1));
	}

	@Override
	public void renderParticle(Particle part, double delta) {
		texCoord.x = 1 + ((ParticleEMP) part).type % 2;
		texCoord.y = 1 + ((ParticleEMP) part).type / 2;
		super.renderParticle(part, delta);
	}

	@Override
	public EParticle getType() {
		return EParticle.EMP;
	}
}
