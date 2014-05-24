package net.sector.level.ship.modules;


import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import net.sector.GameConfig;
import net.sector.collision.Collider;
import net.sector.collision.ColliderPlayerShip;
import net.sector.collision.Scene;
import net.sector.effects.Effects;
import net.sector.input.EInput;
import net.sector.input.IInputHandler;
import net.sector.input.InputTriggerGroup;
import net.sector.input.Routine;
import net.sector.level.ship.PieceBundle;
import net.sector.level.ship.modules.pieces.Piece;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Container for ship body pieces
 * 
 * @author MightyPork
 */
public class ShipBody implements IInputHandler {

	/** Distance of pieces, size of grid cells. */
	public static final double pieceDist = 0.3d;
	/** Defualt size of piece collider */
	public static final double pieceColliderSize = pieceDist * Calc.SQ2 * 0.9;
	/** Default piece render size */
	public static final double pieceRenderSize = pieceDist / 2;

	/** Set of all the pieces */
	public LinkedHashSet<Piece> allPieces = new LinkedHashSet<Piece>();


	/** Center piece - coord in the grid. */
	public CoordI center = new CoordI(0, 0);

	private int cnt = 0;

	/** Wrapping collider */
	public ColliderPlayerShip collider;

	/**
	 * Energy level. Set here so that when ship is spawned, this number is used.
	 */
	public int energyLevel = 1;
	/** Power system */
	public EnergySystem energySystem;

	private boolean initcheck = false;
	private long initTime = 0;


	/** body is dead → entity should die too */
	public boolean isDead = false;

	private Random rand = new Random();

	private boolean shieldActive = false;

	/**
	 * Shield level. Set here so that when ship is spawned, this number is used.
	 */
	public int shieldLevel = 1;

	/** Shield */
	public Shield shieldSystem;

	/** Grid size X */
	public int sizeX = 0;

	/** Grid size Z */
	public int sizeZ = 0;

	/**
	 * Table of pieces Unlike table in Context, this has inverted Z axis.
	 */
	private Piece[][] table;

	private InputTriggerGroup triggers = new InputTriggerGroup();

	/**
	 * New piece store
	 * 
	 * @param width grid width (x)
	 * @param height grid height (z)
	 */
	public ShipBody(int width, int height) {
		table = new Piece[height][width];
		sizeX = width;
		sizeZ = height;
	}

	/**
	 * Count engines
	 * 
	 * @return engines count
	 */
	public int countEngines() {
		int e = 0;
		for (Piece pc : allPieces) {
			if (pc == null || pc.isDead) continue;
			if (pc.isEngine()) e++;
		}
		return e;
	}

	/**
	 * Count engines - total thrust power
	 * 
	 * @return engines power
	 */
	public double countEnginesSq() {
		double e = 0;
		for (Piece pc : allPieces) {
			if (pc == null || pc.isDead) continue;
			if (pc.isEngine()) e += pc.getEnginePoints();
		}
		return e;
	}

	/**
	 * Count weapons.
	 * 
	 * @return weapons.
	 */
	public int countWeapons() {
		int w = 0;
		for (Piece pc : allPieces) {
			if (pc == null || pc.isDead) continue;
			if (pc.isWeapon()) w++;
		}
		return w;
	}

	/**
	 * Enable shield
	 * 
	 * @param b state
	 */
	public void enableShield(boolean b) {
		shieldActive = b;
		shieldSystem.forceFieldActive = b;
	}

	/**
	 * Get piece colliding with given collider.
	 * 
	 * @param other other collider
	 * @return the colliding piece
	 */
	public ArrayList<Piece> getCollidingPieces(Collider other) {
		ArrayList<Piece> colliding = new ArrayList<Piece>();
		for (Piece pc : allPieces) {
			if (pc == null || pc.isDead) continue;
			if (pc.collidesWith(this, other, collider.pos, collider.getRotY(0))) colliding.add(pc);
		}
		return colliding;
	}

	/**
	 * Get piece in grid
	 * 
	 * @param x cell x
	 * @param z cell z
	 * @return piece or null
	 */
	public Piece getPiece(int x, int z) {
		if (x < 0 || x >= table[0].length || z < 0 || z >= table.length) {
			return null;
		}
		Piece p = table[z][x];
		if (p == null || p.isDead) return null;
		return p;
	}

	/**
	 * Get the scene
	 * 
	 * @return scene
	 */
	public Scene getScene() {
		return collider.scene;
	}

	@Override
	public void handleStaticInputs() {
		triggers.handleStaticInputs();
		for (Piece p : allPieces) {
			p.handleStaticInputs();
		}
	}

	/**
	 * Check ship integrity.
	 */
	public void checkIntegrity() {
		for (Piece pc : allPieces) {
			if (pc.isDead) continue;
			pc.consistencyCheckFlag = false;
		}
		markNeighborConsistent(center.x, center.y);
		for (Piece pc : allPieces) {
			if (pc.isDead) continue;
			if (!pc.consistencyCheckFlag) {
				pc.isDead = true;
				onPieceDestroyed(pc, false);
			}
		}
	}

	private void markNeighborConsistent(int x, int z) {
		Piece p;
		p = getPiece(x, z);
		if (p == null || p.isDead) return;

		for (int xx = -1; xx <= 1; xx++) {
			for (int zz = -1; zz <= 1; zz++) {
				if (xx == 0 && zz == 0) continue;
				//if (!p.canConnectToSide(xx, zz)) continue;

				Piece p2 = getPiece(x + xx, z + zz);

				int dx = -xx;
				int dz = -zz;

				if (p2 != null && !p2.consistencyCheckFlag && p2.canConnectToSide(dx, dz)) {
					p2.consistencyCheckFlag = true;
					markNeighborConsistent(x + xx, z + zz);
				}
			}
		}
	}

