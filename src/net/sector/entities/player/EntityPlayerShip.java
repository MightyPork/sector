package net.sector.entities.player;


import java.util.ArrayList;

import net.sector.Constants;
import net.sector.collision.Collider;
import net.sector.collision.ColliderPlayerShip;
import net.sector.collision.ColliderSphere;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.IDamageable;
import net.sector.entities.IPhysEntity;
import net.sector.entities.IScoreCounter;
import net.sector.input.IInputHandler;
import net.sector.level.GameCursor;
import net.sector.level.ship.ShipBundle;
import net.sector.level.ship.modules.ShipBody;
import net.sector.level.ship.modules.pieces.Piece;
import net.sector.sounds.Sounds;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Player ship entity
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EntityPlayerShip extends Entity implements IScoreCounter, IInputHandler {

	public static final int MAXROT = 85;


	/** Ship body piece store + shield + energy system */
	public ShipBody body;

	public GameCursor cursor;

	/** Collider as player ship collider */
	private ColliderPlayerShip colliderBody;

	/** Flag indicating that ship should rotate back to Z- direction gradually. */
	public boolean rotationCenterRequested = false;

	/** Degrees player wants to add to ship rotation - added gradually. */
	public int angleInc = 0;


	/**
	 * Create ship bundle from current state (damaged ship body, new scores
	 * etc.)
	 * 
	 * @return new context
	 */
	public ShipBundle createNewShipBundle() {
		return new ShipBundle(body.toTable(), body.shieldSystem.level, body.energySystem.level);
	}

	/**
	 * Player ship<br>
	 * 
	 * @param pos center in 3D space
	 * @param cursor game cursor
	 */
	public EntityPlayerShip(Coord pos, GameCursor cursor) {

		this.cursor = cursor;

		this.collidePriority = -1;

		int X = cursor.shipBundle.ship[0].length, Z = cursor.shipBundle.ship.length;

		collider = colliderBody = new ColliderPlayerShip(pos, X, Z);
		body = colliderBody.body;


		body.setCenterCoord(X / 2, Z / 2);

		double massSum = 0;

		for (int z = 0; z < Z; z++) {
			for (int x = 0; x < X; x++) {
				if (cursor.shipBundle.ship[z][x] != null) {
					Piece p;
					body.setPiece(x, z, p = cursor.shipBundle.ship[z][x].toPiece());
					massSum += p.getPieceMass();
				}
			}
		}
		this.mass = 5 * massSum;

		body.energyLevel = cursor.shipBundle.energyLevel;
		body.shieldLevel = cursor.shipBundle.shieldLevel;

		setHealth(100000);

		this.motion.setTo(0, 0, 0);

		this.lifetime = -1;
		this.MAX_SPEED = 0.3;
		this.rotDir.setTo(0, 1, 0);
		setGlobalMovement(false);

	}

	@Override
	public ColliderSphere getColliderFor(Collider hitBy) {
		if (colliderBody.collidesWith(hitBy)) {
			if (colliderBody.lastCollided == null) {
				if (colliderBody.collidingShield) {
					return colliderBody;
				}
			} else {
				return colliderBody.lastCollided.get(0).pieceCollider;
			}
		}

		return null;
	}

	@Override
	public void onAddedToScene() {
		colliderBody.onAddedToScene(this);
	}

	/**
	 * Add damage to pieces, weakening with square of distance
	 * 
	 * @param source source of damage
	 * @param damage damage at full strength
	 * @param distMultiplier distance multiplier
	 * @param range max distance of destruction
	 */
	public void piecesAddDamageSquare(ColliderSphere source, double damage, double distMultiplier, double range) {
		if (body.shieldSystem.forceFieldActive) damage *= 0.3;
		for (Piece p : body.allPieces) {
			if (p.isDead) continue;

			double dist = p.getPieceCollider().getPos().distTo(source.getPos());
			dist -= source.radius;
			dist -= p.getPieceCollider().radius;

			if (dist < 0) dist = 0;

			if (dist > range) continue;

			dist *= distMultiplier;

			if (dist < 1) dist = 1;

			p.addDamage((damage) / (dist * dist));
		}
		body.checkIntegrity();

		if (body.isDead) {
			setDead();
			onDeath();
		}
	}

	@Override
	public void addDamage(IDamageable source, double points) {
		if (Double.isNaN(points)) return;
		ArrayList<Piece> p = colliderBody.lastCollided;
		if (p != null) {
			for (Piece pp : p) {
				pp.addDamage(points / p.size());
			}

			if (body.isDead) {
				setDead();
				onDeath();
			}

		} else if (colliderBody.collidingShield) {
			if (source != this && (!source.isDead() || source.getType() == EEntity.SHOT_BAD)) {
				Sounds.shield_hit.playEffect(0.6f + rand.nextFloat() * 0.6f, 0.12f, false, getPos());

				double energy = body.shieldSystem.shieldEnergy;

				double neededToKill = source.getHealth();

				double killCost = neededToKill * 100; // - body.shieldSystem.level * 10);

				if (energy >= killCost) {
					body.shieldSystem.shieldEnergy -= killCost;
					source.addDamage(this, neededToKill);
				} else {
					double consumed = energy;

					source.addDamage(this, neededToKill * (consumed / killCost) * 0.5);
				}

				IPhysEntity hit = (IPhysEntity) source;

				Vec move = getPos().vecTo(hit.getPos());

				Coord midpoint = getPos().add(move.norm(collider.radius));

				if (!hit.isDead()) {
					explodeForce(midpoint, 0.02, false);
				}
			}
		}

		if (body.isDead) {
			setDead();
			onDeath();
		}
	}

	@Override
	public void onImpact(Entity hitBy) {}

	@Override
	public void onUpdate() {
		body.update();

		if (angleInc != 0) {
			double inc = Math.min(1, Math.abs(angleInc)) * Constants.SPEED_MUL;
			if (Math.abs(rotAngle.d) > MAXROT) {
				rotAngle.d = Calc.clampd(rotAngle.d, -MAXROT, MAXROT);
				angleInc = 0;
				rotAngle.pushLast();
			} else {
				rotAngle.d += Calc.sgn(angleInc) * inc;
				angleInc -= Calc.sgn(angleInc) * inc;
			}
		}

		if (rotationCenterRequested) {
			if (Math.abs(rotAngle.d) > 2) {
				rotAngle.d += Calc.sgn(0 - rotAngle.d) * 1 * Constants.SPEED_MUL;
			} else {
				rotAngle.d = 0;
				rotationCenterRequested = false;
			}
		}


	}


	@Override
	public void render(double delta) {
		body.render(delta);
	}

	@Override
	public boolean belongsToZone(double zFrom, double zTo) {
		return Calc.inRange(collider.pos.z, zFrom - collider.radius, zTo + collider.radius);
	}

	@Override
	public void onDeath() {
		explodeForce(getPos(), 20, true);
	}

	@Override
	public EEntity getType() {
		return EEntity.PLAYER;
	}

	@Override
	public void addScore(int points) {
		cursor.addScore(points);
	}

	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {
		body.onMouseMove(pos, move, wheelDelta);
	}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		body.onMouseButton(button, down, wheelDelta, pos, deltaPos);
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		body.onKey(key, c, down);
	}

	@Override
	public void handleStaticInputs() {
		body.handleStaticInputs();
	}

	/**
	 * Get acceleration for GUI, based on number of engines and their level.
	 * 
	 * @return acceleration
	 */
	public double getAcceleration() {
		return 0.05 * body.countEnginesSq() / (mass / 16);
	}

	/**
	 * Get deceleration for GUI, based on number of engines and their level.
	 * 
	 * @return deceleration
	 */
	public double getDecelerate() {
		return 0.015 * (1 + body.countEnginesSq() / (mass / 16));
	}

	@Override
	public double getEmpSensitivity() {
		return 0;
	}

	@Override
	public double getFireFlammability() {
		return 0;
	}

	@Override
	public double getFireSensitivity() {
		return 0;
	}

	@Override
	public double getHealthMax() {
		return 1;
	}

}
