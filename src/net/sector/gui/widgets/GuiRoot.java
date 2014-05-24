package net.sector.gui.widgets;


import static net.sector.util.Align.*;
import static org.lwjgl.opengl.GL11.*;
import net.sector.App;
import net.sector.fonts.FontManager;
import net.sector.gui.panels.Panel;
import net.sector.util.Align;

import org.lwjgl.input.Mouse;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Root widget, container of layouts
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class GuiRoot {

	/**
	 * Widget event listener
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 */
	public static interface EventListener {
		/**
		 * On action performed - button clicked etc.
		 * 
		 * @param widget widget responsible for the event.
		 */
		public void actionPerformed(Widget widget);
	}

	/** root child */
	public Widget child = null;
	/** focused widget */
	public Widget focus = null;
	/** event listener */
	public EventListener listener = null;

	/** Horizontal align to window */
	public int alignH = CENTER;

	/** Vertical align to window */
	public int alignV = CENTER;
	private Panel panel;

	private String tooltipText = "";
	private RGB tooltipColor = RGB.WHITE;
	private boolean tooltipShown = false;

	public void clearTooltip() {
		tooltipShown = false;
	}

	public void setTooltip(String text, RGB color) {
		tooltipText = text.trim();
		tooltipColor = color;
		tooltipShown = true;
	}

	/**
	 * Gui widget root element
	 * 
	 * @param listener listener - for widget events
	 * @param child root child (a layout)
	 */
	public GuiRoot(EventListener listener, Widget child) {
		this.listener = listener;
		this.child = child;
		if (listener instanceof Panel) panel = (Panel) listener;
	}

	public GuiRoot setParentPanel(Panel p) {
		this.panel = p;
		return this;
	}

	public boolean isPanelOnTop() {
		return panel.isFocused();
	}

	/**
	 * Add primary child
	 * 
	 * @param layout
	 */
	public void setRootWidget(Widget layout) {
		this.child = layout;
	}

	/**
	 * Set alignment relative to window; Use constants from Align class.
	 * 
	 * @param horizontal horizontal align
	 * @param vertical vertical align
	 * @return this
	 */
	public GuiRoot setAlign(int horizontal, int vertical) {
		alignH = horizontal;
		alignV = vertical;
		return this;
	}

	/**
	 * Set alignment relative to window; Use constants from Align class.
	 * 
	 * @param horizontal horizontal align
	 * @return this
	 */
	public GuiRoot setAlignH(int horizontal) {
		alignH = horizontal;
		return this;
	}

	/**
	 * Set alignment relative to window; Use constants from Align class.
	 * 
	 * @param vertical vertical align
	 * @return this
	 */
	public GuiRoot setAlignV(int vertical) {
		alignV = vertical;
		return this;
	}

	/**
	 * Do render - centered on screen.
	 */
	public void render() {

		clearTooltip();

		glPushMatrix();

		Coord lower = getLowerPoint();

		glTranslated(lower.x, lower.y, 0);
		glPushMatrix();
		child.render(getRelativeMousePos());
		glPopMatrix();

		if (tooltipShown && isPanelOnTop()) {
			Coord mouse = getRelativeMousePos();

			String font = "designer_tooltip";

			int h = (int) FontManager.height(font);
			Coord pos = mouse.add(20, -h / 2, 60);

			RGB shadowC = new RGB(0, 0.4);

			FontManager.drawFuzzy(pos, tooltipText, font, shadowC, tooltipColor, 2, Align.LEFT);
		}

		glPopMatrix();


	}

	private Coord getRelativeMousePos() {
		Coord lower = getLowerPoint();
		return new Coord(Mouse.getX() - lower.x, Mouse.getY() - lower.y);
	}

	/**
	 * Get lower point of this root widget - base for relative coordinates.
	 * 
	 * @return lower coord.
	 */
	private Coord getLowerPoint() {
		Coord cSize = child.getSize();
		Coord wSize = App.inst.getSize();
		Coord lower = new Coord();

		switch (alignH) {
			case LEFT:
				lower.x = 0;
				break;
			case CENTER:
				lower.x = (wSize.x - cSize.x) / 2;
				break;
			case RIGHT:
				lower.x = wSize.x - cSize.x;
				break;
		}

		switch (alignV) {
			case BOTTOM:
				lower.y = 0;
				break;
			case CENTER:
				lower.y = (wSize.y - cSize.y) / 2;
				break;
			case TOP:
				lower.y = wSize.y - cSize.y;
				break;
		}

		return lower;
	}

	/**
	 * Remove old focus and focus given widget.
	 * 
	 * @param newFocus widget to focus or null.
	 */
	public void setFocus(Widget newFocus) {

		if (focus != null) {
			focus.focused = false;
			focus.onBlur();
		}

		focus = newFocus;

		if (focus != null) {
			focus.focused = true;
			focus.onFocus();
		}

	}

	/**
	 * Handle mouse button
	 * 
	 * @param button button id
	 * @param down is down
	 */
	public void onMouseButton(int button, boolean down) {
		Widget consumer = child.onMouseButton(getRelativeMousePos(), button, down);
		if (consumer != focus && down) {
			setFocus(consumer);
		}
		if (consumer != null) listener.actionPerformed(consumer);
	}

	/**
	 * handle mouse scroll
	 * 
	 * @param scroll -1,0,1
	 */
	public void onScroll(int scroll) {
		Widget consumer = child.onScroll(getRelativeMousePos(), scroll);
		if (consumer != focus && consumer != null) {
			setFocus(consumer);
		}
		if (consumer != null) listener.actionPerformed(consumer);
	}

	/**
	 * handle key press
	 * 
	 * @param key key index
	 * @param chr character typed
	 * @param down
	 */
	public void onKeyDown(int key, char chr, boolean down) {

		Widget consumer = child.onKey(key, chr, down);
		if (consumer != focus && consumer != null) {
			setFocus(consumer);
		}
		if (consumer != null) listener.actionPerformed(consumer);
	}

	/**
	 * Handle analog inputs
	 */
	public void handleStaticInputs() {
		child.handleStaticInputs(getRelativeMousePos());
	}

	/**
	 * Recalculate children positions and sizes.
	 */
	public void updatePositions() {
		child.setGuiRoot(this);
		child.calcChildSizes();
	}

	/**
	 * Get the panel.
	 * 
	 * @return panel
	 */
	public Panel getPanel() {
		return panel;
	}

}
