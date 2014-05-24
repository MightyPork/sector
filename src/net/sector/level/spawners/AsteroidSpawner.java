package net.sector.level.spawners;


import java.util.Map;

import net.sector.collision.Scene;
import net.sector.entities.Entity;
import net.sector.entities.natural.EntityAsteroid;
import net.sector.level.dataobj.AiObjParser;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.math.Range;


/**
 * Asteroid field generator
 * 
 * @author MightyPork
 */
public class AsteroidSpawner extends SpawnerBase {

	private Range asteroidSize = new Range(2, 8);
	private double speed = 1;
	private Range healthMul = new Range(1);

	private int rarity = 8;
	private int type = -1;

	/**
	 * Asteroid generator
	 * 
	 * @param scene scene
	 */
	public AsteroidSpawner(Scene scene) {
		super(scene);
	}

	/**
	 * Set asteroid speed
	 * 
	 * @param speed speed (default 1)
	 */
	public void setAsteroidSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Set asteroid type
	 * 
	 * @param type type (0-5)
	 */
	public void setAsteroidType(int type) {
		this.type = type;
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
	 * Set asteroid size (relative)
	 * 
	 * @param size (default 2 - 8)
	 */
	public void setAsteroidSize(Range size) {
		asteroidSize = size.copy();
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

			double S = 0.4;
			Vec motion = new Vec(-S * speed + rand.nextDouble() * S * speed * 2, 0, 0 + rand.nextDouble() * S * speed);

			if (motion.size() > 0.4) motion.norm(0.4);

			double size1 = Calc.clampd(asteroidSize.randDouble(), 0.005, 20);
			double size2 = Calc.clampd(asteroidSize.randDouble(), 0.005, 20);
			double scale = 0.05 + ((size1 + size2) / 2) * 0.2;

			Entity rock;
			if (type == -1) {
				rock = new EntityAsteroid(scale, Coord.ZERO, motion);
			} else {
				rock = new EntityAsteroid(scale, Coord.ZERO, motion, type);
			}

			rock.healthMul = healthMul.randDouble();

			int tries = 0;
			Coord pos;
			while (true) {
				pos = new Coord(xCoord.randDouble(), y, zCoord.randDouble());
				if (scene.getEntitiesInRange(pos, rock.getRadius() + 0.3).size() <= 0) break;

				tries++;
				if (tries > 10) return;
			}

			rock.getPos().setTo(pos);
			scene.add(rock);

		}
	}

	@Override
	public void loadFromXmlArgs(Map<String, Object> args) {
		/*
		 * 
		 * optional:
		 * <type num="2" />
		 * <size range="2-8" />
		 * <speed num="1" /> <!-- relative max speed, default 1 -->
		 * <rarity num="8" />
		 * <x range="-30-30" />
		 * <z range="100-120" />
		 * 
		 */

		// default 2:8
		setAsteroidSize(AiObjParser.getRange(args.get("size"), asteroidSize));

		// relative, 1 default
		setAsteroidSpeed(AiObjParser.getDouble(args.get("speed"), speed));

		setAsteroidType(AiObjParser.getInteger(args.get("type"), type));

		setRarity(AiObjParser.getInteger(args.get("rarity"), rarity));
		setHealthMul(AiObjParser.getRange(args.get("health"), healthMul));

		Range x = AiObjParser.getRange(args.get("x"), xCoord);
		Range z = AiObjParser.getRange(args.get("z"), zCoord);
		setZone(x, z);

	}

}
