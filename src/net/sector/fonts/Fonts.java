package net.sector.fonts;


import static net.sector.fonts.FontManager.Style.*;
import net.sector.fonts.FontManager.Glyphs;
import net.sector.util.Log;

import com.porcupine.coord.Coord;


/**
 * Global font preloader
 * 
 * @author Rapus
 */
public class Fonts {

	private static void registerFileNames() {
		// simple
		FontManager.registerFile("res/fonts/simple/normal.ttf", "simple", NORMAL);
		FontManager.registerFile("res/fonts/simple/wide.ttf", "simple", WIDE);
		FontManager.registerFile("res/fonts/simple/narrow.ttf", "simple", NARROW);
		FontManager.registerFile("res/fonts/simple/outline.ttf", "simple", OUTLINE);

		/// lcd_a / prop
		FontManager.registerFile("res/fonts/lcd_a/prop/normal.ttf", "lcd1", NORMAL);
		FontManager.registerFile("res/fonts/lcd_a/prop/bold.ttf", "lcd1", BOLD);
		FontManager.registerFile("res/fonts/lcd_a/prop/heavy.ttf", "lcd1", HEAVY);
		FontManager.registerFile("res/fonts/lcd_a/prop/light.ttf", "lcd1", LIGHT);

		// lcd_a-dense / mono
		FontManager.registerFile("res/fonts/lcd_a/mono/normal.ttf", "lcd1_mono", NORMAL);
		FontManager.registerFile("res/fonts/lcd_a/mono/bold.ttf", "lcd1_mono", BOLD);
		FontManager.registerFile("res/fonts/lcd_a/mono/heavy.ttf", "lcd1_mono", HEAVY);
		FontManager.registerFile("res/fonts/lcd_a/mono/light.ttf", "lcd1_mono", LIGHT);

		// lcd_b
		FontManager.registerFile("res/fonts/lcd_b/normal.ttf", "lcd2", NORMAL);
		FontManager.registerFile("res/fonts/lcd_b/bold.ttf", "lcd2", BOLD);
		FontManager.registerFile("res/fonts/lcd_b/boldi.ttf", "lcd2", BOLD_I);
		FontManager.registerFile("res/fonts/lcd_b/italic.ttf", "lcd2", ITALIC);


		FontManager.registerFile("res/fonts/digital_dream/normal.ttf", "dd_r", NORMAL);
		FontManager.registerFile("res/fonts/digital_dream/bold.ttf", "dd_r", BOLD);
		FontManager.registerFile("res/fonts/digital_dream/italic.ttf", "dd_r", ITALIC);
		FontManager.registerFile("res/fonts/digital_dream/bold_italic.ttf", "dd_r", BOLD_I);

		FontManager.registerFile("res/fonts/digital_dream/narrow.ttf", "dd_n", NORMAL);
		FontManager.registerFile("res/fonts/digital_dream/narrow_bold.ttf", "dd_n", BOLD);
		FontManager.registerFile("res/fonts/digital_dream/narrow_italic.ttf", "dd_n", ITALIC);
		FontManager.registerFile("res/fonts/digital_dream/narrow_italic_bold.ttf", "dd_n", BOLD_I);

		FontManager.registerFile("res/fonts/atomic_clock_radio.ttf", "atomic", NORMAL);

		// spaceage
		FontManager.registerFile("res/fonts/spaceage.ttf", "spaceage", NORMAL);
	}

	/**
	 * Load fonts needed for splash.
	 */
	public static void loadForSplash() {
		registerFileNames();

		FontManager.addAlias("splash", "lcd1", 128, HEAVY, " SECTOR_");
		FontManager.addAlias("subsplash", "lcd2", 40, BOLD_I, Glyphs.basic_text);
	}

	/**
	 * Preload all fonts we will use in the game
	 */
	public static void load() {

		// it is in general better to load one larger
		// glyphs set than multiple smaller ones.

		FontManager.nextLoadedClipV(0.2, 0.2);
		FontManager.nextLoadedScale(new Coord(0.6, 1));

		FontManager.addAlias("menu_title", "spaceage", 52, NORMAL, Glyphs.basic_text);

		FontManager.addAlias("gameover", "spaceage", 50, NORMAL, Glyphs.basic_text);


		FontManager.resetNextLoadedSettings();

		FontManager.addAlias("hud", "atomic", 32, NORMAL, Glyphs.basic);


		FontManager.addAlias("score", "lcd1", 48, BOLD, Glyphs.numbers);
		FontManager.addAlias("timer", "dd_n", 32, BOLD_I, Glyphs.numbers);
		FontManager.addAlias("score_small", "lcd1", 26, BOLD, Glyphs.numbers);

		FontManager.nextLoadedCorrection(6, 6);
		FontManager.addAlias("gameover_score", "simple", 40, NORMAL, Glyphs.basic_text);
		FontManager.addAlias("highscore_level_name", "simple", 40, NORMAL, Glyphs.basic_text);
		FontManager.addAlias("small_heading", "simple", 45, NORMAL, Glyphs.basic_text);
		FontManager.addAlias("dialog_heading", "simple", 40, NORMAL, Glyphs.basic_text);
		FontManager.addAlias("small_menu", "simple", 28, NORMAL);
		FontManager.addAlias("login_display1", "simple", 28, NORMAL);
		FontManager.addAlias("login_display2", "simple", 30, NORMAL);
		FontManager.addAlias("larger_text", "simple", 30, NORMAL);


		FontManager.nextLoadedClipV(0.15, 0);
		FontManager.addAlias("small_text", "simple", 28, NORMAL);
		FontManager.addAlias("level_title", "simple", 28, NORMAL);
		FontManager.addAlias("level_subtitle", "simple", 22, NORMAL);
		FontManager.addAlias("small_button", "simple", 26, NORMAL);
		FontManager.nextLoadedCorrection(7, 7);
		FontManager.addAlias("menu", "simple", 64, NORMAL, Glyphs.basic_text);
		FontManager.addAlias("designer_info", "simple", 26, NORMAL);
		FontManager.addAlias("smaller_text", "simple", 22, NORMAL);
		FontManager.addAlias("designer_tooltip", "simple", 24, NORMAL);
		FontManager.addAlias("designer_infopanel", "simple", 18, NORMAL);
		FontManager.addAlias("designer_level", "simple", 18, NORMAL);
		FontManager.resetNextLoadedSettings();

		FontManager.nextLoadedCorrection(7, 7);
		FontManager.addAlias("default", "simple", 24, NORMAL);
		FontManager.addAlias("debug_info", "simple", 24, NORMAL);
		FontManager.resetNextLoadedSettings();



		FontManager.addAlias("score_label", "lcd2", 24, NORMAL);

		Log.i("Fonts loaded = " + FontManager.loadedFontCounter);

	}
}
