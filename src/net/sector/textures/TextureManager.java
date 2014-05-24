package net.sector.textures;


import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.HashMap;

import net.sector.Constants;
import net.sector.util.Log;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


/**
 * Texture manager
 * 
 * @author MightyPork
 */
public class TextureManager {

	private static final boolean DEBUG = Constants.LOG_TEXTURES;

	private static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	private static HashMap<String, Texture> texturesByPath = new HashMap<String, Texture>();

	/**
	 * Load texture
	 * 
	 * @param resourcePath
	 * @return the loaded texture
	 */
	public static Texture load(String resourcePath) {
		try {
			if (texturesByPath.containsKey(resourcePath)) {
				return texturesByPath.get(resourcePath);
			}

			String ext = resourcePath.substring(resourcePath.length() - 4);

			Texture texture = TextureLoader.getTexture(ext.toUpperCase(), ResourceLoader.getResourceAsStream(resourcePath));

			if (texture != null) {

				if (DEBUG) Log.f2("Texture " + resourcePath + " loaded.");

				texturesByPath.put(resourcePath, texture);
				return texture;
			}

			Log.w("Texture " + resourcePath + " could not be loaded.");
			return null;
		} catch (IOException e) {
			Log.e("Loading of texture " + resourcePath + " failed.", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * Load texture to the texture list
	 * 
	 * @param name texture name used to call it
	 * @param resourcePath eg. "res/image.png"
	 * @return the texture just loaded
	 */
	public static Texture load(String name, String resourcePath) {

		if (!textures.containsKey(name)) {
			Texture texture = load(resourcePath);

			if (texturesByPath.containsKey(resourcePath)) {
				textures.put(name, texture = texturesByPath.get(resourcePath));
				return texture;
			}

			if (texture != null) {
				textures.put(name, texture);
				return texture;
			}
			Log.w("Texture " + resourcePath + " could not be loaded.");
			return null;
		} else {
			return get(name);
		}

	}

	/**
	 * Bind texture by name
	 * 
	 * @param name texture name
	 * @throws RuntimeException if not loaded yet
	 */
	public static void bind(String name) throws RuntimeException {
		Texture tx = get(name);
		if (tx == null) throw new RuntimeException("Can't bind texture \"" + name + "\", texture is not loaded.");
		//tx.bind();
		glBindTexture(GL_TEXTURE_2D, tx.getTextureID());
	}

	/**
	 * Unbind all
	 */
	public static void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	/**
	 * Get texture for name
	 * 
	 * @param name name
	 * @return texture
	 */
	public static Texture get(String name) {
		return textures.get(name);
	}


}
