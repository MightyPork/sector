package net.sector.entities;


public interface IDamageable {
	public void addDamage(IDamageable source, double points);

	public boolean isDead();

	public double getHealth();

	public EEntity getType();

	double getHealthMax();
}
