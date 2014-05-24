package net.sector.models;


import com.porcupine.math.Calc;


public class ModelAsteroid extends PhysModel {

	public ModelAsteroid(String resource, double renderScale, double colliderRadius, double density, int health, int score) {
		super(resource, renderScale, colliderRadius, density, health, score);
	}

	public ModelAsteroid(ModelAsteroid resource, String texture) {
		super(resource, texture);
	}

	private double getVolume(double scale) {
		return (1.333 * 3.1416 * Calc.cube(scale));
	}

	@Override
	public double getMass(double scale) {
		return getVolume(scale) * density;
	}

	@Override
	public double getHealth(double scale) {
		return Math.pow(scale, 1.2) * health;
	}

	@Override
	public double getScore(double scale) {
		return Math.pow(scale, 1.2) * score;
	}


}
