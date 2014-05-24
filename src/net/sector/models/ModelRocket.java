package net.sector.models;


import com.porcupine.math.Calc;


public class ModelRocket extends PhysModel {

	public ModelRocket(String resource, double renderScale, double colliderRadius, double density, int health, int score) {
		super(resource, renderScale, colliderRadius, density, health, score);
	}

	public ModelRocket(ModelRocket resource, String texture) {
		super(resource, texture);
	}

	private double getVolume(double scale) {
		return (3.1416 * Calc.square(scale));
	}

	@Override
	public double getMass(double scale) {
		return getVolume(scale) * density;
	}

	@Override
	public double getScore(double scale) {
		return Math.pow(scale, 1.2) * score;
	}

	@Override
	public double getHealth(double scale) {
		return Math.pow(scale, 1.2) * health;
	}
}
