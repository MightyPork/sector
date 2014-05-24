package net.sector.gui.screens;


import net.sector.gui.panels.game.PanelGame;
import net.sector.level.GameContext;
import net.sector.sounds.Music;


/**
 * main game screen
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ScreenGame extends Screen {

	private GameContext context;

	public ScreenGame(GameContext context) {
		this.context = context;
	}

	@Override
	public void initScreen() {
		Music.playIngame();

		// use default camera position and fog settings

		// all of these are enabled by default
		// but we can make sure by re-enabling
		enableOverlay(true);
		enableFog(true);
		enableStars(true);

		rootPanel = new PanelGame(this, context);

	}

}
