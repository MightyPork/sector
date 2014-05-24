package net.sector.effects.renderers;


import static org.lwjgl.opengl.GL11.*;
import net.sector.textures.TextureManager;

import com.porcupine.coord.Coord;



/**
 * Particle renderer BLEND (black image with colors).
 * 
 * @author MightyPork
 */
public abstract class ParticleRendererBlend extends ParticleRenderer {

	/**
	 * Blend renderer
	 * 
	 * @param textureCoords texture coords for texture
	 */
	public ParticleRendererBlend(Coord textureCoords) {
		super(textureCoords);
	}

	@Override
	public void prepareRender() {
		glPushMatrix();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		//glBlendFunc(GL_ONE_MINUS_DST_ALPHA,GL_DST_ALPHA);
		glBlendFunc(GL_ONE, GL_ONE);
		TextureManager.bind("particles_blend");
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
