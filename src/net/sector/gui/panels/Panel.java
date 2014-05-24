package net.sector.gui.panels;


import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import net.sector.App;
import net.sector.collision.Scene;
import net.sector.gui.screens.Screen;
import net.sector.input.IInputHandler;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Panel class, control module for screen.
 * 
 * @author MightyPork
 */
public abstract class Panel implements IInputHandler {

	/** The screen this panel belongs to */
	public Screen screen;

	/** The application instance */
	public App app = App.inst;

	private Panel below = null;

	private Panel above = null;

	/** RNG */
	public Random rand = new Random();

	/** Scene from screen */
	public Scene scene;

	/**
	 * New panel
	 * 
	 * @param screen parent screen
	 */
	public Panel(Screen screen) {
		this.screen = screen;
		this.scene = screen.scene;
	}

	/**
	 * @return true if this panel has no parent
	 */
	public final boolean isRoot() {
		return below == null;
	}

	/**
	 * @return true if this panel is the topmost, focused
	 */
	public final boolean isTop() {
		return above == null;
	}

	/**
	 * @return true if this panel is focused, topmost
	 */
	public final boolean isFocused() {
		return isTop();
	}

	/**
	 * @return topmost panel in the panel stack
	 */
	public final Panel getTop() {
		if (isTop()) return this;
		return above.getTop();
	}

	/**
	 * @return panel below this one, if any
	 */
	public final Panel getBelow() {
		if (isRoot()) return null;
		return below;
	}

	/**
	 * Destroy all panels above this panel.
	 */
	public final void destroyAbove() {
		if (!isTop()) {
			above.destroyAbove();
			above.onBlur();
			above.onClose();
			above = null;
		}
	}

	/**
	 * Close this panel (if not root panel), and give focus to the panel below.
	 */
	public final void closePanel() {
		if (!isRoot()) {
			below.destroyAbove();
			onBlur();
			below.onFocus();
		}
	}

	/**
	 * Add a panel to the stack and focus it.
	 * 
	 * @param added added child
	 */
	public final void openPanel(Panel added) {

		above = added;
		above.below = this;
		this.onBlur();
		above.onCreate();
		above.onFocus();
	}

	/**
	 * Hook called when panel was openned
	 */
	public abstract void onCreate();

	/**
	 * @return true if this panel has dark translucent background.
	 */
	public abstract boolean hasBackgroundLayer();

	/**
	 * Get color of background layer
	 * 
	 * @return color
	 */
	public RGB getBackgroundColor() {
		return new RGB(0, 0.7);
	}

	/**
	 * Called before the panel is closed.
	 */
	public abstract void onClose();

	/**
	 * Called when this panel is focused.
	 */
	public void onFocus() {}

	/**
	 * Called when this panel loses focus.
	 */
	public void onBlur() {}

	/**
	 * Update this panel.<br>
	 * In case of root panel, you should update scene here.
	 */
	public abstract void update();

	/**
	 * Render directly rendered 3D stuff in all panels
	 */
	public final void render3D() {
		renderDirect3D();
		if (!isTop()) {
			getTop().render3D();
		}
	}

	/**
	 * Render the inside elements of this panel
	 */
	protected abstract void renderPanel();

	/**
	 * Render 3D stuff from this panel
	 */
	public void renderDirect3D() {}


	/**
	 * Called each update tick, if the mouse position was changed.
	 * 
	 * @param pos
	 * @param move
	 */
	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {}


	/**
	 * Mouse event handler.
	 * 
	 * @param button button which caused this event
	 * @param down true = down, false = up
	 * @param wheelDelta number of steps the wheel turned since last event
	 * @param pos mouse position
	 * @param deltaPos delta maouse position
	 */
	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {}


	/**
	 * Key event handler.
	 * 
	 * @param key key index, constant Keyboard.KEY_???
	 * @param c character typed, if any
	 * @param down true = down, false = up
	 */
	@Override
	public void onKey(int key, char c, boolean down) {}


	/**
	 * In this method screen can handle static inputs, that is:
	 * Keyboard.isKeyDown, Mouse.isButtonDown etc.
	 */
	@Override
	public void handleStaticInputs() {}


	/**
	 * Render all the panels in the stack, if this is the root panel.
	 */
	public final void render() {

		if (!isRoot()) return;

		//@formatter:off
		glMatrixMode(GL_PROJECTION);

		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);
		
			glLoadIdentity();
			Coord s = app.getSize();
			glOrtho(0, s.x, 0, s.y, -1000, 1000);
	
			glMatrixMode(GL_MODELVIEW);
	
			glPushAttrib(GL_ENABLE_BIT);
			glPushMatrix();
			
				glLoadIdentity();
		
				glDisable(GL_LIGHTING);
				glDisable(GL_FOG);
				glDisable(GL_COLOR_MATERIAL);
				glEnable(GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
				renderThisAndChildren();
	
			glPopMatrix();
			glPopAttrib();
	
			glMatrixMode(GL_PROJECTION);
			
		glPopAttrib();
		glPopMatrix();
		
		glMatrixMode(GL_MODELVIEW);
		//@formatter:on
	}

	private final void renderThisAndChildren() {
		glDisable(GL_TEXTURE_2D);

		if (hasBackgroundLayer()) {

			Coord size = app.getSize();

			RenderUtils.setColor(getBackgroundColor());
			//glColor4d(0, 0, 0, 0.6);
			glBegin(GL_QUADS);
			glVertex2d(0, size.y);
			glVertex2d(size.x, size.y);
			glVertex2d(size.x, 0);
			glVertex2d(0, 0);
			glEnd();
		}

		renderPanel();

		// render any higher guis
		if (!isTop()) {
			glTranslated(0, 0, 50);
			getAbove().renderThisAndChildren();
		}
	}


	private Panel getAbove() {
		return above;
	}

	/**
	 * On window resized
	 */
	public abstract void onWindowChanged();

	/**
	 * On viewport changed (fullscreen etc)
	 */
	public final void onViewportChanged() {
		onWindowChanged();
		if (!isTop()) {
			getTop().onViewportChanged();
		}
	}

	/**
	 * Return true if delta timing is enabled for scene.
	 * 
	 * @return do delta timing
	 */
	public boolean isDeltaEnabled() {
		return false;
	}
}
