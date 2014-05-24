package net.sector.models.wavefront.loader;


import net.sector.models.wavefront.parser.LineParser;


public class NormalParser extends LineParser {

	Vertex vertex = null;

	public NormalParser() {}

	@Override
	public void parse() {
		vertex = new Vertex();

		try {
			vertex.setX(Float.parseFloat(words[1]));
			vertex.setY(Float.parseFloat(words[2]));
			vertex.setZ(Float.parseFloat(words[3]));
		} catch (Exception e) {
			throw new RuntimeException("NormalParser Error");
		}

	}

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {
		wavefrontObject.getNormals().add(vertex);

	}

}
