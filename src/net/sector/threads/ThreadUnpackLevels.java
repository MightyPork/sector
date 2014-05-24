package net.sector.threads;


import static net.sector.threads.EThreadStatus.*;
import net.sector.App;
import net.sector.level.LevelContainer;
import net.sector.level.LevelRegistry;
import net.sector.util.Log;


/**
 * Thread checking version of the latest release.
 * 
 * @author MightyPork
 */
public class ThreadUnpackLevels extends Thread {
	public static ThreadUnpackLevels instance = null;

	/** Thread status */
	public static EThreadStatus status = UNSTARTED;

	@Override
	public void run() {
		instance = this;
		status = WORKING;
		try {

			if (!App.offlineMode) LevelRegistry.netLevels = LevelRegistry.netLevels_inPotentia.unpackLevels();

			for (LevelContainer lc : LevelRegistry.internalLevels_inPotentia) {
				try {
					LevelRegistry.internalLevels.add(lc.toBundle());
				} catch (Exception e) {
					Log.w("THREAD: Error unpacking internal level: " + e.getMessage());
				}
			}

			for (LevelContainer lc : LevelRegistry.localLevels_inPotentia) {
				try {
					LevelRegistry.localLevels.add(lc.toBundle());
				} catch (Exception e) {
					Log.w("THREAD: Error unpacking local level: " + e.getMessage());
				}
			}
			status = SUCCESS;
		} catch (Exception e) {
			status = FAILURE;
			Log.e("THREAD: Could not unpack levels.", e);
		}

	}
}
