package net.sector.level.ship.modules;


import net.sector.Constants;
import net.sector.level.ship.DiscoveryRegistry;

import com.porcupine.math.Calc;


/**
 * Ship energy system
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class EnergySystem {

	/**
	 * Get base energy system cost
	 * 
	 * @return base cost (level 1)
	 */
	public static int getBaseCost() {
		return 400;
	}

	/**
	 * Get max available level
	 * 
	 * @return max level
	 */
	public static int getLevelMax() {
		return DiscoveryRegistry.getDiscoveryLevelMax("energy");
	}

	private double storage = 100;
	private double storageMax = 1000;
	/** Energy system level */
	public int level = 1;

	/**
	 * Energy system
	 * 
	 * @param level level
	 */
	public EnergySystem(int level) {
		setLevel(level);
	}

	/**
	 * Set level
	 * 
	 * @param level level to set
	 */
	public void setLevel(int level) {
		this.level = Calc.clampi(level, 1, getLevelMax());
		storageMax = 1500 * Math.pow(level, 2.2);
		storage = storageMax;
	}

	public double getAddedPerUpdate() {
		return Math.pow(level, 1.8) * 17 * Constants.SPEED_MUL;
	}

	/**
	 * Update tick
	 */
	public void update() {
		storage = Calc.clampd(storage + getAddedPerUpdate(), 0, storageMax);
	}

	/**
	 * Try to consume energy points
	 * 
	 * @param needed points to consume
	 * @return all consumed (false = none consumed)
	 */
	public boolean tryToConsume(double needed) {
		if (storage > needed) {
			storage -= needed;
			return true;
		}
		return false;
	}

	/**
	 * Get relative storage contents
	 * 
	 * @return relative storage contents
	 */
	public double getStorageRatio() {
		return storage / storageMax;
	}

	/**
	 * Get stored energy points
	 * 
	 * @return energy points
	 */
	public double getStored() {
		return storage;
	}

	/**
	 * Get max storage size
	 * 
	 * @return limit
	 */
	public double getLimit() {
		return storageMax;
	}

	/**
	 * Consume given amount of points, or less if not enough
	 * 
	 * @param consumed points to consume
	 * @return consumed all
	 */
	public boolean consume(double consumed) {
		storage -= consumed;
		if (storage < 0) {
			storage = 0;
			return false;
		}
		return true;
	}

	public void fill() {
		storage = storageMax;
	}
}
