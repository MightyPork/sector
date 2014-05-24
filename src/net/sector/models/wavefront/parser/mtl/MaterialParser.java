package net.sector.models.wavefront.parser.mtl;


import net.sector.models.wavefront.loader.Material;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.parser.LineParser;



public class MaterialParser extends LineParser {

	String materialName = "";

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {
		Material newMaterial = new Material(materialName);

		wavefrontObject.getMaterials().put(materialName, newMaterial);
		wavefrontObject.setCurrentMaterial(newMaterial);
	}

	@Override
	public void parse() {
		materialName = words[1];
	}

}
