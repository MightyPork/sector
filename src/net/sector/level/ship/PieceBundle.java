package net.sector.level.ship;


import java.util.Random;

import net.sector.input.TriggerBundle;
import net.sector.level.ship.modules.pieces.Piece;

import com.porcupine.math.Calc;


/**
 * Object describing ship piece
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PieceBundle {
	/** RNG */
	public static Random rand = new Random();

	/** Base cost (at level 1) */
	public int baseCost = 0;
	/** Piece health */
	public double health = 10;
	/** Piece health max */
	public double healthMax = 10;
	/** Current piece level */
	public int level = 1;
	/** Highest level possible */
	public int levelMax = 1;
	/** Piece name */
	public String id = "";
	/** Piece rotation (CCW, deg) */
	public int rotate = 0;
	/** Smoothest piece rotation step (in designer) */
	public int rotStep = 90;
	/** Flag that this is an engine */
	public boolean typeEngine = false;
	/** Flag that this is a weapon */
	public boolean typeWeapon = false;
	/** Flag that this is a body piece */
	public boolean typeBody = false;
	/** Assigned trigger bundle */
	public TriggerBundle trigger = null;
	/** Flag that this piece is controlled */
	public boolean hasTrigger = false;

	@Override
	protected PieceBundle clone() throws CloneNotSupportedException {
		return new PieceBundle(this);
	}

	/**
	 * Create new bundle from piece
	 * 
	 * @param p piece
	 */
	public PieceBundle(Piece p) {
		this.id = PieceRegistry.getPieceName(p);
		level = p.getLevel();
		levelMax = p.getLevelMax();
		health = p.getHealth();
		healthMax = p.getHealthMax();
		rotate = (int) p.getPieceRotate();
		rotStep = p.getPieceRotateStep();
		baseCost = p.getBaseCost();
		typeEngine = p.isEngine();
		typeWeapon = p.isWeapon();
		typeBody = p.isBody();
		hasTrigger = p.hasTrigger();
		if (hasTrigger) trigger = p.getTrigger().copy();
	}

	/**
	 * Create piece as copy of another
	 * 
	 * @param other
	 */
	public PieceBundle(PieceBundle other) {
		id = other.id;
		level = other.level;
		levelMax = other.levelMax;
		health = other.health;
		healthMax = other.healthMax;
		rotate = other.rotate;
		rotStep = other.rotStep;
		baseCost = other.baseCost;
		typeWeapon = other.typeWeapon;
		typeEngine = other.typeEngine;
		typeBody = other.typeBody;
		hasTrigger = other.hasTrigger;
		if (hasTrigger) trigger = other.trigger.copy();
	}

	/**
	 * Create piece for name
	 * 
	 * @param id
	 */
	public PieceBundle(String id) {
		this(PieceRegistry.makePiece(id));
	}

	/**
	 * Create new bundle
	 * 
	 * @param id name
	 * @param level level
	 * @param rotate rotation
	 */
	public PieceBundle(String id, int level, int rotate) {
		this.id = id;
		this.level = level;
		this.rotate = rotate;
		fixFieldValuesSetMaxHealth();
	}

	/**
	 * Create new bundle
	 * 
	 * @param id name
	 * @param level level
	 * @param rotate rotation
	 * @param health health
	 */
	public PieceBundle(String id, int level, int rotate, double health) {
		this.id = id;
		this.level = level;
		this.rotate = rotate;
		fixFieldValuesSetMaxHealth();
		this.health = Calc.clampd(health, 0, this.healthMax);
	}

	/**
	 * Check if piece can be downgraded
	 * 
	 * @return state
	 */
	public boolean canBeDowngraded() {
		return level > 1;
	}

	/**
	 * Check if piece can be upgraded
	 * 
	 * @return state
	 */
	public boolean canBeUpgraded() {
		return level < levelMax;
	}


	/**
	 * Duplicate
	 * 
	 * @return copy
	 */
	public PieceBundle copy() {
		return new PieceBundle(this);
	}

	/**
	 * Get how much a level change would cost
	 * 
	 * @param side 1 for upgrade, -1 for downgrade
	 * @return cost price (positive = consumed, negative = gained, 0 = not
	 *         applicable)
	 */
	public int getLevelChangeCost(int side) {
		if (side > 0 && !canBeUpgraded()) return 0;
		if (side < 0 && !canBeDowngraded()) return 0;
		return PieceRegistry.getLevelChangeCost(baseCost, level, level + (side > 0 ? 1 : -1));
	}

	/**
	 * Get how much a piece repair would cost
	 * 
	 * @return price
	 */
	public int getRepairCost() {
		return PieceRegistry.getRepairCost(baseCost, level, health, healthMax);
	}

	/**
	 * Get piece total value
	 * 
	 * @return total value
	 */
	public int getTotalValue() {
		return PieceRegistry.getPieceCost(baseCost, level, health, healthMax);
	}

	/**
	 * Check if piece is damaged
	 * 
	 * @return is damaged
	 */
	public boolean isDamaged() {
		return health < healthMax;
	}

	/**
	 * Get has trigger
	 * 
	 * @return has trigger
	 */
	public boolean hasTrigger() {
		return hasTrigger;
	}

	/**
	 * Get trigger
	 * 
	 * @return trigger
	 */
	public TriggerBundle getTrigger() {
		return trigger;
	}

	/**
	 * Set trigger replacement if already has some
	 * 
	 * @param trigger the new trigger
	 */
	public void setTrigger(TriggerBundle trigger) {
		if (hasTrigger) this.trigger = trigger.copy();

	}

	/**
	 * Create actual piece
	 * 
	 * @return piece
	 */
	public Piece toPiece() {
		Piece p = PieceRegistry.makePiece(id, level, rotate);
		p.setHealth(health);
		if (hasTrigger) p.setTrigger(trigger);
		return p;
	}

	/**
	 * Update after level change, fix invalid values and update MAX fields. Sets
	 * health to healthMax
	 */
	public void fixFieldValuesSetMaxHealth() {
		Piece p = PieceRegistry.makePiece(id, level, rotate);

		level = p.getLevel();
		levelMax = p.getLevelMax();
		health = p.getHealth();
		healthMax = p.getHealthMax();
		//rotate = (int) p.getPieceRotate();
		rotStep = p.getPieceRotateStep();
		baseCost = p.getBaseCost();

		typeEngine = p.isEngine();
		typeWeapon = p.isWeapon();
		typeBody = p.isBody();

		hasTrigger = p.hasTrigger();
		if (hasTrigger && trigger == null) trigger = p.getTrigger().copy();

		p = null;
	}

	@Override
	public String toString() {
		return "PB:" + id + ",l=" + level;
	}

}
