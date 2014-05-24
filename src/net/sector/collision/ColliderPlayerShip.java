package net.sector.collision;


import java.util.ArrayList;

import net.sector.entities.Entity;
import net.sector.level.ship.modules.ShipBody;
import net.sector.level.ship.modules.pieces.Piece;

import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


/**
 * Player ship collider (made up of pieces)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ColliderPlayerShip extends ColliderSphere {

	/** Ship entity */
	public Entity entity;
	/** Entity scene */
	public Scene scene;

	/**
	 * Player ship collider
	 * 
	 * @param center central coord
	 * @param shipBody ship body.
	 */
	public ColliderPlayerShip(Coord center, ShipBody shipBody) {
		super(center, Calc.pythC(shipBody.sizeX * ShipBody.pieceDist, shipBody.sizeX * ShipBody.pieceDist) / 2d);
		body = shipBody;
		body.setCollider(this);
	}

	/**
	 * Player ship collider
	 * 
	 * @param center central coord
	 * @param width body width [x]
	 * @param height body height [z]
	 */
	public ColliderPlayerShip(Coord center, int width, int height) {
		super(center, 0);
		body = new ShipBody(width, height);
		this.radius = Calc.pythC(width * ShipBody.pieceDist, height * ShipBody.pieceDist) / 2d;
		body.setCollider(this);
	}

	/**
	 * Hook called when the ship was added to scene
	 * 
	 * @param entity ship entity
	 */
	public void onAddedToScene(Entity entity) {
		this.entity = entity;
		this.scene = entity.scene;
		body.onReady();
	}

	/**
	 * Get rotation around Y in delta time
	 * 
	 * @param delta delta time
	 * @return Y rot.
	 */
	public double getRotY(double delta) {
		return entity.rotAngle.delta(delta);
	}

	/**
	 * Get rotation around Z in delta time
	 * 
	 * @param delta delta time
	 * @return Z rot.
	 */
	public double getRotZ(double delta) {
		return Calc.clampd((-entity.getMotion().x) * 150, -25, 25);
	}

	/** Ship body */
	public ShipBody body;
	/** Colliders collided during the last impact */
	public ArrayList<Piece> lastCollided = null;
	/** Last impact was caused by shield. */
	public boolean collidingShield = false;

	@Override
	public boolean collidesWith(Collider other) {
		if (other instanceof ColliderSphere) {
			ColliderSphere otherSphere = (ColliderSphere) other;
			if (pos.distTo(otherSphere.pos) < radius + otherSphere.radius) {
				// collides with the outer sphere
				if (body.isShieldRunning()) {
					lastCollided = null;
					collidingShield = true;
					return true;
				}

				collidingShield = false;

				ArrayList<Piece> colliding = body.getCollidingPieces(other);
				if (colliding.size() == 0) colliding = null;
				if (colliding != null) {
					lastCollided = colliding;
					return true;
				}
			}
			lastCollided = null;
			return false;
		}
		throw new RuntimeException("Collision test not implemented for " + Calc.className(this) + " and " + Calc.className(other));
	}

}
