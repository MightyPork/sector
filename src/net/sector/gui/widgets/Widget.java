package net.sector.gui.widgets;


import net.sector.gui.panels.Panel;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Rect;


/**
 * Abstract widget class
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class Widget implements IWidget {

	/** Theme */
	public ETheme theme = ETheme.BLUE;

	/** Widget text */
	public String text = "";
	/** Text render font */
	public String font = "default";
	/** widget id */
	public int id = -1;
	/** flag is visible */
	public boolean visible = true;
	/** flag is enabled */
	public boolean enabled = true;
	/** has focus */
	public boolean focused = false;
	/** bounding rectangle */
	public Rect rect = new Rect();
	/** required margins */
	public WidgetMargins margins = new WidgetMargins(5, 5, 5, 5);
	/** minimal size */
	public Coord minSize = new Coord();
	/** Gui hierarchy root */
	public GuiRoot guiRoot;
	/** Widget tag */
	public String tag = "";
	/** Selected flag (for toggle buttons and similar) */
	public boolean selected = false;
	/** Clicked flag */
	public boolean clicked = false;

	/**
	 * Get widget font
	 * 
	 * @return font name
	 */
	public final String getFont() {
		return font;
	}

	/**
	 * Set widget font
	 * 
	 * @param font font name
	 * @return this
	 */
	public final Widget setFont(String font) {
		this.font = font;
		return this;
	}

	/**
	 * Get if widget is focused
	 * 
	 * @return is focused
	 */
	public final boolean isFocused() {
		return focused;
	}

	/**
	 * Set widget focused
	 * 
	 * @param focused focused flag
	 * @return this
	 */
	public final Widget setFocused(boolean focused) {
		this.focused = focused;
		return this;
	}

	/**
	 * Get theme
	 * 
	 * @return theme
	 */
	public final ETheme getTheme() {
		return theme;
	}

	/**
	 * Set widget theme
	 * 
	 * @param theme theme to set
	 * @return this
	 */
	public final Widget setTheme(ETheme theme) {
		this.theme = theme;
		return this;
	}

	/**
	 * Get if this widget has "SELECTED" flag.
	 * 
	 * @return has selected flag.
	 */
	public final boolean isSelected() {
		return selected;
	}

	/**
	 * Get if this widget has "CLICKED" flag (used in buttons and similar).
	 * 
	 * @return has selected flag.
	 */
	public final boolean isClicked() {
		return false;
	}

	/**
	 * Set "SELECTED" flag.
	 * 
	 * @param state flag state
	 * @return this
	 */
	public final Widget setSelected(boolean state) {
		selected = state;
		return this;
	}

	/**
	 * Set "CLICKED" flag.
	 * 
	 * @param state flag state
	 * @return this
	 */
	public final Widget setClicked(boolean state) {
		clicked = state;
		return this;
	}

	/**
	 * Get color from theme
	 * 
	 * @param role color role
	 * @param mouse mouse position
	 * @return RGB color
	 */
	public final RGB getColor(EColorRole role, Coord mouse) {
		ColorScheme cs = ColorScheme.getForTheme(theme);
		return cs.getColor(role, isPanelOnTop(), enabled, isMouseOver(mouse), isClicked(), isSelected());
	}

	/**
	 * Get color from theme
	 * 
	 * @param role color role
	 * @param mouse mouse position
	 * @param hover (explicitly set HOVER flag)
	 * @return RGB color
	 */
	public final RGB getColor(EColorRole role, Coord mouse, boolean hover) {
		ColorScheme cs = ColorScheme.getForTheme(theme);
		return cs.getColor(role, isPanelOnTop(), enabled, hover, isClicked(), isSelected());
	}

	/**
	 * Get color from theme
	 * 
	 * @param role color role
	 * @return RGB color
	 */
	public final RGB getColor(EColorRole role) {
		ColorScheme cs = ColorScheme.getForTheme(theme);
		return cs.getColor(role, isPanelOnTop(), enabled, false, false, isSelected());
	}

	/**
	 * Set widget tag
	 * 
	 * @param tag tag
	 * @return this
	 */
	public final Widget setTag(String tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * Get widget tag
	 * 
	 * @return tag
	 */
	public final String getTag() {
		return tag;
	}

	@Override
	public final Widget setMargins(int left, int top, int right, int bottom) {
		margins.setTo(left, top, right, bottom);
		return this;
	}


	@Override
	public final Widget setMarginsH(int left, int right) {
		margins.left = left;
		margins.right = right;
		return this;
	}


	@Override
	public final Widget setMarginsV(int top, int bottom) {
		margins.top = top;
		margins.bottom = bottom;
		return this;
	}

	@Override
	public final WidgetMargins getMargins() {
		return margins; //.mul(App.fs12());
	}

	@Override
	public Widget setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public final String getText() {
		return text;
	}

	@Override
	public final Widget setId(int id) {
		this.id = id;
		return this;
	}

	@Override
	public final int getId() {
		return id;
	}

	@Override
	public final Widget setEnabled(boolean state) {
		enabled = state;
		return this;
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	@Override
	public final Widget setVisible(boolean state) {
		visible = state;
		return this;
	}

	@Override
	public final boolean isVisible() {
		return visible;
	}

	@Override
	public final Widget setMinSize(Coord size) {
		minSize.setTo(size);
		return this;
	}

	@Override
	public final Widget setMinSize(int w, int h) {
		minSize.setTo(w, h);
		return this;
	}


	@Override
	public final Widget setMinWidth(int w) {
		minSize.x = w;
		return this;
	}


	@Override
	public final Widget setMinHeight(int h) {
		minSize.y = h;
		return this;
	}

	@Override
	public final Coord getMinSize() {
		return minSize;
	}

	@Override
	public final Coord getSize() {
		return rect.getSize();
	}

	@Override
	public final Widget setRect(Rect rect) {
		rect.setTo(rect);
		return this;
	}

	@Override
	public final Rect getRect() {
		return rect;
	}

	@Override
	public boolean isMouseOver(Coord mouse) {
		return rect.isInside(mouse);
	}

	@Override
	public abstract void render(Coord mouse);

	@Override
	public void onFocus() {}

	@Override
	public void onBlur() {}

	@Override
	public final boolean hasFocus() {
		return focused;
	}

	@Override
	public abstract Widget onMouseButton(Coord pos, int button, boolean down);

	@Override
	public abstract Widget onScroll(Coord pos, int scroll);

	@Override
	public abstract Widget onKey(int key, char chr, boolean down);

	@Override
	public boolean canAddChild() {
		return false;
	}

	@Override
	public void add(Widget child) {}

	@Override
	public void removeChild(Widget child) {}

	@Override
	public void removeAll() {}

	@Override
	public abstract void calcChildSizes();

	@Override
	public final GuiRoot getGuiRoot() {
		return guiRoot;
	}

	/**
	 * Get root panel
	 * 
	 * @return panel
	 */
	public final Panel getPanel() {
		return guiRoot.getPanel();
	}

	/**
	 * Get if the panel is on top.
	 * 
	 * @return is opn top.
	 */
	public final boolean isPanelOnTop() {
		return getGuiRoot().isPanelOnTop();
	}


	@Override
	public void setGuiRoot(GuiRoot guiContainer) {
		this.guiRoot = guiContainer;
	}

	/**
	 * Render tooltip for current widget
	 * 
	 * @param text text
	 * @param color color
	 */
	public void renderTooltip(String text, RGB color) {
		getGuiRoot().setTooltip(text, color);
	}

	/**
	 * Render white tooltip for current widget
	 * 
	 * @param text text
	 */
	public void renderTooltip(String text) {
		getGuiRoot().setTooltip(text, RGB.WHITE);
	}


	/**
	 * Handle static inputs (mouse coord given, in case it be needed)
	 * 
	 * @param pos mouse
	 */
	public void handleStaticInputs(Coord pos) {}
}
