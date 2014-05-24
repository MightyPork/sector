package net.sector;


import net.sector.entities.EEntity;
import net.sector.entities.IDamageable;


public class NullDamageSource implements IDamageable {

	@Override
	public void addDamage(IDamageable source, double points) {}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public double getHealth() {
		return 0;
	}

	@Override
	public EEntity getType() {
		return EEntity.NONE;
	}

	@Override
	public double getHealthMax() {
		return 0;
	}

}
