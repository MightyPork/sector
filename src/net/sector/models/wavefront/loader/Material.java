package net.sector.models.wavefront.loader;


import org.newdawn.slick.opengl.Texture;


public class Material {

	private Texture texture;
	private Vertex Kd;
	private String name;

	public Material(String name) {
		this.name = name;
	}

	public Material(Material other) {
		//this.texture = other.texture;
		this.Kd = other.Kd;
		this.name = other.name;
	}

	public Texture getTexture() {
		return texture;
	}

	public Material setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}

	public Vertex getKd() {
		return Kd;
	}

	public void setKa(Vertex kd) {
		Kd = kd;
	}

	@Override
	public String toString() {
		return "MAT." + name;
	}

	public String getName() {
		return name;
	}


}
