package net.sector.level.sequence;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sector.collision.Scene;
import net.sector.entities.Entity;
import net.sector.level.SuperContext;
import net.sector.level.drivers.DriverStore;
import net.sector.level.sequence.nodes.NodeList;
import net.sector.level.spawners.SpawnerBase;
import net.sector.util.Log;

import org.jdom2.Element;

import com.porcupine.coord.Vec;


/**
 * Level sequencer with timing, spawners & algorithm
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class LevelController {
	/** Spawners map */
	private Map<String, SpawnerBase> spawners = new HashMap<String, SpawnerBase>();

	/** Enemy waves */
	private Map<String, EnemyWave> waves = new HashMap<String, EnemyWave>();

	private ArrayList<HudMessage> hudMessages = new ArrayList<HudMessage>();

	private NodeList sequence;

	private LevelTimer timer = null;
	private int initialTimer = -1;

	private boolean endReached = false;
	private Scene scene;
	private DriverStore drivers = SuperContext.basicDrivers;

	public void setInitialTimer(int timer) {
		initialTimer = timer;
	}

	/**
	 * Get if there are any messages waiting for display.
	 * 
	 * @return if there are messages
	 */
	public boolean hasHudMessage() {
		return !hudMessages.isEmpty();
	}

	/**
	 * Add a message
	 * 
	 * @param msg message text
	 * @param time time
	 */
	public void addHudMessage(String msg, double time) {
		hudMessages.add(new HudMessage(msg, time));
	}

	/**
	 * Pop one message from list waiting for display.
	 * 
	 * @return one message
	 */
	public HudMessage getOneHudMessage() {
		if (!hasHudMessage()) return null;
		HudMessage one = hudMessages.get(0);
		hudMessages.remove(0);
		return one;
	}

	/**
	 * Create instance of level controller (needed to load from XML)
	 * 
	 * @param scene game scene for level
	 */
	public LevelController(Scene scene) {
		this.scene = scene;
	}

	/**
	 * Create instance of level controller (needed to set Scene)
	 * 
	 * @param tag xml tag to load from
	 */
	public LevelController(Element tag) {
		loadFromXml(tag);
	}

	/**
	 * Create instance of level controller (needed to set Scene and load from
	 * XML)
	 */
	public LevelController() {}

	/**
	 * Load level from XML (the sequence)
	 * 
	 * @param tag
	 * @return this
	 */
	public LevelController loadFromXml(Element tag) {
		sequence = new NodeList(null, this);
		sequence.loadFromXml(tag);
		return this;
	}

	/**
	 * Reset (recycle)
	 */
	public void reset() {
		spawners.clear();
		sequence.reset();
		waves.clear();
		endReached = false;
		timer = null;
		flag_end = false;
		hudMessages.clear();
		if (initialTimer != -1) setTimer(initialTimer);
	}

	/**
	 * Get game scene
	 * 
	 * @return the scene
	 */
	public Scene getScene() {
		return scene;
	}

	/**
	 * Set scene
	 * 
	 * @param scene
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	/**
	 * Pause timer if any
	 */
	public void pause() {
		if (hasTimer()) getTimer().pause();
	}

	/**
	 * Resume timer if any
	 */
	public void resume() {
		if (hasTimer()) getTimer().resume();
	}

	/** flag that "end of level" message has beed logged. */
	private boolean flag_end = false;
	private Vec asteroidMovement = new Vec(0, 0, -1);

	/**
	 * On update
	 */
	public void onGameTick() {

		if (isEnded()) {
			if (!flag_end) Log.f3("Level finished.");
			flag_end = true;
			return;
		}

		if (hasTimer()) getTimer().update();

		if (sequence.execute()) {
			setEnded();
		}

		for (String name : spawners.keySet()) {
			spawners.get(name).onGameTick();
		}
	}

	/**
	 * Is level ended?
	 * 
	 * @return is ended
	 */
	public boolean isEnded() {
		return endReached || (hasTimer() && timer.isFinished());
	}

	/**
	 * End level
	 */
	public void setEnded() {
		endReached = true;
	}

	/**
	 * Add a spawner (replace if exists)
	 * 
	 * @param name spawner name
	 * @param spawner spawner
	 */
	public void addSpawner(String name, SpawnerBase spawner) {
		if (name == null) name = System.nanoTime() + "";
		spawners.put(name.toLowerCase(), spawner);
		spawner.setLevel(this);
	}

	/**
	 * Enable spawner
	 * 
	 * @param name spawner name
	 * @param enabled be enabled
	 */
	public void enableSpawner(String name, boolean enabled) {
		SpawnerBase spawner = spawners.get(name.toLowerCase());
		if (spawner != null) spawner.enable(enabled);
	}

	/**
	 * Add entity to wave (create wave if not exists)
	 * 
	 * @param name wave name
	 * @param entity the added entity
	 */
	public void addToWave(String name, Entity entity) {
		name = name.toLowerCase();
		if (!waves.containsKey(name)) waves.put(name, new EnemyWave());
		waves.get(name).add(entity);
	}

	/**
	 * Get if all entities in wave are dead = wave is empty. Returns true if
	 * wave does not exist.
	 * 
	 * @param name wave name
	 * @return all entities in wave are dead, or wave does not exist
	 */
	public boolean isWaveDead(String name) {
		if (!waves.containsKey(name)) return true;
		return waves.get(name).isDead();
	}
	
	public EnemyWave getWave(String name) {
		return waves.get(name);		
	}

	/**
	 * Get driver store
	 * 
	 * @return driver store
	 */
	public DriverStore getDriverStore() {
		return drivers;
	}

	/**
	 * Set driver store
	 * 
	 * @param drivers driver store to set
	 */
	public void setDriverStore(DriverStore drivers) {
		this.drivers = drivers;
	}

	/**
	 * Get a spawner.
	 * 
	 * @param name spawner name
	 * @return spawner or null
	 */
	public SpawnerBase getSpawner(String name) {
		if (name == null) return null;
		return spawners.get(name.toLowerCase());
	}

	/**
	 * Get if level is timed
	 * 
	 * @return has timer
	 */
	public boolean hasTimer() {
		return timer != null;
	}

	/**
	 * Get the timer if any
	 * 
	 * @return the count-down timer
	 */
	public LevelTimer getTimer() {
		return timer;
	}

	/**
	 * Set countdown timer
	 * 
	 * @param t timer
	 */
	public void setTimer(LevelTimer t) {
		timer = t;
	}

	/**
	 * Set timer time
	 * 
	 * @param seconds countdown seconds
	 */
	public void setTimer(int seconds) {
		timer = new LevelTimer(seconds);
		timer.start();
	}

	/**
	 * Set asteroid movement.
	 * 
	 * @param movement
	 */
	public void setGlobalMovement(Vec movement) {
		asteroidMovement.setTo(movement);
	}

	/**
	 * Get global movement
	 * 
	 * @return global movement scaled to 0.1 (for direct use in entities)
	 */
	public Vec getGlobalMovement() {
		return asteroidMovement.scale(0.1);
	}
}
