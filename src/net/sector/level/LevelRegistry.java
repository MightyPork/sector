package net.sector.level;


import java.util.ArrayList;
import java.util.Collections;

import net.sector.network.levels.NetLevelList;


public class LevelRegistry {

	/** Levels connected to Global Leaderboard IN POTENTIA */
	public static NetLevelList netLevels_inPotentia = new NetLevelList();
	/** Levels from resources, built-in IN POTENTIA */
	public static ArrayList<LevelContainer> internalLevels_inPotentia = new ArrayList<LevelContainer>();
	/** Levels from user's folder IN POTENTIA */
	public static ArrayList<LevelContainer> localLevels_inPotentia = new ArrayList<LevelContainer>();
	/** Levels connected to Global Leaderboard */
	public static ArrayList<LevelBundle> netLevels = new ArrayList<LevelBundle>();
	/** Levels from resources, built-in */
	public static ArrayList<LevelBundle> internalLevels = new ArrayList<LevelBundle>();
	/** Levels from user's folder */
	public static ArrayList<LevelBundle> localLevels = new ArrayList<LevelBundle>();

	/**
	 * Get list with all level types.
	 * 
	 * @return list of level bundles.
	 */
	public static ArrayList<LevelBundle> getAllLevels() {
		ArrayList<LevelBundle> list = new ArrayList<LevelBundle>();
		list.addAll(localLevels);
		list.addAll(internalLevels);
		list.addAll(netLevels);
		Collections.sort(list);
		return list;
	}

}
