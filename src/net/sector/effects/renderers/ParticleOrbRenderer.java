package net.sector.effects.renderers;


import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;
import net.sector.effects.particles.ParticleOrb;

import com.porcupine.coord.Coord;


/**
 * orb particle renderer
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ParticleOrbRenderer extends ParticleRendererBlend {
	/**
	 * Fire particle renderer
	 */
	public ParticleOrbRenderer() {
		super(new Coord(1, 1));
	}

	@Override
	public void renderParticle(Particle part, double delta) {
		texCoord.x = 1 + ((ParticleOrb) part).type % 2;
		texCoord.y = 1 + ((ParticleOrb) part).type / 2;
		super.renderParticle(part, delta);
	}

	@Override
	public EParticle getType() {
		return EParticle.ORB;
	}
}