	/**
	 * Check if shield is active
	 * 
	 * @return shield is active
	 */
	public boolean isShieldRequestedByPlayer() {
		return shieldActive;
	}

	/**
	 * Check if shield is active
	 * 
	 * @return shield is active
	 */
	public boolean isShieldRunning() {
		return isShieldRequestedByPlayer() && shieldSystem.forceFieldActive && shieldSystem.level > 0;
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		triggers.onKey(key, c, down);
		for (Piece p : allPieces) {
			p.onKey(key, c, down);
		}
	}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		triggers.onMouseButton(button, down, wheelDelta, pos, deltaPos);

		for (Piece p : allPieces) {
			p.onMouseButton(button, down, wheelDelta, pos, deltaPos);
		}
	}

	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {
		// NO-OP
	}

	/**
	 * Handle piece destruction (explode, integrity check etc..)
	 * 
	 * @param piece piece destroyed
	 * @param check perform integrity check
	 */
	public void onPieceDestroyed(Piece piece, boolean check) {
		Effects.addExplosion(getScene().particles, piece.getAbsoluteCoord(), collider.entity.getMotion(), piece.explodeSize * (check ? 1 : 0.3),
				true, false);
		if (getPiece(center.x, center.y) == null || countWeapons() == 0 || countEngines() == 0) {
			isDead = true;
			return;
		}

		if (check) {
			checkIntegrity();
		}
	}

	/**
	 * Hook called when all is ready: scene, ship, collider != null
	 */
	public void onReady() {
		shieldSystem = new Shield(this);
		shieldSystem.setColor(new RGB(0.1, 0.7, 1, Calc.clampd(0.2 * shieldLevel, 0.2, 0.6)));
		shieldSystem.onReady();
		shieldSystem.setLevel(shieldLevel);

		energySystem = new EnergySystem(energyLevel);


		triggers.addTrigger(new Routine() {
			@Override
			public void run() {
				enableShield(true);
			}
		}, EInput.KEY_PRESS, GameConfig.keyShield);

		triggers.addTrigger(new Routine() {
			@Override
			public void run() {
				enableShield(false);
			}
		}, EInput.KEY_RELEASE, GameConfig.keyShield);

	}

	/**
	 * Remove piece from grid
	 * 
	 * @param x cell x
	 * @param z cell z
	 */
	public void removePiece(int x, int z) {
		Piece pc = getPiece(x, z);
		if (pc == null) return;
		allPieces.remove(pc);
		table[z][x] = null;
		pc = null;
	}

	/**
	 * Remove piece if contained
	 * 
	 * @param piece piece to remove
	 */
	public void removePiece(Piece piece) {
		table[piece.gridCoord.y][piece.gridCoord.x] = null;
		allPieces.remove(piece);
		piece = null;
	}

	/**
	 * Render the ship at current pos.
	 * 
	 * @param delta delta time
	 */
	public void render(double delta) {
		if (isDead) return;

		//@formatter:off
		glPushMatrix();
			glLoadIdentity();
			Coord  p = collider.pos.getDelta(delta);
			glTranslated(p.x, p.y, -p.z);
			
			
			glPushMatrix();
			glRotated(collider.getRotY(delta), 0, 1, 0);
			glRotated(collider.getRotZ(delta), 0, 0, 1);
						
			for (Piece pc : allPieces) {
				if (pc.isDead) continue;
				pc.render(this);
			}
			shieldSystem.render(delta);
			glPopMatrix();
			
		glPopMatrix();
		//@formatter:on
	}

	/**
	 * Set grid coordinates to work as center (for rotation & consistency
	 * checking)
	 * 
	 * @param x cell x
	 * @param y cell y
	 */
	public void setCenterCoord(int x, int y) {
		center.setTo(x, y);
	}

	/**
	 * Set instance of wrapping collider.
	 * 
	 * @param colliderPieced
	 */
	public void setCollider(ColliderPlayerShip colliderPieced) {
		this.collider = colliderPieced;
	}

	/**
	 * Set grid piece
	 * 
	 * @param x cell x
	 * @param z cell z
	 * @param piece piece to add
	 */
	public void setPiece(int x, int z, Piece piece) {
		if (piece == null) {
			table[sizeZ - 1 - z][x] = null;
			return;
		}
		piece.setStore(this);
		table[sizeZ - 1 - z][x] = piece;
		allPieces.add(piece);
		piece.setGridCoord(x, sizeZ - 1 - z);
		piece.setColliderRadius(pieceColliderSize);
	}

	/**
	 * Convert back to table used in designer.
	 * 
	 * @return table
	 */
	public PieceBundle[][] toTable() {
		PieceBundle[][] s = new PieceBundle[sizeZ][sizeX];
		int Z = sizeZ;
		int X = sizeX;

		for (int z = 0; z < Z; z++) {
			for (int x = 0; x < X; x++) {
				Piece p = table[Z - 1 - z][x];
				s[z][x] = new PieceBundle(p);
			}
		}

		return s;
	}

	/**
	 * Update tick..
	 */
	public void update() {
		if (isDead) return;

		if (initTime == 0) initTime = System.currentTimeMillis();

		if (!initcheck && System.currentTimeMillis() - initTime > 1000) {
			checkIntegrity();
			initcheck = true;
		}

		if (rand.nextInt(3) == 0) {
			// shuffle pieces
			List<Piece> list = new ArrayList<Piece>(allPieces);
			Collections.shuffle(list);
			allPieces.clear();
			allPieces.addAll(list);
		}

		energySystem.update();
		shieldSystem.update();

		for (Piece p : allPieces) {
			p.update();
		}
	}
}
