package net.sector;


import javax.naming.TimeLimitExceededException;

import net.sector.gui.widgets.ColorScheme;
import net.sector.level.drivers.FunctorRegistry;
import net.sector.level.sequence.LevelNodeRegistry;
import net.sector.level.ship.DiscoveryRegistry;
import net.sector.level.ship.PieceRegistry;
import net.sector.level.spawners.EntityRegistry;
import net.sector.network.CountryList;
import net.sector.threads.*;
import net.sector.util.Log;


/**
 * Initialization utility, initializing all the static stuff that is needed
 * before starting main loop.
 * 
 * @author MightyPork
 */
public class StaticInitializer {

	/**
	 * Init static things and start threads.<br>
	 * This is called on startup, even before the splash screen.
	 */
	public static void initOnStartup() {

		CustomIonMarks.init();

		DiscoveryRegistry.init();
		FunctorRegistry.init();
		LevelNodeRegistry.init();

		CountryList.init();
		ColorScheme.init();
		EntityRegistry.init();

		// load user profiles
		new ThreadLoadAndActivateProfiles().start();

		// check latest version.		
		new ThreadCheckLatestVersion().start();

		// download new levels.
		new ThreadDownloadNewLevels().start();

		// load local and internal levels
		new ThreadLoadOfflineLevels().start();

		// load drivers.
		new ThreadLoadBasicDrivers().start();

	}

	private static void logThreadStatus() {
		Log.f2("\nLOADING THREADS:");
		Log.f2("\tThreadLoadBasicDrivers: " + ThreadLoadBasicDrivers.status);
		Log.f2("\tThreadLoadOfflineLevels: " + ThreadLoadOfflineLevels.status);
		Log.f2("\tThreadDownloadNewLevels: " + ThreadDownloadNewLevels.status);
		Log.f2("\tThreadCheckLatestVersion: " + ThreadCheckLatestVersion.status);
		Log.f2("\tThreadLoadAndActivateProfiles: " + ThreadLoadAndActivateProfiles.status);
		Log.f2("\n\n");

	}

	/**
	 * Initialize all.
	 */
	public static void initPostLoad() {

		// initialize piece and discovery registry
		// put here, so that ThreadUnpackLevels can build ship bundles.
		PieceRegistry.init();

		Log.f1("Waiting for loading threads to finish...");

		logThreadStatus();

		long beginTime = System.currentTimeMillis();

		// wait for threads.
		while (true) {
			if (System.currentTimeMillis() - beginTime > 8000) {
				Log.w("Loading time limit exceeded.");
				logThreadStatus();

				if (ThreadLoadBasicDrivers.status == EThreadStatus.WORKING || ThreadLoadOfflineLevels.status == EThreadStatus.WORKING) {
					Log.w("Cannot continue, aborting startup.");
					App.showCrashReport(new TimeLimitExceededException("Resource loading thread(s) timed out."));
				} else {
					// network problem..	
					Log.w("Could not connect to server, entering offline mode.");
					App.offlineMode = true;
					break;
				}
			}

			if (ThreadLoadBasicDrivers.status == EThreadStatus.WORKING) continue;
			if (ThreadLoadOfflineLevels.status == EThreadStatus.WORKING) continue;
			if (ThreadDownloadNewLevels.status == EThreadStatus.WORKING) continue;
			if (ThreadCheckLatestVersion.status == EThreadStatus.WORKING) continue;
			if (ThreadLoadAndActivateProfiles.status == EThreadStatus.WORKING) continue;
			break;

		}

		if (ThreadDownloadNewLevels.status == EThreadStatus.FAILURE
			|| ThreadCheckLatestVersion.status == EThreadStatus.FAILURE
			|| ThreadLoadAndActivateProfiles.status == EThreadStatus.FAILURE) {
			
			Log.w("Could not connect to server, entering offline mode.");
			App.offlineMode = true;

		}

		Log.f1("Unpacking level containers...");
		new ThreadUnpackLevels().start();
	}

}
