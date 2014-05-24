package net.sector;


import net.sector.level.highscore.HighscoreEntry;
import net.sector.level.highscore.HighscoreTable;
import net.sector.network.ProfileList;
import net.sector.network.UserProfile;

import com.porcupine.ion.Ion;


/**
 * Class adding ION marks for custom ionizable objects
 * 
 * @author MightyPork
 */
@SuppressWarnings("javadoc")
public class CustomIonMarks {

	// ION
	public static final byte HIGHSCORE_TABLE = 20;
	public static final byte HIGHSCORE_ENTRY = 21;
	public static final byte USER_PROFILE_LIST = 22;
	public static final byte USER_PROFILE = 23;

//	public static final byte LEVEL_LIST = 24;
//	public static final byte LEVEL_CONTAINER = 25;

	/**
	 * Register ion marks
	 */
	public static void init() {
		Ion.registerIonizable(HIGHSCORE_ENTRY, HighscoreEntry.class);
		Ion.registerIonizable(HIGHSCORE_TABLE, HighscoreTable.class);
		Ion.registerIonizable(USER_PROFILE_LIST, ProfileList.class);
		Ion.registerIonizable(USER_PROFILE, UserProfile.class);
//		Ion.registerIonizable(LEVEL_LIST, NetLevelList.class);
//		Ion.registerIonizable(LEVEL_CONTAINER, NetLevelContainer.class);
	}

}
