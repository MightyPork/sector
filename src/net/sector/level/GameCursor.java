package net.sector.level;


import net.sector.level.sequence.LevelController;
import net.sector.level.ship.DiscoveryTable;
import net.sector.level.ship.ShipBundle;


/**
 * This is a game cursor, something that indicates where in the level the player
 * is, what ship he has, what's his current money and score, discoveries and
 * other stuff.<br>
 * Current level and set of levels is handled by the GameContext.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class GameCursor {

	/** Parent game context */
	public GameContext context = null;

	/** Current player ship bundle (before starting current level) */
	public ShipBundle shipBundle;

	/** Total score collected (for high-score table) */
	public int scoreTotal = 0;

	/** Money = score collected that can be used in designer to buy stuff */
	public int money = 0;

	/** Newly collected artifacts, to be spent in the discovery table */
	public int newArtifacts = 0;

	/** Discovery table */
	public DiscoveryTable discoveryTable;

	/** Designer building mode */
	public EBuildingMode buildMode = EBuildingMode.NORMAL;


	/**
	 * Make a cursor.
	 * 
	 * @param ctx game context
	 */
	public GameCursor(GameContext ctx) {
		context = ctx;
	}

	/**
	 * Make a cursor - with null context
	 */
	public GameCursor() {}

	/**
	 * Create cursor as copy of other cursor
	 * 
	 * @param other
	 */
	public GameCursor(GameCursor other) {
		shipBundle = other.shipBundle.copy();
		scoreTotal = other.scoreTotal;
		money = other.money;
		discoveryTable = other.discoveryTable.copy();
		context = other.context;
		newArtifacts = other.newArtifacts;
		buildMode = other.buildMode;
	}

	/**
	 * Get current level controller
	 * 
	 * @return the level controller
	 */
	public LevelController getLevel() {
		if (context == null) return null;
		return context.getLevelController();
	}

	/**
	 * Get deep copy
	 * 
	 * @return copy
	 */
	public GameCursor copy() {
		return new GameCursor(this);
	}

	/**
	 * Add artifact(s)
	 * 
	 * @param artifacts number of artifacts
	 */
	public void addArtifact(int artifacts) {
		newArtifacts += artifacts;
	}

	/**
	 * Add player score
	 * 
	 * @param points score points
	 */
	public void addScore(int points) {
		money += points;
		scoreTotal += points;
	}

	/**
	 * Set game context
	 * 
	 * @param ctx game context
	 * @return this
	 */
	public GameCursor setContext(GameContext ctx) {
		this.context = ctx;
		return this;
	}

}
