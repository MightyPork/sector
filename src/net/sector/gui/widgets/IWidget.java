package net.sector.gui.widgets;


import com.porcupine.coord.Coord;
import com.porcupine.coord.Rect;


/**
 * GUI widget interface
 * 
 * @author MightyPork
 */
public interface IWidget {

	/**
	 * set widget required margins in layout.
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return this
	 */
	public Widget setMargins(int left, int top, int right, int bottom);

	/**
	 * set widget required margins in layout.
	 * 
	 * @param left
	 * @param right
	 * @return this
	 */
	public Widget setMarginsH(int left, int right);

	/**
	 * set widget required margins in layout.
	 * 
	 * @param top
	 * @param bottom
	 * @return this
	 */
	public Widget setMarginsV(int top, int bottom);

	/**
	 * Set text
	 * 
	 * @param text text to set
	 * @return this
	 */
	public Widget setText(String text);

	/**
	 * Get display text
	 * 
	 * @return text
	 */
	public String getText();

	/**
	 * Set widget id
	 * 
	 * @param id
	 * @return this
	 */
	public Widget setId(int id);

	/**
	 * get id
	 * 
	 * @return id
	 */
	public int getId();

	/**
	 * Enable widget (eg. button)
	 * 
	 * @param state
	 * @return this
	 */
	public Widget setEnabled(boolean state);

	/**
	 * @return is enabled
	 */
	public boolean isEnabled();

	/**
	 * Set widget visible
	 * 
	 * @param state
	 * @return this
	 */
	public Widget setVisible(boolean state);

	/**
	 * @return is visible
	 */
	public boolean isVisible();

	/**
	 * Set minimal size in layout
	 * 
	 * @param size size to set
	 * @return this
	 */
	public IWidget setMinSize(Coord size);

	/**
	 * Set minimal size in layout
	 * 
	 * @param w width
	 * @param h height
	 * @return this
	 */
	public IWidget setMinSize(int w, int h);

	/**
	 * Get minimal size (set by setMinSize)
	 * 
	 * @return size width,height
	 */
	public Coord getMinSize();

	/**
	 * Get current size (in fact a size of getRect())
	 * 
	 * @return the size width/height
	 */
	public Coord getSize();

	/**
	 * Set bounding rectangle.<br>
	 * The margin is excluded from this area.
	 * 
	 * @param rect
	 * @return this
	 */
	public Widget setRect(Rect rect);

	/**
	 * Get bounding rectangle
	 * 
	 * @return bounding rectangle
	 */
	public Rect getRect();

	/**
	 * Get minimal widget margin
	 * 
	 * @return margin (left,right,up,down)
	 */
	public WidgetMargins getMargins();

	/**
	 * Check if mouse is over.
	 * 
	 * @param mouse mouse coord
	 * @return is over
	 */
	public boolean isMouseOver(Coord mouse);

	/**
	 * Do render as 2D
	 * 
	 * @param mouse current mouse position, for hover effects
	 */
	public void render(Coord mouse);


	/**
	 * On focus gained
	 */
	public void onFocus();

	/**
	 * On focus lost
	 */
	public void onBlur();

	/**
	 * Check if this widget has focus
	 * 
	 * @return has focus
	 */
	public boolean hasFocus();

	/**
	 * Handle click (do effects etc.)
	 * 
	 * @param pos mouse position
	 * @param button mouse button
	 * @param down true if the button was pressed
	 * @return this if event consumed
	 */
	public Widget onMouseButton(Coord pos, int button, boolean down);

	/**
	 * Handle scroll event
	 * 
	 * @param pos mouse position
	 * @param scroll scroll (-1,0,1)
	 * @return this if event consumed
	 */
	public Widget onScroll(Coord pos, int scroll);

	/**
	 * Key pressed
	 * 
	 * @param key key index
	 * @param chr character typed
	 * @return this if event consumed
	 */
	public Widget onKey(int key, char chr, boolean down);

	/**
	 * Check if can add child
	 * 
	 * @return can add child to this widget?
	 */
	public boolean canAddChild();

	/**
	 * Add child
	 * 
	 * @param child
	 */
	public void add(Widget child);

	/**
	 * Remove child widget
	 * 
	 * @param child removed child
	 */
	public void removeChild(Widget child);

	/**
	 * remove all childs
	 */
	public void removeAll();

	/**
	 * calculate size based on contents and childs
	 */
	public void calcChildSizes();

	/**
	 * set min height
	 * 
	 * @param h
	 * @return this
	 */
	public IWidget setMinHeight(int h);

	/**
	 * set min width
	 * 
	 * @param w
	 * @return this
	 */
	public IWidget setMinWidth(int w);

	/**
	 * Get gui container
	 * 
	 * @return root
	 */
	public GuiRoot getGuiRoot();

	/**
	 * Set gui container
	 * 
	 * @param guiContainer
	 */
	public void setGuiRoot(GuiRoot guiContainer);

}
