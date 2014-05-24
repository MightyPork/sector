package net.sector.gui.screens;


import net.sector.gui.panels.PanelMenu;
import net.sector.sounds.Music;


/**
 * main menu screen
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ScreenMenuMain extends Screen {

	@Override
	public void initScreen() {
		Music.playMenu();

		enableOverlay(true);
		enableStars(true);

		rootPanel = new PanelMenu(this);
	}


}
