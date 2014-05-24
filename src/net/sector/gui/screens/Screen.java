package net.sector.gui.screens;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.FloatBuffer;
import java.util.Random;

import net.sector.App;
import net.sector.Constants;
import net.sector.collision.Scene;
import net.sector.gui.panels.Panel;
import net.sector.gui.panels.PanelEmpty;
import net.sector.input.Keys;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Buffers;
import com.porcupine.struct.Struct5;


/**
 * Screen class.<br>
 * Screen animates 3D world, while contained panels render 2D overlays, process
 * inputs and run the game logic.
 * 
 * @author MightyPork
 */
public abstract class Screen {

	/** stars renderer */
	public static StarfieldRenderer stars = new StarfieldRenderer();

	/** application instance, for easier calls */
	public App app = App.inst;

	/** root panel */
	public Panel rootPanel = new PanelEmpty(this);

	/** RNG */
	public Random rand = new Random();

	/** 3D space manager and renderer */
	public Scene scene = new Scene();

	private boolean enableFog = false;

	/** Rendering of 2D panels enabled */
	public boolean enableOverlay = false;

	/** Rendering of starfield enabled */
	public boolean enableStars = true;

	private boolean fogSetUp = false, cameraSetUp = false;

	/**
	 * Enable fog with given parameters
	 * 
	 * @param fogStart distance from which the fog grows denser
	 * @param fogEnd distance at which the fog is densest
	 * @param fogDensity relative fog density, 1.0 default, 0.3 recommended
	 * @param fogOpacity fog opacity, 1.0 default
	 * @param fogColor fog color
	 * @param bgColor screen background color
	 * @param fogType fog type: GL_LINEAR, GL_EXP, GL_EXP2
	 */
	protected final void setupFog(double fogStart, double fogEnd, double fogDensity, double fogOpacity, RGB fogColor, RGB bgColor, int fogType) {
		// bg
		glClearColor((float) bgColor.r, (float) bgColor.g, (float) bgColor.b, 1.0F);
		//fog
		glFogf(GL_FOG_START, (float) fogStart);
		glFogf(GL_FOG_END, (float) fogEnd);
		glFogi(GL_FOG_MODE, fogType);
		glFog(GL_FOG_COLOR, Calc.Buffers.fBuff((float) fogColor.r, (float) fogColor.g, (float) fogColor.b, (float) fogOpacity));
		glFogf(GL_FOG_DENSITY, (float) fogDensity);
		glHint(GL_FOG_HINT, GL_NICEST);

		fogSetUp = true;
	}

	/**
	 * Enable/disable fog
	 * 
	 * @param enable whether the fog should be rendered
	 */
	protected final void enableFog(boolean enable) {
		enableFog = enable;
		if (enable) {
			glEnable(GL_FOG);
		} else {
			glDisable(GL_FOG);
		}
	}

	/**
	 * Check if fog is enabled
	 * 
	 * @return fog enabled
	 */
	protected final boolean isFogEnabled() {
		return enableFog;
	}

	private Struct5<Coord, Coord, Double, Double, Double> lastCam = null;

	/**
	 * readjust camera position
	 */
	private final void reinitViewport() {
		setupCamera(lastCam.a, lastCam.b, lastCam.c, lastCam.d, lastCam.e);
	}

	/**
	 * handle fullscreen change
	 */
	public final void onFullscreenChange() {
		reinitViewport();
		rootPanel.onViewportChanged();
	}

	/**
	 * handle window resize.
	 */
	public final void onWindowResize() {
		reinitViewport();
		rootPanel.onViewportChanged();
	}

	/**
	 * Set up camera
	 * 
	 * @param eyePos position of the observing eye
	 * @param centerPos point the eye is looking at
	 * @param viewAngle viewing angle (FOV)
	 * @param zNear nearest visible objects (Z axis)
	 * @param zFar farthest visible objects (Z axis)
	 */
	protected final void setupCamera(Coord eyePos, Coord centerPos, double viewAngle, double zNear, double zFar) {
		lastCam = new Struct5<Coord, Coord, Double, Double, Double>(eyePos, centerPos, viewAngle, zNear, zFar);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		// set up perspective

		Coord s = app.getSize();
		gluPerspective((float) viewAngle, (float) s.x / (float) s.y, (float) zNear, (float) zFar);
		gluLookAt((float) eyePos.x, (float) eyePos.y, (float) eyePos.z, (float) centerPos.x, (float) centerPos.y, (float) centerPos.z, 0F, 1F, 0F);

		glViewport(0, 0, s.xi(), s.yi());

		// back to modelview matrix
		glMatrixMode(GL_MODELVIEW);

		cameraSetUp = true;
	}

