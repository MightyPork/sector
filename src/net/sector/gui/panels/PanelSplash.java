package net.sector.gui.panels;


import net.sector.App;
import net.sector.Constants;
import net.sector.GameConfig;
import net.sector.LoadingManager;
import net.sector.effects.Effects;
import net.sector.fonts.FontManager;
import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenMenuMain;
import net.sector.util.Align;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Splash panel
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PanelSplash extends Panel {

	private String shown = "";
	private String remains = "SECTOR";
	private String allOrig = remains;
	private String subsplash = "";
	private long counter = 0, finishedTimer = -1, finishedTimerD = 0;

	/**
	 * @param screen
	 */
	public PanelSplash(Screen screen) {
		super(screen);
	}

	@Override
	public boolean isDeltaEnabled() {
		return true;
	}

	@Override
	public void onCreate() {
		if (!GameConfig.enableSplash) {
			shown = remains;
			remains = "";
			finishedTimer = 0;
		}
	}

	@Override
	public void onClose() {}

	@Override
	public void onWindowChanged() {}

	@Override
	public boolean hasBackgroundLayer() {
		return false;
	}

	@Override
	protected void renderPanel() {

		FontManager.setFullscreenDoubleSize(true);

		Coord s = app.getSize();


		double left = s.x / 2 - FontManager.width("splash", allOrig) / 2;
		double down = (s.y / 2) - FontManager.height("splash") / 3 + 20;
		Coord pos = new Coord(left, down);

		boolean underscore = (counter % (Constants.FPS_UPDATE / 4) > (Constants.FPS_UPDATE / 4) / 2 && remains.length() > 0);
		String txt = shown + (remains.length() == 0 ? "" : underscore ? "_" : " ");

		FontManager.drawFuzzy(pos, txt, "splash", new RGB(0x0033ff, 0.3), new RGB(0x00ff00, 1), 8 * App.fs2(), Align.LEFT, false);

		FontManager.drawFuzzy(new Coord(s.x / 2, down - FontManager.height("subsplash")), subsplash, "subsplash", new RGB(0x9999ff, 0.05), new RGB(
				0xffffff), 2 * App.fs2(), Align.CENTER);

		FontManager.setFullscreenDoubleSize(false);
	}

	@Override
	public void update() {
		counter++;
		if (finishedTimer >= 0) finishedTimer++;
		if (counter % ((int) (Constants.FPS_UPDATE / 1.5)) == 0 && remains.length() > 0) {
			shown += remains.charAt(0);
			remains = remains.substring(1);
			if (remains.length() == 0) {
				finishedTimer = 0;
			}
		}

		if (finishedTimer == -1 && counter % (Constants.FPS_UPDATE / 10) == 0) {
			//for(int i=0; i<5+rand.nextInt(5); i++) {
			Effects.addBinaries(scene.particles, new Coord(-1.5 + rand.nextDouble() * 3, 2.2 + 0.8 * rand.nextDouble(), 0), 7);
//				Effects.addExplosion(scene.particles, new Coord(-1.5 + rand.nextDouble() * 3, 2.3+0.6*rand.nextDouble(), 0), Vec.ZERO, 3, false, false, false);
			//}

			//for(int i=0;i<10;i++) Effect.addStar(scene.particles, new Coord(-1 + rand.nextDouble() * 2, 2.6, 0));
		}

		if (finishedTimer == Constants.FPS_UPDATE * 1) {
			subsplash = "Loading...";
		}


		if (finishedTimer > Constants.FPS_UPDATE * 2 && (finishedTimer - finishedTimerD) > Constants.FPS_UPDATE * 0.2) {
			if (LoadingManager.hasMoreGroups()) {
				finishedTimerD = finishedTimer;
				LoadingManager.loadGroup();
			} else {
				LoadingManager.onResourcesLoaded();
				app.replaceScreen(new ScreenMenuMain());
				FontManager.destroyFont("splash");
				FontManager.destroyFont("subsplash");
			}
		}
		if (finishedTimer > Constants.FPS_UPDATE * 1.5) {
			subsplash = LoadingManager.getSplashInfo();
		}

		scene.update();
	}
}
