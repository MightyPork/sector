package net.sector.effects.renderers;


import static org.lwjgl.opengl.GL11.*;
import net.sector.effects.EParticle;
import net.sector.effects.particles.Particle;

import com.porcupine.coord.Coord;



/**
 * Particle renderer.<br>
 * Only one instance of this renderer is made, and is later held in
 * ParticleManager.renderers
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class ParticleRenderer {

	/**
	 * coordinates 0-7,0-7 in texture, allowing having multiple particles in
	 * single file
	 */
	public Coord texCoord;

	/**
	 * New particle renderer
	 * 
	 * @param textureCoords new float[]{left, top, right, bottom} coordinates in
	 *            texture, 0-1
	 */
	public ParticleRenderer(Coord textureCoords) {
		this.texCoord = textureCoords;
	}

	/**
	 * Render the particle. You should load identity, translate and rotate and
	 * do the rendering using texture.bind().<br>
	 * Don't forget to add MINUS sign before z axis in translation.
	 * 
	 * @param part the particle
	 * @param delta delta time
	 */
	public void renderParticle(Particle part, double delta) {

		double left = (texCoord.x) * 0.125;
		double top = (texCoord.y) * 0.125;
		double right = (texCoord.x + 1) * 0.125;
		double bottom = (texCoord.y + 1) * 0.125;

		Coord pos = part.pos.getDelta(delta);
		double scale = part.size * 1.4142414 * 0.5;

		double sx1 = Math.cos(Math.toRadians(part.rotAngle.delta(delta))) * scale;
		double sy1 = Math.sin(Math.toRadians(part.rotAngle.delta(delta))) * scale;
		double sx2 = -sy1;
		double sy2 = sx1;

		glColor4d(part.renderColor.r, part.renderColor.g, part.renderColor.b, part.renderAlpha);

		glTexCoord2d(left, top);
		glVertex3d(pos.x + sx1, pos.y + sy1, -pos.z);

		glTexCoord2d(right, top);
		glVertex3d(pos.x + sx2, pos.y + sy2, -pos.z);

		glTexCoord2d(right, bottom);
		glVertex3d(pos.x - sx1, pos.y - sy1, -pos.z);

		glTexCoord2d(left, bottom);
		glVertex3d(pos.x - sx2, pos.y - sy2, -pos.z);

	}

	/**
	 * Prepare for rendering
	 */
	public abstract void prepareRender();

	/**
	 * Finish the rendering
	 */
	public abstract void finishRender();

	/**
	 * Get particle type this renderer can render
	 * 
	 * @return type
	 */
	public abstract EParticle getType();

}
