package net.sector.level.ship.modules.pieces;


import static org.lwjgl.opengl.GL11.*;
import net.sector.collision.Collider;
import net.sector.collision.ColliderSphere;
import net.sector.collision.Scene;
import net.sector.entities.Entity;
import net.sector.entities.player.EntityPlayerShip;
import net.sector.input.IInputHandler;
import net.sector.input.InputTrigger;
import net.sector.input.Routine;
import net.sector.input.TriggerBundle;
import net.sector.level.ship.PieceRegistry;
import net.sector.level.ship.modules.EnergySystem;
import net.sector.level.ship.modules.ShipBody;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Deg;


/**
 * Ship piece
 * 
 * @author MightyPork
 */
public abstract class Piece implements IInputHandler {

	/** Wrapping store. */
	public ShipBody body = null;

	/** Health points. */
	public double health = 3;

	/**
	 * Size of explosion when destroyed, can be increased for fuel tanks etc,
	 * decreased for wings & small pieces.)
	 */
	public double explodeSize = 5;

	/** Flag for consistency check at {@link ShipBody} */
	public boolean consistencyCheckFlag = false;

	/** Mark that is dead, for collisions etc. */
	public boolean isDead = false;

	/**
	 * Technical level - the higher the better functions, lower load time,
	 * bigger damage
	 */
	public int techLevel = 1;

	/** Piece collider, set to correct values after calling collidesWith. */
	public ColliderSphere pieceCollider = new ColliderSphere(new Coord(0, 0, 0), 1);

	/** Coordinate in PieceStore's table */
	public CoordI gridCoord = new CoordI(0, 0);

	/** rotation of this individual piece (block rotation) */
	public double pieceRotate = 0;

	/** trigger used to detect controls */
	public InputTrigger inputTrigger = null;

	/** Action executed if the trigger is triggered */
	public Routine inputAction = null;

	public abstract double getPieceMass();

	/**
	 * For designer - increment for rotating. 15 is for weapons, 90 is for body
	 * pieces.
	 * 
	 * @return step in degrees
	 */
	public abstract int getPieceRotateStep();

	/**
	 * Get price of piece level 1
	 * 
	 * @return price
	 */
	public abstract int getBaseCost();


	/**
	 * Ship body piece
	 */
	public Piece() {
		health = getHealthMax();
	}

	/**
	 * Set tech level. Gets clamped to 1-max
	 * 
	 * @param level level to set
	 * @return this
	 */
	public Piece setLevel(int level) {
		techLevel = Calc.clampi(level, 1, getLevelMax());
		return this;
	}

	/**
	 * Get tech level
	 * 
	 * @return tech level
	 */
	public int getLevel() {
		return techLevel;
	}

	/**
	 * Get maximal tech level
	 * 
	 * @return tech level
	 */
	public final int getLevelMax() {
		return PieceRegistry.getPieceLevelMax(this);
	}

	/**
	 * Get scene instance
	 * 
	 * @return the scene
	 */
	public final Scene getScene() {
		return body.getScene();
	}

	/**
	 * Add entity to scene (for shots)
	 * 
	 * @param entity
	 */
	public final void addEntityToScene(Entity entity) {
		getScene().add(entity);
	}

	/**
	 * Get ship entity
	 * 
	 * @return the entity
	 */
	public final EntityPlayerShip getEntity() {
		return (EntityPlayerShip) body.collider.entity;
	}

	/**
	 * Get energy system
	 * 
	 * @return system
	 */
	public final EnergySystem getEnergySystem() {
		return body.energySystem;
	}

	/**
	 * Try to consume energy
	 * 
	 * @param points points needed
	 * @return consumed
	 */
	public final boolean tryToConsumeEnergy(double points) {
		return getEnergySystem().tryToConsume(points);
	}

	/**
	 * Get ship rotation around Y axis
	 * 
	 * @return Y rotation
	 */
	public final double getRotY() {
		return body.collider.getRotY(0);
	}

	/**
	 * Get ship rotation around Z axis (to sides)
	 * 
	 * @return Z rotation
	 */
	public final double getRotZ() {
		return body.collider.getRotZ(0);
	}

