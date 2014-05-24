package net.sector.level.spawners;


import java.util.Map;

import net.sector.collision.Scene;
import net.sector.entities.EntityNavigable;
import net.sector.level.SuperContext;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.DriverStore;

import com.porcupine.coord.Coord;
import com.porcupine.math.Range;


/**
 * Asteroid field generator
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EnemySpawner extends SpawnerBase {

	private Range entitySize = new Range(1);
	private Range speedMul = null;
	private Range healthMul = new Range(1);
	private String driver = null;
	private String wave = null;
	private DriverStore drivers = SuperContext.basicDrivers;

	private int rarity = 100;

	private String entityName = "";


	/**
	 * Asteroid generator
	 * 
	 * @param scene scene
	 */
	public EnemySpawner(Scene scene) {
		super(scene);
	}

	/**
	 * Set driver store (basic store is default)
	 * 
	 * @param store driver store
	 */
	public void setDriverStore(DriverStore store) {
		drivers = store;
	}

	/**
	 * Set spawned entity
	 * 
	 * @param entityType entity name
	 */
	public void setEntity(String entityType) {
		entityName = entityType;
	}

	/**
	 * Set spawned entity driver
	 * 
	 * @param driver entity driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * Set spawned entity wave
	 * 
	 * @param wave entity driver
	 */
	public void setWave(String wave) {
		this.wave = wave;
	}

	/**
	 * Set entity size (relative)
	 * 
	 * @param size (default 1)
	 */
	public void setEntitySize(Range size) {
		entitySize = size.copy();
	}

	/**
	 * Set entity speed
	 * 
	 * @param speed (1 = default)
	 */
	public void setEntitySpeed(Range speed) {
		speedMul = speed;
	}

	/**
	 * Set entity health multiplier
	 * 
	 * @param health health multiplier (1 = default)
	 */
	public void setHealthMul(Range health) {
		healthMul = health;
	}

	/**
	 * Set spawn rarity (0 = super common (bad), 100 = every 100 ticks)
	 * 
	 * @param rate (default 100)
	 */
	public void setRarity(int rate) {
		this.rarity = rate;
	}

	/**
	 * Spawn asteroids if needed.
	 */
	@Override
	public void onUpdate() {
		if (rand.nextInt(rarity + 1) == 0) {
			double y = 0;

			double scale = entitySize.randDouble();

			EntityNavigable ent = EntityRegistry.buildEntity(entityName, Coord.ZERO);
			ent.adjustForScale(scale);
			if (speedMul != null) ent.setStableSpeedMultiplier(speedMul.randDouble());

			int tries = 0;
			Coord pos;
			while (true) {
				pos = new Coord(xCoord.randDouble(), y, zCoord.randDouble());
				if (scene.getEntitiesInRange(pos, ent.getRadius() + 0.3).size() <= 0) break;

				tries++;
				if (tries > 15) return;
			}

			ent.getPos().setTo(pos);
			if (driver != null) ent.setDriver(drivers.getDriver(driver));

			if (wave != null) getLevel().addToWave(wave, ent);

			scene.add(ent);
		}
	}

	@Override
	public void loadFromXmlArgs(Map<String, Object> args) {
		/*
		 * 
		 * <entity str="burger" />
		 * 
		 * optional:
		 * <driver str="burger_zone_mad" />
		 * <size range="1-2" />
		 * <rarity num="100" />
		 * <x range="-30-30" />
		 * <z range="100-120" />
		 * 
		 */

		setEntity(AiObjParser.getString(args.get("entity"), entityName));
		setDriver(AiObjParser.getString(args.get("driver"), driver));
		setWave(AiObjParser.getString(args.get("wave"), wave));
		setEntitySize(AiObjParser.getRange(args.get("size"), entitySize));

		setHealthMul(AiObjParser.getRange(args.get("health"), healthMul));
		setEntitySpeed(AiObjParser.getRange(args.get("speed"), speedMul));

		setRarity(AiObjParser.getInteger(args.get("rarity"), rarity));

		Range x = AiObjParser.getRange(args.get("x"), xCoord);
		Range z = AiObjParser.getRange(args.get("z"), zCoord);
		setZone(x, z);
	}
}
