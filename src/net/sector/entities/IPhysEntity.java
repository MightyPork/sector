package net.sector.entities;


import net.sector.collision.Collider;
import net.sector.collision.ColliderSphere;
import net.sector.collision.Scene;
import net.sector.util.DeltaDoubleDeg;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Entity interface for physics calculations
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public interface IPhysEntity extends IDamageable {
	public Coord getPos();

	public Vec getMotion();

	public void setMotion(Vec motion);

	public void setPos(Coord pos);

	public void setMaxSpeed(double maxSpeed);

	public double getSpeed();

	public void setDead();

	@Override
	public boolean isDead();

	@Override
	public double getHealth();

	public double getMass();

	@Override
	public void addDamage(IDamageable source, double damage);

	public Scene getScene();

	public void setScene(Scene scene);

	public double getRadius();

	public Vec getRotDir();

	public DeltaDoubleDeg getRotAngle();

	public ColliderSphere getColliderFor(Collider hitBy);
}
