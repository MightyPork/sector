package net.sector.level;


import java.io.File;

import net.sector.Constants;
import net.sector.level.drivers.DriverStore;
import net.sector.network.ProfileList;
import net.sector.network.UserProfile;
import net.sector.util.Log;
import net.sector.util.Utils;

import com.porcupine.ion.Ion;


/**
 * Super context (global)
 * 
 * @author MightyPork
 */
public class SuperContext {

	// INFO ABOUT LATEST VERSION

	/** Flag that update alert was already shown */
	public static boolean updateAlertShown = false;

	/** Version name of the latest release */
	public static String latestVersionName = Constants.VERSION_NAME;

	/** Number of the latest release */
	public static int latestVersionNumber = Constants.VERSION_NUMBER;



	// USER PROFILE LIST

	/** User profiles, loaded from file on startup. */
	public static ProfileList userProfiles = new ProfileList();

	/** Active user profile - null = anonymous/guest mode */
	public static UserProfile selectedUser = null;

	/** Currently active game */
	private static GameContext game = null;

	/** Default drivers */
	public static DriverStore basicDrivers = new DriverStore();



	private static File getUserListFile() {
		return Utils.getGameSubfolder(Constants.FILE_CONFIG);
	}


	/**
	 * Load user list from a file.
	 */
	public static void loadUserList() {
		File file = getUserListFile();

		boolean loaded = false;
		if (file.exists()) {
			try {
				userProfiles = (ProfileList) Ion.fromFile(file);
				loaded = true;
			} catch (Exception e) {
				Log.e("Error loading user profiles from " + file, e);
			}
		}

		if (!loaded) {
			userProfiles = new ProfileList();
			selectedUser = null; // enter guest mode
		}
	}


	/**
	 * Save user list to a file.
	 */
	public static void saveUserList() {
		File file = getUserListFile();

		try {
			Ion.toFile(file, userProfiles);
		} catch (Exception e) {
			Log.e("Error saving user profiles to " + file, e);
		}

	}

	/**
	 * Get game context
	 * 
	 * @return game context
	 */
	public static GameContext getGameContext() {
		if (game == null) {
			Log.e("at getGameContext(): No game context exists yet!");
		}
		return game;
	}


	public static void startGame(LevelBundle level) {
		game = level.toGameContext();
	}
}
