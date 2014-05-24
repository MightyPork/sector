package net.sector.gui.widgets;


/**
 * Margins container class
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class WidgetMargins {
	/** left margin */
	public int left;
	/** top margin */
	public int top;
	/** right margin */
	public int right;
	/** bottom margin */
	public int bottom;

	/**
	 * new set of margins
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public WidgetMargins(int left, int top, int right, int bottom) {
		setTo(left, top, right, bottom);
	}

	/**
	 * new set of margins
	 * 
	 * @param other
	 */
	public WidgetMargins(WidgetMargins other) {
		setTo(other);
	}

	/**
	 * set margins to....
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setTo(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	/**
	 * set margins to....
	 * 
	 * @param other other margins obj
	 */
	public void setTo(WidgetMargins other) {
		this.left = other.left;
		this.top = other.top;
		this.right = other.right;
		this.bottom = other.bottom;
	}

	/**
	 * get copy multiplied by some number.
	 * 
	 * @param mul miltiplier
	 * @return copy multiplied
	 */
	public WidgetMargins mul(int mul) {
		return new WidgetMargins(left * mul, top * mul, right * mul, bottom * mul);
	}

	public WidgetMargins copy() {
		return new WidgetMargins(this);
	}
}
