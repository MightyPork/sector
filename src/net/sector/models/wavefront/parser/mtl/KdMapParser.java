package net.sector.models.wavefront.parser.mtl;


import net.sector.models.wavefront.loader.Material;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.parser.LineParser;
import net.sector.textures.TextureManager;

import org.newdawn.slick.opengl.Texture;


public class KdMapParser extends LineParser {

	private Texture texture = null;
	private RenderModel object = null;
	private String textureFileName = null;

	public KdMapParser(RenderModel object) {
		this.object = object;
	}

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {

		if (texture != null) {
			Material currentMaterial = wavefrontObject.getCurrentMaterial();
			currentMaterial.setTexture(texture);
		}
	}

	@Override
	public void parse() {
		String textureFileName = words[words.length - 1];
		String pathToTextureBinary = object.getContextfolder() + (object.replTexture == null ? textureFileName : object.replTexture);

		// load texture, if already loaded, use the same instance.
		texture = TextureManager.load(pathToTextureBinary);
	}

}
