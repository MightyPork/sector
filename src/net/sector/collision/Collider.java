package net.sector.collision;


import com.porcupine.coord.Coord;


/**
 * Collider object, used to hold information about object positions, rotations
 * and to detect their collisions.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class Collider {
	/** Central point */
	public Coord pos = new Coord();

	/**
	 * Check if collides with other collider
	 * 
	 * @param other other collider
	 * @return collides
	 */
	public abstract boolean collidesWith(Collider other);

	/**
	 * Render debug sphere
	 */
	public abstract void render();
}
