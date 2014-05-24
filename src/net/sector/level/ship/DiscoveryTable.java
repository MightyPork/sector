package net.sector.level.ship;


import java.util.HashMap;
import java.util.Map;

import net.sector.util.Log;


/**
 * Table of discoveries
 * 
 * @author MightyPork
 */
public class DiscoveryTable extends HashMap<String, Integer> {

	/**
	 * Table of discoveries - empty
	 */
	public DiscoveryTable() {}


	/**
	 * Table of discoveries - as copy of other map
	 * 
	 * @param copied copied map of discovery levels
	 */
	public DiscoveryTable(Map<String, Integer> copied) {
		super(copied);
	}

	/**
	 * Check if piece is discovered
	 * 
	 * @param pieceName piece name
	 * @return is discovered
	 */
	public boolean isPieceDiscovered(String pieceName) {
		return getDiscoveryLevelForPiece(pieceName) > 0;
	}

	/**
	 * Check if piece is discovered
	 * 
	 * @param discovery discovery
	 * @return is discovered
	 */
	public boolean isDiscovered(String discovery) {
		return getDiscoveryLevel(discovery) > 0;
	}

	/**
	 * Get discovery level for piece name
	 * 
	 * @param pieceName piece name
	 * @return max level discovered
	 */
	public int getDiscoveryLevelForPiece(String pieceName) {
		String discovery = PieceRegistry.getPieceDiscoveryKey(pieceName);
		return getDiscoveryLevel(discovery);
	}

	/**
	 * Get discovered level for discovery key
	 * 
	 * @param discovery discovery key
	 * @return level
	 */
	public int getDiscoveryLevel(String discovery) {
		if (!containsKey(discovery)) {
			Log.w("DiscoveryTable: requested discovery level of unregistered discovery " + discovery);
			return 0;
		}
		return get(discovery);
	}

	/**
	 * Set discovery level (when building table)
	 * 
	 * @param discovery discovery key
	 * @param levelToSet levels discovered total to set
	 */
	public void setDiscoveryLevel(String discovery, int levelToSet) {
		int level = DiscoveryRegistry.clampLevel(discovery, levelToSet);
		put(discovery, level);
	}

	/**
	 * Add discovery levels (when artifact is collected)
	 * 
	 * @param discovery discovery key
	 * @param levelsToAdd new levels discovered
	 */
	public void addDiscoveryLevel(String discovery, int levelsToAdd) {
		int level = getDiscoveryLevel(discovery);
		setDiscoveryLevel(discovery, level + levelsToAdd);
	}

	/**
	 * Get copy
	 * 
	 * @return copy
	 */
	public DiscoveryTable copy() {
		return new DiscoveryTable(this);
	}

	/**
	 * Get max level a discovery can reach (total limit)
	 * 
	 * @param discovery discovery id
	 * @return the limit
	 */
	public static int getDiscoveryLevelMax(String discovery) {
		return DiscoveryRegistry.getDiscoveryLevelRange(discovery).getMaxI();
	}

	/**
	 * Get if discovery is available for discovering (if dependencies are met)
	 * 
	 * @param discovery discovery id
	 * @return is available
	 */
	public boolean isDiscoveryAvailable(String discovery) {
		return DiscoveryRegistry.isDiscoveryAvailable(discovery, this);
	}

	/**
	 * Get if can discover given discovery (if it makes sense to spend artifact
	 * on this)
	 * 
	 * @param discovery discovery id
	 * @return can be discovered
	 */
	public boolean canDiscover(String discovery) {
		if (!isDiscoveryAvailable(discovery)) return false;
		return getDiscoveryLevel(discovery) < getDiscoveryLevelMax(discovery);
	}


}
