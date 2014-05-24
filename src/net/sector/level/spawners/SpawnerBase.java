package net.sector.level.spawners;


import java.util.Map;
import java.util.Random;

import net.sector.collision.Scene;
import net.sector.level.sequence.LevelController;

import com.porcupine.math.Range;


/**
 * Base for natural stuff generators (asteroids etc.)
 * 
 * @author MightyPork
 */
public abstract class SpawnerBase {
	/** Flag whether this generator is enabled */
	protected boolean enabled = true;

	private LevelController level;

	/** Scene instance */
	protected Scene scene;
	/** Z range */
	protected Range zCoord = new Range(100, 120);
	/** X range */
	protected Range xCoord = new Range(-30, 30);

	/** RNG */
	protected static Random rand = new Random();

	/**
	 * Enable/disable the generator
	 * 
	 * @param state enabled
	 */
	public void enable(boolean state) {
		enabled = state;
	}


	/**
	 * lead generator from XML arguments
	 * 
	 * @param args xml arguments
	 */
	public SpawnerBase(Map<String, Object> args) {
		loadFromXmlArgs(args);
	}

	public void setLevel(LevelController level) {
		this.level = level;
	}

	public LevelController getLevel() {
		return level;
	}

	/**
	 * Entity generator
	 * 
	 * @param scene scene
	 */
	public SpawnerBase(Scene scene) {
		setScene(scene);
	}

	/**
	 * Init from XML arguments
	 * 
	 * @param args map of arguments
	 */
	public abstract void loadFromXmlArgs(Map<String, Object> args);

	/**
	 * Asteroid generator (needs initialization)
	 */
	public SpawnerBase() {}

	/**
	 * Set entity generation zone (usual scene size: x -40 - 40, z 0 - 100)
	 * 
	 * @param x x range (default -30,30)
	 * @param z z range (default 90, 110)
	 */
	public void setZone(Range x, Range z) {
		if (x != null) xCoord.setTo(x);
		if (z != null) zCoord.setTo(z);
	}

	/**
	 * Set the scene
	 * 
	 * @param scene scene
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	/**
	 * Spawn entities if enabled
	 */
	public final void onGameTick() {
		if (enabled) onUpdate();
	}

	/**
	 * Update the generator, eg. spawn some rocks if rand.nextInt() == 100
	 */
	protected abstract void onUpdate();
}
