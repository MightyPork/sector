package net.sector.models.wavefront.loader;



public class Face {

	public static final int GL_TRIANGLES = 3;
	public static final int GL_QUADS = 4;

	private Vertex[] vertices;
	private Vertex[] normals;
	private TextureCoordinate[] textures;
	private Material material;

	private int type;

	public Face copy() {
		Face f = new Face();
		f.setVertices(vertices);
		f.setNormals(normals);
		f.setTextures(textures);
		f.setMaterial(material);
		return f;
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Vertex[] getNormals() {
		return normals;
	}

	public void setNormals(Vertex[] normals) {
		this.normals = normals;
	}

	public TextureCoordinate[] getTextures() {
		return textures;
	}

	public void setTextures(TextureCoordinate[] textures) {
		this.textures = textures;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}


}
