package net.sector;


import net.sector.util.Utils;

import org.lwjgl.input.Keyboard;

import com.porcupine.util.PropertyManager;


/**
 * Configuration
 * 
 * @author MightyPork
 */
@SuppressWarnings("javadoc")
public class GameConfig {

	public static final String pk_win_resize = "window.resizable";
	public static final String pk_splash = "window.splash";
	public static final String pk_win_fs = "window.start.fullscreen";

	public static final String pk_antialiasing = "graphics.antialiasing";
	public static final String pk_vsync = "graphics.vsync";

	public static final String pk_in_mouse = "input.mouse.sensitivity";

	public static final String pk_sound_volume = "audio.volume.sound";
	public static final String pk_music_volume = "audio.volume.music";

	public static final String pk_log = "log.enable";
	public static final String pk_log_stdout = "log.toStdOut";


	public static final String pk_key_shield = "key.shield";

	public static final String pk_update_notifications = "feature.update_alerts";



	public static boolean enableUpdateAlerts;
	public static boolean startInFullscreen;
	public static boolean enableVsync;
	public static boolean enableSplash;
	public static boolean enableResize;
	public static int mouseSensitivity;
	public static int audioVolumeSound;
	public static int audioVolumeMusic;
	public static int antialiasing;
	public static boolean logEnabled;
	public static boolean logStdOut;

	public static int keyShield = Keyboard.KEY_LCONTROL;

	public static boolean colliderWireframe = false;
	private static PropertyManager p;

	/**
	 * Init property manager and load from file.
	 */
	public static void initLoad() {
		p = new PropertyManager(Utils.getGameFolder() + "/config.ini", "Main SECTOR's configuration file.");

		p.cfgSeparateSections(true);
		p.cfgNewlineBeforeComments(false);

		p.putBoolean(pk_win_fs, false, "Start in fullscreen.");
		p.putBoolean(pk_update_notifications, true, "Show update notifications.");
		p.putInteger(pk_antialiasing, 4, "Antialiasing 0, 2, 4, 8; Depends of graphic card.");
		p.putBoolean(pk_splash, true, "Enable splash animation");
		p.putBoolean(pk_vsync, true, "Enable vsync");
		p.putBoolean(pk_win_resize, true, "Make window resizable");

		p.putInteger(pk_in_mouse, 1000, "Mouse sensitivity for ship movement. 1000 is the default sensitivity.");

		p.putInteger(pk_sound_volume, 100, "Sound volume, 0-100.");
		p.putInteger(pk_music_volume, 100, "Music volume, 0-100.");

		p.putBoolean(pk_log, true, "Enable logging.");
		p.putBoolean(pk_log_stdout, false, "Print log messages also to stdout.");

		p.putKey(pk_key_shield, Keyboard.KEY_LCONTROL);

		saveLoad();
		useLoaded();
	}

	/**
	 * Set property value (call saveLoad and useLoaded afterwards)
	 * 
	 * @param key key to set
	 * @param newValue new value assigned
	 */
	public static void setNewProp(String key, Object newValue) {
		p.setValue(key, newValue);
	}

	/**
	 * Save changes and load from file.
	 */
	public static void saveLoad() {
		p.apply();
	}

	/**
	 * Store loaded config to fields
	 */
	public static void useLoaded() {
		startInFullscreen = p.getBoolean(pk_win_fs);
		enableSplash = p.getBoolean(pk_splash);
		enableVsync = p.getBoolean(pk_vsync);
		enableResize = p.getBoolean(pk_win_resize);

		mouseSensitivity = p.getInteger(pk_in_mouse);

		antialiasing = p.getInteger(pk_antialiasing);
		audioVolumeSound = p.getInteger(pk_sound_volume);
		audioVolumeMusic = p.getInteger(pk_music_volume);

		logEnabled = p.getBoolean(pk_log);
		logStdOut = p.getBoolean(pk_log_stdout);

		keyShield = p.getInt(pk_key_shield);

		enableUpdateAlerts = p.getBoolean(pk_update_notifications);
	}
}