	/**
	 * get ship position
	 * 
	 * @return position
	 */
	public final Coord getPos() {
		return body.collider.pos;
	}

	/**
	 * Get sphere collider for this piece
	 * 
	 * @return this piece's collider
	 */
	public final ColliderSphere getPieceCollider() {
		return pieceCollider;
	}

	/**
	 * Assign store to this piece
	 * 
	 * @param store the piece store
	 * @return this
	 */
	public final Piece setStore(ShipBody store) {
		this.body = store;
		return this;
	}

	/**
	 * Set piece health
	 * 
	 * @param health health points
	 * @return this
	 */
	public final Piece setHealth(double health) {
		this.health = Calc.clampd(health, 0, getHealthMax());
		return this;
	}

	/**
	 * Add health
	 * 
	 * @param points health points
	 * @return this
	 */
	public final Piece addHealth(double points) {
		this.health = Calc.clampd(health + points, 0, getHealthMax());
		return this;
	}


	/**
	 * Add damage to this piece, explode and check integrity if destroyed.
	 * 
	 * @param points points of damage
	 * @return this
	 */
	public final Piece addDamage(double points) {
		if (isDead) return this;
		health -= points / getResistance();
		if (health <= 0) {
			isDead = true;
			body.onPieceDestroyed(this, true);
		}
		return this;
	}

	/**
	 * Get health limit
	 * 
	 * @return health points max
	 */
	public abstract double getHealthMax();

	/**
	 * Get piece resistance, based on tech level. Higher = better. Default = 1,
	 * 0.5 = half, 4 = 4x stronger
	 * 
	 * @return resistance
	 */
	public abstract double getResistance();

	/**
	 * Get coordinate of this piece relative to grid center (in real units)
	 * 
	 * @return the coordinate
	 */
	public final Coord getRelativeCoordToCenter() {
		CoordI abs = getGridCoord().sub(body.center);
		return new Coord(abs.x, 0, abs.y);
	}

	/**
	 * Get coordinate of this piece in absolute units - relative to point
	 * (0,0,0)
	 * 
	 * @return the coord
	 */
	public final Coord getAbsoluteCoord() {
		Coord relative = getRelativeCoordToCenter();
		Coord scaledRelative = relative.mul(ShipBody.pieceDist);

		// here we rotate it around Z.

		scaledRelative.y += scaledRelative.x * Math.sin(Math.toRadians(getRotZ()));
		scaledRelative.x = scaledRelative.x * Math.cos(Math.toRadians(getRotZ()));


		Coord scaledRotatedRelative = CoordUtils.coordToLocalSystem(getRotY(), scaledRelative);
		return getPos().add(scaledRotatedRelative);
	}

	/**
	 * Get rendering model.<br>
	 * Used to get static field Model by super type.
	 * 
	 * @return model
	 */
	public abstract RenderModel getModel();

	/**
	 * Set model collider radius.<br>
	 * Should be slightly bigger than gridScale;
	 * 
	 * @param radius collider radius
	 */
	public final void setColliderRadius(double radius) {
		pieceCollider.radius = radius;
	}

	/**
	 * Set coordinate within PieceStore's table.<br>
	 * 
	 * @param x x coord
	 * @param y y coord
	 */
	public final void setGridCoord(int x, int y) {
		gridCoord.setTo(x, y);
	}

	/**
	 * Check if this part collides woth other entity
	 * 
	 * @param store piece store
	 * @param other other collider - usually shot / asteroid
	 * @param realCenterPos center of the entity, aligned with table center
	 * @param rotAngle entity rotate angle
	 * @return collides
	 */
	public final boolean collidesWith(ShipBody store, Collider other, Coord realCenterPos, double rotAngle) {
		if (isDead) return false;

		pieceCollider.pos.setTo(getAbsoluteCoord());

		//check collider collision
		return pieceCollider.collidesWith(other);
	}

	/**
	 * Get relative render scale multiplier.
	 * 
	 * @return 1 for default size.
	 */
	public abstract double getRenderScale();

