package net.sector.collision;


import com.porcupine.coord.Coord;


/**
 * Sphere collider, never colliding
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ColliderSphereFake extends ColliderSphere {

	/**
	 * Sphere collider
	 * 
	 * @param center center
	 * @param radius sphere radius
	 */
	public ColliderSphereFake(Coord center, double radius) {
		super(center, radius);
	}

	@Override
	public boolean collidesWith(Collider other) {
		return false;
	}
}
