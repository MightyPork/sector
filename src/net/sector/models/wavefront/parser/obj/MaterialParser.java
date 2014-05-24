package net.sector.models.wavefront.parser.obj;


import net.sector.models.wavefront.loader.Material;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.parser.LineParser;



public class MaterialParser extends LineParser {
	String materialName = "";

	@Override
	public void parse() {
		materialName = words[1];
	}

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {
		Material newMaterial = wavefrontObject.getMaterials().get(materialName);
		wavefrontObject.setCurrentMaterial(newMaterial);

	}



}
