package net.sector.threads;


import static net.sector.threads.EThreadStatus.*;
import net.sector.level.LevelRegistry;
import net.sector.network.communication.LeaderboardClient;
import net.sector.network.levels.NetLevelList;
import net.sector.network.responses.ObjLevelList;
import net.sector.util.Log;


/**
 * Thread checking version of the latest release.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ThreadDownloadNewLevels extends Thread {

	/** Thread status */
	public static EThreadStatus status = UNSTARTED;

	@Override
	public void run() {
		status = WORKING;

		try {
			ObjLevelList levels = LeaderboardClient.getLevelList();
			Log.f1("THREAD: Downloaded list of new levels.");

			NetLevelList nlc = new NetLevelList(levels);

			Log.f1("THREAD: Retrieving missing levels and getting updates.");
			nlc.downloadMissingLevels();

			LevelRegistry.netLevels_inPotentia = nlc;

			status = SUCCESS;

		} catch (Exception e) {
			status = FAILURE;
			Log.w("THREAD: Could not get list of levels: " + e.getMessage());
		}

	}
}
