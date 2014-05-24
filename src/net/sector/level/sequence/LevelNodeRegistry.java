package net.sector.level.sequence;


import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import net.sector.level.sequence.nodes.*;
import net.sector.util.Log;

import org.jdom2.Element;


/**
 * Entity registry for easier spawning
 * 
 * @author MightyPork
 */
public class LevelNodeRegistry {

	private static Map<String, Class<? extends LevelNodeBase>> nodes = new HashMap<String, Class<? extends LevelNodeBase>>();

	/**
	 * Initialize
	 */
	public static void init() {
		Log.f1("Initializing algorithm node registry...");

		// algorithms + aliases

		nodes.put("repeat", NodeCycle.class);
		nodes.put("loop", NodeCycle.class);
		nodes.put("cycle", NodeCycle.class);

		nodes.put("list", NodeList.class);


		//nodes.put("hud", NodeHudMessage.class);
		nodes.put("msg", NodeHudMessage.class);
		nodes.put("message", NodeHudMessage.class);
		nodes.put("text", NodeHudMessage.class);
		nodes.put("alert", NodeHudMessage.class);
		nodes.put("info", NodeHudMessage.class);


		//nodes.put("passive_shift", NodeSetGlobalMovement.class);
		//nodes.put("shift", NodeSetGlobalMovement.class);
		//nodes.put("scene_shift", NodeSetGlobalMovement.class);
		//nodes.put("movement", NodeSetGlobalMovement.class);
		nodes.put("rockshift", NodeSetGlobalMovement.class);
		//nodes.put("scrolling", NodeSetGlobalMovement.class);
		//nodes.put("move", NodeSetGlobalMovement.class);


		nodes.put("spawn", NodeSpawnEntity.class);
		nodes.put("fleet", NodeSpawnEntity.class);
		nodes.put("squad", NodeSpawnEntity.class);

		nodes.put("pause", NodeWait.class);
		nodes.put("delay", NodeWait.class);
		nodes.put("sleep", NodeWait.class);
		nodes.put("wait", NodeWait.class);

		nodes.put("stop", NodeEndLevel.class);
		nodes.put("end", NodeEndLevel.class);
		nodes.put("finish", NodeEndLevel.class);

		nodes.put("timer", NodeTimerSetup.class);
		nodes.put("timer_set", NodeTimerSetup.class);

		nodes.put("timer_restart", NodeTimerRestart.class);
		nodes.put("timer_reset", NodeTimerRestart.class);

		nodes.put("timer_remove", NodeTimerRemove.class);
		nodes.put("timer_destroy", NodeTimerRemove.class);

		nodes.put("timer_stop", NodeTimerPause.class);
		nodes.put("timer_pause", NodeTimerPause.class);

		nodes.put("timer_resume", NodeTimerResume.class);
		nodes.put("timer_continue", NodeTimerResume.class);
		nodes.put("timer_start", NodeTimerResume.class);

//		nodes.put("gen_asteroid", NodeSetupAsteroidSpawner.class);
//		nodes.put("gen_asteroids", NodeSetupAsteroidSpawner.class);
//		nodes.put("asteroids", NodeSetupAsteroidSpawner.class);
//		nodes.put("gen_rock", NodeSetupAsteroidSpawner.class);
//		nodes.put("gen_rocks", NodeSetupAsteroidSpawner.class);
//		nodes.put("rocks", NodeSetupAsteroidSpawner.class);
		nodes.put("rockgen", NodeSetupAsteroidSpawner.class);

		nodes.put("scoregen", NodeSetupScoreSpawner.class);
		nodes.put("time_reward", NodeSetupScoreSpawner.class);

//		nodes.put("gen_entity", NodeSetupEntitySpawner.class);
//		nodes.put("gen_entities", NodeSetupEntitySpawner.class);
//		nodes.put("entities", NodeSetupEntitySpawner.class);
//		nodes.put("gen_enemy", NodeSetupEntitySpawner.class);
//		nodes.put("gen_enemies", NodeSetupEntitySpawner.class);
//		nodes.put("gen_ship", NodeSetupEntitySpawner.class);
//		nodes.put("gen_ships", NodeSetupEntitySpawner.class);
//		nodes.put("enemies", NodeSetupEntitySpawner.class);

		nodes.put("enemygen", NodeSetupEntitySpawner.class);
		nodes.put("shipgen", NodeSetupEntitySpawner.class);

		nodes.put("gen_enable", NodeSpawnerEnable.class);
		nodes.put("enable", NodeSpawnerEnable.class);
		nodes.put("gen_disable", NodeSpawnerDisable.class);
		nodes.put("disable", NodeSpawnerDisable.class);


		nodes.put("score", NodeAddScore.class);
		nodes.put("bonus", NodeAddScore.class);
		nodes.put("reward", NodeAddScore.class);
		nodes.put("add_score", NodeAddScore.class);

		nodes.put("repair_ship", NodeRepairShip.class);
		nodes.put("repair", NodeRepairShip.class);
	}

	/**
	 * Load node from XML by tag name
	 * 
	 * @param parent node parent
	 * @param level level controller
	 * @param tag tag
	 * @return entity
	 */
	public static LevelNodeBase loadNode(LevelNodeBase parent, LevelController level, Element tag) {
		try {

			Class<? extends LevelNodeBase> cls = nodes.get(tag.getName().toLowerCase());
			Constructor<? extends LevelNodeBase> constructor = cls.getDeclaredConstructor(LevelNodeBase.class, LevelController.class);
			LevelNodeBase node = constructor.newInstance(parent, level);
			node.loadFromXml(tag);
			return node;

		} catch (Exception e) {
			Log.e("Could not instantiate algorithm node '" + tag.getName() + "' - not found in registry.");
			return null;
		}
	}

}
