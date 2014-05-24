package net.sector.models;


import net.sector.models.wavefront.loader.RenderModel;

import com.porcupine.math.Calc;


public class ModelEnemyShip extends PhysModel {

	public ModelEnemyShip(String resource, double renderScale, double colliderRadius, double density, int health, int score) {
		super(resource, renderScale, colliderRadius, density, health, score);
	}

	public ModelEnemyShip(PhysModel resource, String texture) {
		super(resource, texture);
	}

	public ModelEnemyShip(RenderModel model, double renderScale, double colliderRadius, int density, int health, int score) {
		super(model, renderScale, colliderRadius, density, health, score);
	}

	private double getVolume(double scale) {
		return (3.1416 * Calc.square(scale));
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
