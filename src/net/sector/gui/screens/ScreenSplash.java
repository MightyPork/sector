package net.sector.gui.screens;


import net.sector.gui.panels.PanelSplash;
import net.sector.sounds.Music;



/**
 * Splash screen
 * 
 * @author MightyPork
 */
public class ScreenSplash extends Screen {

	@Override
	public void initScreen() {
		Music.playIntro();

		enableOverlay(true);
		enableStars(true);

		rootPanel = new PanelSplash(this);
	}


}
