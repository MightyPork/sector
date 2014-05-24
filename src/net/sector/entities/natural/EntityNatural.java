package net.sector.entities.natural;


import net.sector.entities.EEntity;
import net.sector.entities.Entity;


public abstract class EntityNatural extends Entity {

	@Override
	public EEntity getType() {
		return EEntity.NATURAL;
	}

	@Override
	public abstract void onImpact(Entity hitBy);

	@Override
	public double getEmpSensitivity() {
		return 0;
	}

	@Override
	public double getFireFlammability() {
		return 0.3;
	}

	@Override
	public double getFireSensitivity() {
		return 0.08;
	}

	@Override
	public abstract void onUpdate();

	@Override
	public abstract void onDeath();

	@Override
	public abstract void render(double delta);

	@Override
	public abstract double getHealthMax();

}
