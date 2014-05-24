package net.sector.gui.screens;


import net.sector.gui.panels.designer.PanelDesigner;
import net.sector.level.GameContext;
import net.sector.level.SuperContext;
import net.sector.sounds.Music;


/**
 * ship designer screen
 * 
 * @author MightyPork
 */
public class ScreenDesigner extends Screen {

	@Override
	public void initScreen() {

		enableOverlay(true);
		enableFog(true);
		enableStars(true);

		Music.playDesigner();

		GameContext ctx = SuperContext.getGameContext();
		rootPanel = new PanelDesigner(this, ctx);
	}

}
