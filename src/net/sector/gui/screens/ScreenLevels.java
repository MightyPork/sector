package net.sector.gui.screens;


import net.sector.gui.panels.PanelChallenges;
import net.sector.sounds.Music;


/**
 * screen for level selection.
 * 
 * @author MightyPork
 */
public class ScreenLevels extends Screen {

	@Override
	public void initScreen() {
		Music.playMenu();

		enableOverlay(true);
		enableStars(true);

		rootPanel = new PanelChallenges(this);
	}


}
