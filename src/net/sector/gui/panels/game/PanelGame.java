package net.sector.gui.panels.game;


import static org.lwjgl.opengl.GL11.*;
import net.sector.App;
import net.sector.Constants;
import net.sector.GameConfig;
import net.sector.entities.player.EntityPlayerShip;
import net.sector.fonts.FontManager;
import net.sector.gui.panels.Panel;
import net.sector.gui.screens.ScreenGame;
import net.sector.input.EInput;
import net.sector.input.InputTriggerGroup;
import net.sector.input.Routine;
import net.sector.level.ELevel;
import net.sector.level.GameContext;
import net.sector.level.GameCursor;
import net.sector.level.SuperContext;
import net.sector.level.sequence.HudMessage;
import net.sector.level.ship.PieceRegistry;
import net.sector.level.ship.modules.pieces.Piece;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.util.StringUtils;


/**
 * panel animating the 3D game
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PanelGame extends Panel {

	// soft area
	private static final double moveZoneX = 6;
	private static final double moveZoneZ = 6;
	//border around soft area
	private static final double moveBrd = 1.5;
	// z offset of min Z coords
	private static final double moveMinZ = 2;


	private static final Coord SHIP_MIN_H = new Coord(-moveZoneX / 2 - moveBrd, 0, moveMinZ);
	private static final Coord SHIP_MAX_H = new Coord(moveZoneX / 2 + moveBrd, 0, moveMinZ + moveZoneZ + 2 * moveBrd);

	private static final Coord SHIP_MIN_S = new Coord(-moveZoneX / 2, 0, moveMinZ + moveBrd);
	private static final Coord SHIP_MAX_S = new Coord(moveZoneX / 2, 0, moveMinZ + moveZoneZ + moveBrd);
	private static final double BORDER_SLOWDOWN = 0.25;

	private ScreenGame screenGame = null;

	private EntityPlayerShip ship;

	private static boolean enableOverlay = true;
	private static boolean enableDamageDisplay = true;
	private static boolean enableDebugOverlay = false;

	private GameContext context;
	private GameCursor cursor;

	private String hudMessage = null;
	private int hudTicks = 0;
	private double hudAlpha = 0;

	private InputTriggerGroup triggers = new InputTriggerGroup();

	// functions.
	private class FnPause implements Routine {
		@Override
		public void run() {
			PanelGame.this.openPanel(new PanelGamePause(screenGame));
		}
	}

	private class FnToggleOverlay implements Routine {
		@Override
		public void run() {
			enableOverlay ^= true;
		}
	}

	private class FnToggleDamageDisplay implements Routine {
		@Override
		public void run() {
			enableDamageDisplay ^= true;
		}
	}

	private class FnToggleDebugOverlay implements Routine {
		@Override
		public void run() {
			enableDebugOverlay ^= true;
		}
	}

	private class FnToggleColiderWireframe implements Routine {
		@Override
		public void run() {
			GameConfig.colliderWireframe ^= true;
		}
	}

	private class FnAngleCenter implements Routine {
		@Override
		public void run() {
			ship.rotationCenterRequested = true;
		}
	}

	private class FnAngleLeft implements Routine {
		@Override
		public void run() {
			if (ship.rotAngle.get() < EntityPlayerShip.MAXROT) ship.angleInc += 5;
		}
	}

	private class FnAngleRight implements Routine {
		@Override
		public void run() {
			if (ship.rotAngle.get() > -EntityPlayerShip.MAXROT) ship.angleInc += -5;
		}
	}


	/**
	 * Panel for game
	 * 
	 * @param screen container screen
	 * @param context ship bundle
	 */
	public PanelGame(ScreenGame screen, GameContext context) {
		super(screen);

		this.context = context;
		this.cursor = context.getCursor();

		screenGame = screen;
		ship = new EntityPlayerShip(new Coord(0, 0, moveMinZ + moveZoneZ / 2), cursor);

		Routine f = null;

		// pause key
		triggers.addTrigger(f = new FnPause(), EInput.KEY_PRESS, Keyboard.KEY_ESCAPE);
		triggers.addTrigger(f, EInput.KEY_PRESS, Keyboard.KEY_PAUSE);
		triggers.addTrigger(f, EInput.KEY_PRESS, Keyboard.KEY_P);

		//toggle overlay
		triggers.addTrigger(new FnToggleOverlay(), EInput.KEY_PRESS, Keyboard.KEY_F4);
		triggers.addTrigger(new FnToggleDamageDisplay(), EInput.KEY_PRESS, Keyboard.KEY_F3);
		triggers.addTrigger(new FnToggleColiderWireframe(), EInput.KEY_PRESS, Keyboard.KEY_F8);
		triggers.addTrigger(new FnToggleDebugOverlay(), EInput.KEY_PRESS, Keyboard.KEY_F7);

		triggers.addTrigger(new FnAngleCenter(), EInput.BTN_DOWN, 2);

		triggers.addTrigger(new FnAngleLeft(), EInput.SCROLL, 1);
		triggers.addTrigger(new FnAngleRight(), EInput.SCROLL, -1);


	}

	@Override
	public void onCreate() {

		scene.add(ship);
		scene.update();

		// init level controller, set scene.
		cursor.getLevel().setScene(scene);
		cursor.getLevel().reset();
	}

	@Override
	public void onClose() {}

	@Override
	public boolean hasBackgroundLayer() {
		return false;
	}

	@Override
	public boolean isDeltaEnabled() {
		return true;
	}

	@Override
	protected void renderPanel() {
		if (!enableOverlay) return;

		double fs2 = App.isFullscreen() ? 2 : 1;

		FontManager.setFullscreenDoubleSize(true);

		int scoreW = (int) FontManager.width("score", "000000");
		int scoreH = (int) FontManager.height("score") + (int) FontManager.height("score_label");

		double winW = App.inst.getSize().x;
		double winH = App.inst.getSize().y;

		double barH = 10 * fs2;
		double barMargin = 10 * fs2;
		double barW = winW - barMargin * 2 - scoreW;
		double barPadding = 2 * fs2;

		Coord barBoxMin = new Coord(barMargin, winH - barMargin - barH);
		Coord barBoxMax = new Coord(barMargin + barW, winH - barMargin);


		// energy line
		glColor3d(0, 0.3, 0);
		RenderUtils.quadCoord(barBoxMin, barBoxMax);

		RGB dark = new RGB(0, 0.6, 0);
		RGB light = new RGB(0, 1, 0);

		double ratio = ship.body.energySystem.getStorageRatio();
		Coord lower = barBoxMin.add(barPadding, barPadding);
		Coord higher = barBoxMin.add((barW - barPadding * 2) * ratio, barH - barPadding);

		RenderUtils.quadCoordGradVBilinear(lower, higher, dark, light);


		// render shield bar only if shield is discovered.
		if (ship.body.shieldSystem.level > 0 && ship.body.shieldSystem.getLoadRatio() > 0) {

			barBoxMin.sub_ip(0, barH + barPadding);
			barBoxMax.sub_ip(0, barH + barPadding);

			// shield line
			glColor3d(0, 0, 0.5);
			RenderUtils.quadCoord(barBoxMin, barBoxMax);

			dark = new RGB(0.2, 0.2, 0.7);
			light = new RGB(0.3, 0.3, 1);

			ratio = ship.body.shieldSystem.getLoadRatio();
			lower = barBoxMin.add(barPadding, barPadding);
			higher = barBoxMin.add((barW - barPadding * 2) * ratio, barH - barPadding);

			RenderUtils.quadCoordGradVBilinear(lower, higher, dark, light);
		}

		double dist = 12;
		double size = 10;
		double sizeSmall = 8;

		if (App.isFullscreen()) {
			dist *= 2;
			size *= 2;
			sizeSmall *= 2;
		}

		if (enableDamageDisplay && deathTimer == -1) {
			glPushMatrix();
			glTranslated((winW - dist * 6), winH - (scoreH + 20 + dist * 6), 0);
			glRotated(ship.rotAngle.d, 0, 0, 1);
			for (Piece p : ship.body.allPieces) {
				if (p.isDead) continue;

				Coord offset = p.getRelativeCoordToCenter();
				offset.y = offset.z;
				offset.z = 0;

				RGB color = PieceRegistry.getDamageColor(p.getHealth(), p.getHealthMax());
				color.setAlpha_ip(0.7);
				RenderUtils.setColor(color);

				double sz = (p.isBody() ? size : sizeSmall);

				glPushMatrix();
				RenderUtils.translate(offset.mul(dist));
				RenderUtils.quadCoord(new Coord(-sz / 2, -sz / 2), new Coord(sz / 2, sz / 2));
				glPopMatrix();
			}
			glPopMatrix();
		}

		glColor3d(1, 1, 1);

		glEnable(GL_TEXTURE_2D);

		Coord s = app.getSize();
		Coord pos = s.copy();
		FontManager.setAlign(Align.RIGHT);
		pos.x -= 10 * fs2;
		pos.y -= FontManager.height("score_label");
		FontManager.draw(pos, "Score", "score_label", new RGB(1, 0, 0));
		pos.y -= FontManager.height("score");
		FontManager.draw(pos, StringUtils.formatInt(cursor.scoreTotal) + "", "score", new RGB(1, 0.9, 0));

//		// Money display for campaign
//		pos.x -= 10 * fs2;
//		pos.y -= FontManager.height("score_small");
//		FontManager.draw(pos, Calc.formatDotThousand(cursor.money) + "", "score_small", new RGB(1, 0, 0.9));

		if (cursor.getLevel().hasTimer()) {
			pos.setTo(s);
			pos.setX_ip(10 * fs2);
			pos.add_ip(FontManager.width("timer", "99:99"), -50 * fs2 - FontManager.height("timer"));
			FontManager.draw(pos, cursor.getLevel().getTimer().getRemainingTimeFormatted(), "timer", new RGB(1, 1, 1, 0.8), Align.RIGHT);
		}

		if (hudMessage != null) {
			pos.setTo(s);
			pos.mul_ip(0.5);
			pos.y *= 1.5;
			pos.sub_ip(0, FontManager.height("hud") / 2);
			FontManager.draw(pos, hudMessage, "hud", new RGB(0, 1, 0, hudAlpha), Align.CENTER);
		}

		if (enableDebugOverlay) {
			FontManager.setFullscreenDoubleSize(false);
			FontManager.setFont("debug_info", -1);
			double h = FontManager.height();

			pos.setTo(5, 5 + h * 4 + 10);
			FontManager.draw(pos, "FPS: " + app.fpsMeter.getFPS(), new RGB(1, 1, 1), -1);
			pos.y -= h;
			FontManager.draw(pos, "Dropped frames: " + app.fpsMeter.getDropped(), new RGB(1, 1, 1), -1);
			pos.y -= h;
			FontManager.draw(pos, "Particles: " + scene.particles.size(), new RGB(0, 1, 1), -1);
			pos.y -= h;
			FontManager.draw(pos, "Entities:  " + scene.allEntities.size(), new RGB(0, 1, 0), -1);
		}

		glDisable(GL_TEXTURE_2D);
		FontManager.setFullscreenDoubleSize(false);
	}

	@Override
	public void onWindowChanged() {
		Mouse.setGrabbed(false);
		Mouse.poll();
		if (isTop()) Mouse.setGrabbed(true);
	}

	@Override
	public void onFocus() {
		cursor.getLevel().resume();
		Mouse.setGrabbed(true);
		Sounds.timer_loop.resumeLoop();
		Sounds.shield_loop.resumeLoop();
	}

	@Override
	public void onBlur() {
		cursor.getLevel().pause();
		Mouse.setGrabbed(false);
		Sounds.timer_loop.pauseLoop();
		Sounds.shield_loop.pauseLoop();
	}

	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {
		ship.onMouseMove(pos, move, wheelDelta);

		double acceleration = ship.getAcceleration() * (GameConfig.mouseSensitivity / 1000d);
		ship.getMotion().add_ip((move.x / app.getSize().x) * acceleration, 0, (move.y / app.getSize().y) * acceleration);
	}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		if (!Mouse.isGrabbed()) Mouse.setGrabbed(true);

		ship.onMouseButton(button, down, wheelDelta, pos, deltaPos);
		triggers.onMouseButton(button, down, wheelDelta, pos, deltaPos);
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		ship.onKey(key, c, down);
		triggers.onKey(key, c, down);
	}

	@Override
	public void handleStaticInputs() {
		ship.handleStaticInputs();
		triggers.handleStaticInputs();
	}

	private boolean timer_ending_sound = false;
	private int deathTimer = -1;

	private void openGameOverScreen() {
		if (SuperContext.selectedUser == null || App.offlineMode) {
			openPanel(new PanelGameOver_Guest(screenGame));
		} else {
			// registered active user
			if (context.levelType == ELevel.NET) {
				openPanel(new PanelGameOverShared_User(screenGame));
			} else {
				openPanel(new PanelGameOver_User(screenGame));
			}
		}
	}

	@Override
	public void update() {
		if (!Display.isActive()) return;

		if (cursor.getLevel().hasTimer() && cursor.getLevel().getTimer().getRemainingTime() == 5) {
			if (!timer_ending_sound) Sounds.timer_loop.playAsEffectLoop(1, 0.6f);
			timer_ending_sound = true;
		}

		if (cursor.getLevel().isEnded()) {
			Sounds.timer_loop.stop();
			Sounds.timer_end.playAsSoundEffect(1, 0.5f, false);
			openGameOverScreen();
			return;
		}

		if (ship.isDead()) {
			Sounds.timer_loop.stop();

			if (deathTimer < Constants.FPS_UPDATE * 2) {
				deathTimer++;
			} else {
				openGameOverScreen();
			}
		}

		cursor.getLevel().onGameTick();
		scene.setGlobalMovement(cursor.getLevel().getGlobalMovement());

		if (hudTicks <= 0 && hudAlpha <= 0) {
			if (cursor.getLevel().hasHudMessage()) {
				HudMessage hm = cursor.getLevel().getOneHudMessage();
				hudMessage = hm.text;
				hudTicks = (int) (Constants.FPS_UPDATE * hm.secs);
			}
		}

		if (hudTicks > 0) {
			hudTicks--;
			if (hudAlpha < 1) hudAlpha += 0.03 * Constants.SPEED_MUL;
		} else {
			if (hudAlpha > 0) hudAlpha -= 0.05 * Constants.SPEED_MUL;
		}

		if (hudAlpha < 0) hudAlpha = 0;
		if (hudAlpha > 1) hudAlpha = 1;



		// limit ship movement.

		Coord pos = ship.getPos();
		pos.pushLast();
		Vec motion = ship.getMotion();

		if (pos.x < SHIP_MIN_S.x) {
			if (Calc.inRange(pos.x, SHIP_MIN_H.x, SHIP_MIN_S.x)) {
				if (motion.x < 0) motion.x *= 1 - BORDER_SLOWDOWN;
			} else {
				pos.x = SHIP_MIN_H.x;
				if (motion.x < 0) motion.x = 0;
			}
		}

		if (pos.x > SHIP_MAX_S.x) {
			if (Calc.inRange(pos.x, SHIP_MAX_S.x, SHIP_MAX_H.x)) {
				if (motion.x > 0) motion.x *= 1 - BORDER_SLOWDOWN;
			} else {
				pos.x = SHIP_MAX_H.x;
				if (motion.x > 0) motion.x = 0;
			}
		}


		if (pos.z < SHIP_MIN_S.z) {
			if (Calc.inRange(pos.z, SHIP_MIN_H.z, SHIP_MIN_S.z)) {
				if (motion.z < 0) motion.z *= 1 - BORDER_SLOWDOWN;
			} else {
				pos.z = SHIP_MIN_H.z;
				if (motion.z < 0) motion.z = 0;
			}
		}

		if (pos.z > SHIP_MAX_S.z) {
			if (Calc.inRange(pos.z, SHIP_MAX_S.z, SHIP_MAX_H.z)) {
				if (motion.z > 0) motion.z *= 1 - BORDER_SLOWDOWN;
			} else {
				pos.z = SHIP_MAX_H.z;
				if (motion.z > 0) motion.z = 0;
			}
		}

		pos.update();

		scene.update();
		ship.getMotion().scale_ip(1D - ship.getDecelerate());

	}


	@Override
	public void renderDirect3D() {}


}
