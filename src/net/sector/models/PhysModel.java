package net.sector.models;


import net.sector.models.wavefront.loader.RenderModel;


public abstract class PhysModel {

	public double renderScale = 1;
	public double colliderRadius = 1;
	public double density = 1;
	public int health = 8;
	public int score = 10;

	public RenderModel model = null;

	public PhysModel(String modelpath, double renderScale, double colliderRadius, double density, int health, int score) {
		this.renderScale = renderScale;
		this.colliderRadius = colliderRadius;
		this.density = density;
		this.model = new RenderModel(modelpath);
		this.health = health;
		this.score = score;
	}

	public PhysModel(RenderModel resource, String texture, double renderScale, double colliderRadius, double density, int health, int score) {
		this.renderScale = renderScale;
		this.colliderRadius = colliderRadius;
		this.density = density;
		this.model = new RenderModel(resource, texture);
		this.health = health;
		this.score = score;
	}

	public PhysModel(PhysModel other, String texture) {
		this.renderScale = other.renderScale;
		this.colliderRadius = other.colliderRadius;
		this.density = other.density;
		this.model = new RenderModel(other.model, texture);
		this.health = other.health;
		this.score = other.score;
	}

	public PhysModel(RenderModel model, double renderScale, double colliderRadius, int density, int health, int score) {
		this.renderScale = renderScale;
		this.colliderRadius = colliderRadius;
		this.density = density;
		this.model = model;
		this.health = health;
		this.score = score;
	}

	public abstract double getMass(double scale);

	public abstract double getHealth(double scale);

	public abstract double getScore(double scale);

	public void render() {
		model.render();
	}

}
