package net.sector.network.levels;


import java.util.ArrayList;

import net.sector.level.LevelBundle;
import net.sector.network.responses.ObjLevelInfo;
import net.sector.network.responses.ObjLevelList;
import net.sector.util.Log;


/**
 * List of profile entries
 * 
 * @author MightyPork
 */
public class NetLevelList extends ArrayList<NetLevelContainer> {


	@Override
	public String toString() {
		String s = "";
		s += "\n# LEVEL LIST BEGIN #\n";
		for (NetLevelContainer u : this) {
			s += "\n";
			s += u.toString();
			s += "\n";
		}
		s += "# LEVEL LIST END #\n";
		return s;
	}

	/**
	 * Empty constructor for NetLevelList
	 */
	public NetLevelList() {}

	/**
	 * Create level list from downloaded ObjLevelList object
	 * 
	 * @param list level list info
	 */
	public NetLevelList(ObjLevelList list) {
		for (ObjLevelInfo oli : list) {
			add(new NetLevelContainer(oli));
		}
	}

	/**
	 * Unpack levels and convert them to level bundles.
	 * 
	 * @return level bundle list.
	 */
	public ArrayList<LevelBundle> unpackLevels() {
		ArrayList<LevelBundle> list = new ArrayList<LevelBundle>();
		for (NetLevelContainer lc : this) {
			if (lc.isValid) {
				try {
					list.add(new LevelBundle(lc));
				} catch (Exception e) {
					Log.e("Error unpacking a level " + lc.title, e);
				}
			}
		}

		return list;
	}

	/**
	 * Download missing levels, check already existing level files for validity
	 * using checksum.
	 */
	public void downloadMissingLevels() {
		for (NetLevelContainer lc : this) {
			boolean got = false;
			if (lc.isDownloaded()) {
				Log.f2("NET_LEVELS: Level " + lc.title + " found on disk, calculating checksum...");
				if (lc.loadCheckIfOriginal()) {
					got = true;
					Log.f3("NET_LEVELS: Level " + lc.title + " is valid.");
				} else {
					Log.f3("NET_LEVELS: Level " + lc.title + " is corrupted.");
				}
			}

			if (!got) {
				try {
					Log.f2("NET_LEVELS: Downloading level " + lc.title + "...");
					lc.download();
					Log.f2("NET_LEVELS: Level " + lc.title + " downloaded, calculating checksum...");
					if (!lc.loadCheckIfOriginal()) {
						Log.f3("NET_LEVELS: Level " + lc.title + " is corrupted.");
					} else {
						Log.f3("NET_LEVELS: Level " + lc.title + " is valid.");
					}
				} catch (Exception e) {
					Log.e("NET_LEVELS: Level " + lc.title + " could not be downloaded.", e);
				}
			}
		}
	}

}
