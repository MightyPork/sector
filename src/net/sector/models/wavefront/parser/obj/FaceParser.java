package net.sector.models.wavefront.parser.obj;


import net.sector.models.wavefront.loader.Face;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.loader.TextureCoordinate;
import net.sector.models.wavefront.loader.Vertex;
import net.sector.models.wavefront.parser.LineParser;



public class FaceParser extends LineParser {

	private Face face;
	private Vertex[] vertices;
	private Vertex[] normals;
	private TextureCoordinate[] textures;
	private RenderModel object = null;

	public FaceParser(RenderModel object) {
		this.object = object;
	}

	@Override
	public void parse() {
		face = new Face();
		normals = new Vertex[words.length - 1];
		textures = new TextureCoordinate[words.length - 1];
		switch (words.length) {
			case 4:
				parseTriangles();
				break;
			case 5:
				parseQuad();
				break;
			default:
				parsePolygon();
		}


	}

	private void parseTriangles() {
		face.setType(Face.GL_TRIANGLES);
		parseLine(3);
	}

	private void parsePolygon() {
		face.setType(words.length - 1);
		parseLine(words.length - 1);
	}

	private void parseLine(int vertexCount) {
		String[] rawFaces = null;
		int currentValue;
		vertices = new Vertex[vertexCount];
		for (int i = 1; i <= vertexCount; i++) {
			rawFaces = words[i].split("/");

			// v
			currentValue = Integer.parseInt(rawFaces[0]);
			vertices[i - 1] = object.getVertices().get(currentValue - 1);	// -1 because references starts at 1

			if (rawFaces.length == 1) continue;

			if (!"".equals(rawFaces[1]) && rawFaces.length == 3) {
				currentValue = Integer.parseInt(rawFaces[1]);
				if (currentValue <= object.getTextures().size())  // This is to compensate the fact that if no texture is in the obj file, sometimes '1' is put instead of 'blank' (we find coord1/1/coord3 instead of coord1//coord3 or coord1/coord3)
					textures[i - 1] = object.getTextures().get(currentValue - 1); // -1 because references starts at 1
			}


			currentValue = Integer.parseInt(rawFaces[rawFaces.length - 1]);

			normals[i - 1] = object.getNormals().get(currentValue - 1); 	// -1 because references starts at 1
		}
	}

	private void parseQuad() {
		face.setType(Face.GL_QUADS);
		parseLine(4);
	}

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {

		face.setNormals(this.normals);
		face.setVertices(this.vertices);
		face.setTextures(this.textures);
		if (wavefrontObject.getCurrentMaterial() != null) {
			face.setMaterial(wavefrontObject.getCurrentMaterial());
		}
		wavefrontObject.getFaces().add(face);

	}

	static int faceC = 0;
}
