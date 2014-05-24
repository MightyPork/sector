package net.sector.effects.renderers;


import static org.lwjgl.opengl.GL11.*;
import net.sector.textures.TextureManager;

import com.porcupine.coord.Coord;



/**
 * Particle renderer PLAIN.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class ParticleRendererPlain extends ParticleRenderer {

	/**
	 * Plain renderer
	 * 
	 * @param textureCoords texture coords
	 */
	public ParticleRendererPlain(Coord textureCoords) {
		super(textureCoords);
	}

	@Override
	public void prepareRender() {
		glPushMatrix();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		TextureManager.bind("particles_plain");
		glBegin(GL_QUADS);

	}

	@Override
	public void finishRender() {

		glEnd();
		TextureManager.unbind();
		glDisable(GL_BLEND);
		glPopMatrix();

	}
}
