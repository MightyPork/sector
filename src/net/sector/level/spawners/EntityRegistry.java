package net.sector.level.spawners;


import java.util.HashMap;

import net.sector.entities.EntityNavigable;
import net.sector.entities.enemies.*;
import net.sector.level.drivers.TaskList;
import net.sector.util.Log;

import com.porcupine.coord.Coord;


/**
 * Entity registry for easier spawning
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EntityRegistry {

	private static HashMap<String, Class<? extends EntityNavigable>> entities = new HashMap<String, Class<? extends EntityNavigable>>();

	/**
	 * Initialize
	 */
	public static void init() {
		Log.f1("Initializing entity registry...");
		entities.put("mine", EntityMine.class);
		entities.put("bird", EntityShipBird.class);
		entities.put("burger", EntityShipBurger.class);
		entities.put("burger_king", EntityShipBurgerKing.class);
		entities.put("falcon", EntityShipFalcon.class);
		entities.put("fighter", EntityShipFighter.class);
		entities.put("shark", EntityShipShark.class);
		entities.put("snake", EntityShipCube.class);
	}

	/**
	 * Get entity instance
	 * 
	 * @param type entity type
	 * @param pos position
	 * @return entity
	 */
	public static EntityNavigable buildEntity(String type, Coord pos) {
		try {
			return entities.get(type).getDeclaredConstructor(Coord.class).newInstance(pos);
		} catch (Exception e) {
			Log.e("Could not instantiate entity '" + type + "' with Coord argument.");
			return null;
		}
	}

	/**
	 * Get entity instance
	 * 
	 * @param type entity type
	 * @param scale relative scale
	 * @param pos position
	 * @return entity
	 */
	public static EntityNavigable buildEntity(String type, double scale, Coord pos) {
		EntityNavigable e = buildEntity(type, pos);
		if (e == null) return null;
		e.adjustForScale(scale);
		return e;
	}

	/**
	 * Get entity instance
	 * 
	 * @param type entity type
	 * @param pos position
	 * @param driver driver (task list)
	 * @return entity
	 */
	public static EntityNavigable buildEntity(String type, Coord pos, TaskList driver) {
		EntityNavigable e = buildEntity(type, pos);
		if (e == null) return null;
		e.setDriver(driver);
		return e;
	}

	/**
	 * Get entity instance
	 * 
	 * @param type entity type
	 * @param scale relative scale
	 * @param pos position
	 * @param driver driver (task list)
	 * @return entity
	 */
	public static EntityNavigable buildEntity(String type, double scale, Coord pos, TaskList driver) {
		EntityNavigable e = buildEntity(type, pos);
		if (e == null) return null;
		e.adjustForScale(scale);
		e.setDriver(driver);
		return e;
	}

}
