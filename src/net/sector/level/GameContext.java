package net.sector.level;


import net.sector.level.sequence.LevelController;
import net.sector.network.levels.NetLevelContainer;


/**
 * Game context
 * 
 * @author MightyPork
 */
public class GameContext {

	/** current level */
	private LevelController currentLevel;

	/** Current game cursor */
	public GameCursor cursor;

	/** Saved cursor (when last left designer) */
	private GameCursor savedCursor;

	/** Net level container */
	public NetLevelContainer netLevelContainer = null;

	/** game type */
	public ELevel levelType = null;

	/** current level bundle (to get information about the level) */
	public LevelBundle levelBundle;

	/**
	 * Create new game context.
	 */
	public GameContext() {}

//	
//	public File getHighscoreFile() {
//		return levelBundle.getHighscoreFile();
//	}

	/**
	 * Save ship state after entering or leaving ship editor. Game can then be
	 * saved to disk and nothing will get lost.
	 */
	public void saveCursor() {
		savedCursor = cursor.copy();
	}

	/**
	 * Save cursor for next level start in bundle + in saved slot.
	 */
	public void saveCursorInBundle() {
		saveCursor();
		levelBundle.saveCursorToBundle(cursor.copy());
	}

	/**
	 * Save ship to file
	 */
	public void saveShipToFile() {
		levelBundle.saveShipToFile(cursor.shipBundle);
	}

	/**
	 * restore from saved state.
	 */
	public void restoreCursor() {
		if (savedCursor == null) return;
		cursor = savedCursor.copy();
	}

	/**
	 * Get last saved ship state.
	 * 
	 * @return last ship bundle
	 */
	public GameCursor getSavedCursor() {
		return savedCursor;
	}

	/**
	 * Get current level controller
	 * 
	 * @return level
	 */
	public LevelController getLevelController() {
		return currentLevel;
	}

	/**
	 * Get current cursor
	 * 
	 * @return cursor
	 */
	public GameCursor getCursor() {
		return cursor;
	}

	/**
	 * Set cursor
	 * 
	 * @param cursor cursor to set
	 */
	public void setCursor(GameCursor cursor) {
		this.cursor = cursor;
		cursor.setContext(this);
	}

	/**
	 * Set level controller.
	 * 
	 * @param level
	 */
	public void setLevelController(LevelController level) {
		this.currentLevel = level;
	}

	/**
	 * Set level type.
	 * 
	 * @param elevel level type enum
	 */
	public void setLevelType(ELevel elevel) {
		this.levelType = elevel;
	}

	/**
	 * Set level bundle, containing information about level
	 * 
	 * @param levelBundle level bundle to set
	 */
	public void setLevelBundle(LevelBundle levelBundle) {
		this.levelBundle = levelBundle;
	}

}
