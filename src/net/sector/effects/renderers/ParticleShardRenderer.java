package net.sector.effects.renderers;


import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;
import net.sector.effects.particles.ParticleShard;

import com.porcupine.coord.Coord;


/**
 * Shard particle renderer
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ParticleShardRenderer extends ParticleRendererPlain {
	/**
	 * Shard particle renderer
	 */
	public ParticleShardRenderer() {
		super(new Coord(0, 0));
	}

	@Override
	public void renderParticle(Particle part, double delta) {
		texCoord.x = 2 + ((ParticleShard) part).type;
		//glEnable(GL_LIGHTING);
		super.renderParticle(part, delta);
		//glDisable(GL_LIGHTING);
	}

	@Override
	public EParticle getType() {
		return EParticle.SHARD;
	}
}