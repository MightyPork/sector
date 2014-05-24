package net.sector;


import com.porcupine.coord.Coord;


/**
 * Sector constants
 * 
 * @author MightyPork
 */
@SuppressWarnings("javadoc")
public class Constants {

	// STRINGS
	public static final int VERSION_NUMBER = 18;
	public static final String VERSION_NAME = "1.8";
	public static final String TITLEBAR = "SECTOR " + VERSION_NAME + " - Game by MightyPork - www.ondrovo.com/sector";

	// FILES+DIRS
	public static final String APP_DIR = "sector";

	public static final String DIR_LEVELS_SHARED = "levels/shared";
	public static final String DIR_LEVELS_LOCAL = "levels/local";

	public static final String DIR_LASTSHIP_LOCAL = "last_ship/local";
	public static final String DIR_LASTSHIP_SHARED = "last_ship/shared";
	public static final String DIR_LASTSHIP_INTERNAL = "last_ship/internal";

	public static final String DIR_HIGHSCORE_LOCAL = "highscore/local";
	public static final String DIR_HIGHSCORE_SHARED = "highscore/shared";
	public static final String DIR_HIGHSCORE_INTERNAL = "highscore/internal";

	public static final String SUFFIX_SHIP = "ship";
	public static final String DIR_SHIPS = "ships";

	public static final String DIR_SCREENSHOTS = "screenshots";

	public static final String FILE_CONFIG = "users.ion";
	public static final String FILE_LOG = "Sector.log";
	public static final String FILE_LOG_E = "Sector_errors.log";

	// NETWORK
	public static final String WEB_URL = "http://www.ondrovo.com/sector/download";
	public static final String SERVER_URL = "http://www.ondrovo.com/sector/api/server.php";

	// LIGHT
	public static final float LIGHT_AMBIENT = 0.3F;
	public static final float LIGHT_SPECULAR = 0.3F;
	public static final float LIGHT_DIFFUSE = 0.3F;
	public static final Coord LIGHT_POS = new Coord(-3, 5, 5);
	public static final float LIGHT_ATTR = 1;

	public static final float SCENE_MAT_AMBIENT = 0.15F;
	public static final float SCENE_MAT_SPECULAR = 0.4F;
	public static final float SCENE_MAT_DIFFUSE = 0.3F;

	// CAMERA & SCENE
	public static final Coord CAM_POS = new Coord(0, 3.3, 5);
	public static final Coord CAM_LOOKAT = new Coord(0, 2.3, 0);
	public static final float CAM_ANGLE = 45;
	public static final double CAM_NEAR = 0.1;
	public static final double FOG_START = 80;
	public static final double CAM_FAR = 100;

	// AUDIO
	public static final Coord LISTENER_POS = new Coord(0, 5, 0);

	// TIMING
	public static final int FPS_UPDATE = 55;
	public static final double SPEED_MUL = 100D / FPS_UPDATE;

	public static final int FPS_RENDER = 200; // max

	// LOGGING GROUPS
	public static final boolean LOG_DRIVERS = false;
	public static final boolean LOG_FONTS = false;
	public static final boolean LOG_MODELS = false;
	public static final boolean LOG_TEXTURES = false;
	public static final boolean LOG_SOUNDS = false;
	public static final boolean LOG_XML_LOADING = false;
	public static final boolean LOG_COUNTRIES = false;
	public static final boolean LOG_ZONES = false;

	// INITIAL WINDOW SIZE (later loaded from config file)
	public static final int WINDOW_SIZE_X = 800;
	public static final int WINDOW_SIZE_Y = 600;

	public static final int PARTICLE_COUNT_LIMIT = 3000;


}