	/**
	 * do render. relative to ship center and rotation, with push-pop.
	 * 
	 * @param store piece store
	 */
	public void render(ShipBody store) {
		if (isDead) return;
		Coord relpos = getRelativeCoordToCenter();
		glPushMatrix();
		glTranslated(relpos.x * ShipBody.pieceDist, 0, -relpos.z * ShipBody.pieceDist);
		double d = getRenderScale();
		glScaled(ShipBody.pieceRenderSize * d, ShipBody.pieceRenderSize * d, ShipBody.pieceRenderSize * d);
		glRotated(pieceRotate, 0, 1, 0);

		getModel().render();

		glPopMatrix();
	}

	/**
	 * Get coord in grid
	 * 
	 * @return coord
	 */
	public final CoordI getGridCoord() {
		return gridCoord;
	}

	// input handling

	/**
	 * Get if piece has input trigger configurable
	 * 
	 * @return has trigger
	 */
	public boolean hasTrigger() {
		return inputTrigger != null && inputAction != null;
	}

	/**
	 * Get trigger bundle
	 * 
	 * @return trigger bundle
	 */
	public TriggerBundle getTrigger() {
		if (!hasTrigger()) return null;
		return inputTrigger.toBundle();
	}

	/**
	 * Set trigger
	 * 
	 * @param trigger trigger bundle
	 */
	public void setTrigger(TriggerBundle trigger) {
		inputTrigger = trigger.toTrigger();
	}

	/**
	 * Get triggered action (eg. shoot action)
	 * 
	 * @return action
	 */
	public Routine getAction() {
		return inputAction;
	}

	/**
	 * Set triggered action (eg. shoot action)
	 * 
	 * @param fn action
	 */
	public void setAction(Routine fn) {
		inputAction = fn;
	}

	/**
	 * Init triggered action
	 * 
	 * @param tb trigger bundle
	 * @param fn action
	 */
	public void initAction(TriggerBundle tb, Routine fn) {
		setTrigger(tb);
		setAction(fn);
	}

	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		if (isDead || getEntity().isDead() || body.isDead) return;
		if (hasTrigger() && inputTrigger.onMouseButton(button, down, wheelDelta, pos, deltaPos)) {
			inputAction.run();
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		if (isDead || getEntity().isDead() || body.isDead) return;
		if (hasTrigger() && inputTrigger.onKey(key, c, down)) {
			inputAction.run();
		}
	}

	@Override
	public void handleStaticInputs() {
		if (isDead || getEntity().isDead() || body.isDead) return;
		if (hasTrigger() && inputTrigger.handleStaticInputs()) {
			inputAction.run();
		}
	}

	/**
	 * Update what needs to be updated..
	 */
	public void update() {}

	/**
	 * Check if this piece can connect to given side
	 * 
	 * @param x x offset
	 * @param z z offset
	 * @return can connect
	 */
	public abstract boolean canConnectToSide(int x, int z);

	/**
	 * Set piece rotation
	 * 
	 * @param rotate rotation (deg CCW)
	 * @return this
	 */
	public Piece setPieceRotate(int rotate) {
		this.pieceRotate = Deg.norm(rotate);
		return this;
	}

//	/**
//	 * Get render-model for designer
//	 * 
//	 * @return model
//	 */
//	public RenderModel getModelForDesigner() {
//		return getModel();
//	}

	/**
	 * Is this piece an engine?
	 * 
	 * @return is engine
	 */
	public boolean isEngine() {
		return false;
	}

	/**
	 * Get thrust points (engine)
	 * 
	 * @return thrust points (1 = level 1 engine)
	 */
	public double getEnginePoints() {
		return 0;
	}

	/**
	 * Is this piece a weapon?
	 * 
	 * @return is weapon
	 */
	public boolean isWeapon() {
		return false;
	}

	/**
	 * Is a body piece (= can occupy central slot)
	 * 
	 * @return is body
	 */
	public boolean isBody() {
		return false;
	}


	/**
	 * Get health.
	 * 
	 * @return health
	 */
	public double getHealth() {
		return health;
	}

	/**
	 * Get rotation
	 * 
	 * @return rotation
	 */
	public double getPieceRotate() {
		return pieceRotate;
	}
}
