package net.sector.level.ship;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sector.util.Log;

import com.porcupine.math.Calc;
import com.porcupine.math.Range;
import com.porcupine.util.VarargsParser;


/**
 * Discovery registry.
 * 
 * @author MightyPork
 */
public class DiscoveryRegistry {
	private static Map<String, DiscoveryEntry> discoveries = new HashMap<String, DiscoveryEntry>();

	/**
	 * initialize static fields
	 */
	public static void init() {
		Log.f1("Initializing discovery registry...");

		//@formatter:off
		
		// ship systems
		registerDiscovery("shield", 		"Force Shield", 	0, 9);
		registerDiscovery("energy", 		"Energy System", 	1, 9);
		
		// piece discoveries
		registerDiscovery("body",			"Body", 			1, 3);
				
		registerDiscovery("engineRocket",	"Rocket Engine", 	0, 6);	
		registerDiscovery("engineIon",		"Ion Engine", 		0, 6);			
		
		registerDiscovery("cannon",			"Cannon", 			0, 6);
		registerDiscovery("laser", 			"Laser Gun", 		0, 6);
		registerDiscovery("plasma",			"Plasma Gun", 		0, 6);
		registerDiscovery("rocket", 		"Direct Rocket", 	0, 6);
		registerDiscovery("rocket_guided", 	"Guided Rocket", 	0, 6, "rocket", 1);
		registerDiscovery("emp", 			"EMP Weapon", 		0, 6);
		registerDiscovery("fireball", 		"Flamethrower", 	0, 6);

		//@formatter:on
	}

	/**
	 * Clamp level of discovery to legal range
	 * 
	 * @param discovery discovery name
	 * @param level level to clamp
	 * @return clamped level
	 */
	public static int clampLevel(String discovery, int level) {
		return Calc.clampi(level, getDiscoveryLevelRange(discovery));
	}

	/**
	 * Build discovery table with max levels and entries for all discoveries
	 * registered.
	 * 
	 * @return the discovery table
	 */
	public static DiscoveryTable getDiscoveryTableMaximal() {
		DiscoveryTable dt = new DiscoveryTable();
		for (Entry<String, DiscoveryEntry> e : discoveries.entrySet()) {
			dt.setDiscoveryLevel(e.getKey(), e.getValue().getLevelRange().getMaxI());
		}
		return dt;
	}

	/**
	 * Build discovery table with min levels and entries for all discoveries
	 * registered. Energy level is forced to 1.
	 * 
	 * @return the discovery table
	 */
	public static DiscoveryTable getDiscoveryTableMinimal() {
		DiscoveryTable dt = new DiscoveryTable();
		for (Entry<String, DiscoveryEntry> e : discoveries.entrySet()) {
			dt.setDiscoveryLevel(e.getKey(), e.getValue().getLevelRange().getMinI());
		}
		dt.setDiscoveryLevel("energy", 1);
		return dt;
	}

	/**
	 * @param id discovery id
	 * @param entry discovery entry
	 */
	public static void registerDiscovery(String id, DiscoveryEntry entry) {
		discoveries.put(id, entry);
	}

	/**
	 * Register a discovery
	 * 
	 * @param id discovery id
	 * @param label human readable label
	 * @param level range (min - max) level
	 * @param requirements varargs of requirements, eg. "DiscA", 1, "DiscB",
	 *            3...
	 */
	public static void registerDiscovery(String id, String label, Range level, Object... requirements) {
		registerDiscovery(id, new DiscoveryEntry(label, level, requirements));
	}

	/**
	 * Register a discovery
	 * 
	 * @param id discovery id
	 * @param label human readable label
	 * @param minLevel minimal level
	 * @param maxLevel maximal level
	 * @param requirements varargs of requirements, eg. "DiscA", 1, "DiscB",
	 *            3...
	 */
	public static void registerDiscovery(String id, String label, int minLevel, int maxLevel, Object... requirements) {
		registerDiscovery(id, new DiscoveryEntry(label, minLevel, maxLevel, requirements));
	}


	/**
	 * Get discovery level range
	 * 
	 * @param discovery discovery id
	 * @return range (initial - max)
	 */
	public static Range getDiscoveryLevelRange(String discovery) {
		return getDiscovery(discovery).getLevelRange();
	}


	/**
	 * Get min discovery level
	 * 
	 * @param discovery discovery id
	 * @return min level
	 */
	public static int getDiscoveryLevelMin(String discovery) {
		return getDiscoveryLevelRange(discovery).getMinI();
	}


	/**
	 * Get max discovery level
	 * 
	 * @param discovery discovery id
	 * @return max level
	 */
	public static int getDiscoveryLevelMax(String discovery) {
		return getDiscoveryLevelRange(discovery).getMaxI();
	}


	/**
	 * Get if a discovery exists
	 * 
	 * @param discovery discovery id
	 * @return exists
	 */
	public static boolean discoveryExists(String discovery) {
		return discoveries.containsKey(discovery);
	}


	/**
	 * Get discovery entry
	 * 
	 * @param id discovery id
	 * @return discovery entry
	 */
	private static DiscoveryEntry getDiscovery(String id) {
		DiscoveryEntry de = discoveries.get(id);
		if (de == null) Log.w("No such discovery: " + id);

		return de;
	}


	/**
	 * Get if discovery is available for discovering (dependencies met)
	 * 
	 * @param discovery discovery id
	 * @param table discovery table to look in
	 * @return is available
	 */
	public static boolean isDiscoveryAvailable(String discovery, DiscoveryTable table) {
		return getDiscovery(discovery).isAvailable(table);
	}



	/**
	 * Discovery entry for Discovery Registry
	 * 
	 * @author MightyPork
	 */
	public static class DiscoveryEntry {
		private String label;
		private HashMap<String, Integer> dependencies = new HashMap<String, Integer>();
		private Range level = new Range(0, 1);

		/**
		 * Create a new discovery descriptor
		 * 
		 * @param label Human readable labe
		 * @param minLevel minimal (initial) level
		 * @param maxLevel maximal level
		 * @param requirements varargs of requirements, eg. "DiscA", 1, "DiscB",
		 *            3, "DiscX", 6
		 */
		public DiscoveryEntry(String label, int minLevel, int maxLevel, Object... requirements) {
			this(label, new Range(minLevel, maxLevel), requirements);
		}

		/**
		 * Create a new discovery descriptor
		 * 
		 * @param label Human readable labe
		 * @param level level range (initial - max)
		 * @param requirements varargs of requirements, eg. "DiscA", 1, "DiscB",
		 *            3, "DiscX", 6
		 */
		public DiscoveryEntry(String label, Range level, Object... requirements) {
			this.label = label;
			this.level.setTo(level);

			dependencies = new VarargsParser<String, Integer>().parse(requirements);

//			System.out.println();
//			System.out.println("Discovery "+label+", range "+level);
//			System.out.println("Dep = "+dependencies);
//			System.out.println();
		}

		/**
		 * Get if this discovery is available for a discovery table
		 * 
		 * @param table the discovery table to check
		 * @return is available for discovering
		 */
		public boolean isAvailable(DiscoveryTable table) {

			for (Entry<String, Integer> e : dependencies.entrySet()) {
				if (table.get(e.getKey()) < e.getValue()) return false;
			}

			return true;
		}

		/**
		 * Get level range
		 * 
		 * @return level range (initial - max)
		 */
		public Range getLevelRange() {
			return level;
		}

		/**
		 * Get human readable label
		 * 
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
	}

}
