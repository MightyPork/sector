package net.sector.textures;


import org.lwjgl.opengl.GL11;


/**
 * Texture loading class
 * 
 * @author MightyPork
 */
public class Textures {

	/**
	 * Load what's needed for splash
	 */
	public static void loadForSplash() {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		TextureManager.load("particles_plain", "res/images/particles_plain.png");
		//TextureManager.load("particles_blend", "res/images/particles_blend.png");
	}

	/**
	 * Load textures
	 */
	public static void load() {
		TextureManager.load("designer_icons", "res/images/designer_icons.png");
		//TextureManager.load("particles_plain", "res/images/particles_plain.png");
		TextureManager.load("particles_blend", "res/images/particles_blend.png");

		TextureManager.load("shield", "res/images/shield.png");

		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

}
