package net.sector.gui.widgets;


import net.sector.gui.widgets.input.Scrollbar;


/**
 * Scrollable element
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public interface IScrollable {

	/**
	 * Get height of the entire content
	 * 
	 * @return content height
	 */
	public double getContentHeight();

	public void onScrollbarConnected(Scrollbar scrollbar);

	/**
	 * Get view height (height of the visible area)
	 * 
	 * @return view height
	 */
	public double getViewHeight();

	/**
	 * Hook called when scrollbar value changes
	 * 
	 * @param value scrollbar value 0-1
	 */
	public void onScrollbarChange(double value);
}
