package net.sector.threads;


import static net.sector.threads.EThreadStatus.*;
import net.sector.level.SuperContext;
import net.sector.network.communication.LeaderboardClient;
import net.sector.network.communication.ServerError;
import net.sector.network.responses.ObjInfoTable;
import net.sector.util.Log;


/**
 * Thread checking version of the latest release.
 * 
 * @author MightyPork
 */
public class ThreadCheckLatestVersion extends Thread {

	/** Thread status */
	public static EThreadStatus status = UNSTARTED;

	@Override
	public void run() {
		status = WORKING;

		try {
			ObjInfoTable tbl = LeaderboardClient.getInfoTable();

			SuperContext.latestVersionName = tbl.latest_version;
			SuperContext.latestVersionNumber = tbl.latest_version_num;

			Log.f1("THREAD: Downloaded information table.");
			Log.f2(tbl.toString());

			status = SUCCESS;

		} catch (ServerError e) {
			status = FAILURE;
			Log.w("THREAD: Could not get info table: " + e.getMessage());
		}

	}
}
