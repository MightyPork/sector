package net.sector.models.wavefront.parser.obj;


import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.loader.Vertex;
import net.sector.models.wavefront.parser.LineParser;



public class VertexParser extends LineParser {

	Vertex vertex = null;

	public VertexParser() {}

	@Override
	public void parse() {
		vertex = new Vertex();

		try {
			vertex.setX(Float.parseFloat(words[1]));
			vertex.setY(Float.parseFloat(words[2]));
			vertex.setZ(Float.parseFloat(words[3]));
		} catch (Exception e) {
			throw new RuntimeException("VertexParser Error");
		}
	}

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {
		wavefrontObject.getVertices().add(vertex);
	}

}