	/**
	 * Enable / disable 2D overlay
	 * 
	 * @param enable
	 */
	protected void enableOverlay(boolean enable) {
		enableOverlay = enable;
	}

	/**
	 * Enable / disable stars
	 * 
	 * @param enable
	 */
	public void enableStars(boolean enable) {
		enableStars = enable;
	}

	/**
	 * Initialize screen
	 */
	public final void init() {

		if (!stars.hasInit) stars.init();

		initScreen();

		rootPanel.onCreate();
		rootPanel.onFocus();

		if (!cameraSetUp) setupCamera(Constants.CAM_POS, Constants.CAM_LOOKAT, Constants.CAM_ANGLE, Constants.CAM_NEAR, Constants.CAM_FAR);
		if (!fogSetUp) setupFog(Constants.FOG_START, Constants.CAM_FAR, 1.0, 1.0, new RGB(0, 0, 0), new RGB(0, 0, 0), GL_LINEAR);


		// SETUP LIGHTS

		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);

		float spec = Constants.LIGHT_SPECULAR;
		float amb = Constants.LIGHT_AMBIENT;
		float diff = Constants.LIGHT_DIFFUSE;
		Coord pos = Constants.LIGHT_POS;

		FloatBuffer buff = Buffers.mkBuff(4);
		Buffers.fillBuff(buff, amb, amb, amb, 1.0f);
		glLight(GL_LIGHT0, GL_AMBIENT, buff);

		buff.clear();
		Buffers.fillBuff(buff, diff, diff, diff, 1.0f);
		glLight(GL_LIGHT0, GL_DIFFUSE, buff);

		buff.clear();
		Buffers.fillBuff(buff, spec, spec, spec, 1.0f);
		glLight(GL_LIGHT0, GL_SPECULAR, buff);

		buff.clear();
		Buffers.fillBuff(buff, (float) pos.x, (float) pos.y, (float) pos.z, Constants.LIGHT_ATTR);
		glLight(GL_LIGHT0, GL_POSITION, buff); //Position The Light


		// OTHER SETTINGS

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glClearDepth(1f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		glEnable(GL_CULL_FACE);
		glEnable(GL_NORMALIZE);

		glShadeModel(GL_SMOOTH);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
		glDisable(GL_TEXTURE_2D);

	}

	/**
	 * Here you can initialize the screen.
	 */
	public abstract void initScreen();

	public final void update() {

		Mouse.poll();
		Keyboard.poll();
		checkInputEvents();

		getFocusedPanel().update();
	}

	public final void render(float delta) {
		glPushAttrib(GL_ENABLE_BIT);

		if (enableStars) stars.render();

		// render the scene
		scene.render(delta);

		// draw the directly rendered 3D stuff
		rootPanel.render3D();

		if (enableOverlay) {
			rootPanel.render();
		}

		glPopAttrib();
	}


	/**
	 * @return topmost panel which can handle inputs
	 */
	protected final Panel getFocusedPanel() {
		return rootPanel.getTop();
	}

	/**
	 * Check input events and process them.
	 */
	private final void checkInputEvents() {
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			boolean down = Keyboard.getEventKeyState();
			char c = Keyboard.getEventCharacter();
			Keys.onKey(key, down);
			getFocusedPanel().onKey(key, c, down);
		}
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			boolean down = Mouse.getEventButtonState();
			Coord delta = new Coord(Mouse.getEventDX(), Mouse.getEventDY());
			Coord pos = new Coord(Mouse.getEventX(), Mouse.getEventY());
			int wheeld = Mouse.getEventDWheel();

			getFocusedPanel().onMouseButton(button, down, wheeld, pos, delta);
		}

		int xc = Mouse.getX();
		int yc = Mouse.getY();
		int xd = Mouse.getDX();
		int yd = Mouse.getDY();
		int wd = Mouse.getDWheel();

		if (Math.abs(xd) > 0 || Math.abs(yd) > 0 || Math.abs(wd) > 0) {
			getFocusedPanel().onMouseMove(new Coord(xc, yc), new Vec(xd, yd), wd);
		}

		getFocusedPanel().handleStaticInputs();
	}

	public boolean deltaEnabled() {
		return getFocusedPanel().isDeltaEnabled();
	}
}
