package com.porcupine.coord;


import com.porcupine.math.Calc;


/**
 * Rectangle determined by two coordinates - min and max.
 * 
 * @author MightyPork
 */
public class Rect {

	/** Lowest coordinates xy */
	protected Coord min = new Coord();
	/** Highest coordinates xy */
	protected Coord max = new Coord();

	/**
	 * New Rect
	 */
	public Rect() {
		this(0, 0, 0, 0);
	}

	/**
	 * New Rect
	 * 
	 * @param x1 lower x
	 * @param y1 lower y
	 * @param x2 upper x
	 * @param y2 upper y
	 */
	public Rect(double x1, double y1, double x2, double y2) {
		setTo(x1, y1, x2, y2);
	}

	/**
	 * New rect of two coords
	 * 
	 * @param c1 coord 1
	 * @param c2 coord 2
	 */
	public Rect(Coord c1, Coord c2) {
		this(c1.x, c1.y, c2.x, c2.y);
	}

	/**
	 * New rect as a copy of other rect
	 * 
	 * @param r other rect
	 */
	public Rect(Rect r) {
		this(r.min.x, r.min.y, r.max.x, r.max.y);
	}

	/**
	 * Get a copy
	 * 
	 * @return copy
	 */
	public Rect copy() {
		return new Rect(this);
	}

	/**
	 * Offset in place (add)
	 * 
	 * @param move offset vector
	 * @return this
	 */
	public Rect add_ip(Vec move) {
		min.add_ip(move);
		max.add_ip(move);
		return this;
	}

	/**
	 * Get offset copy (add)
	 * 
	 * @param move offset vector
	 * @return offset copy
	 */
	public Rect add(Vec move) {
		return copy().add_ip(move);
	}

	/**
	 * Offset in place (subtract)
	 * 
	 * @param move offset vector
	 * @return this
	 */
	public Rect sub_ip(Vec move) {
		min.sub_ip(move);
		max.sub_ip(move);
		return this;
	}

	/**
	 * Get offset copy (subtract)
	 * 
	 * @param move offset vector
	 * @return offset copy
	 */
	public Rect sub(Vec move) {
		return copy().sub_ip(move);
	}

	/**
	 * @return lowest coordinates xy
	 */
	public Coord getMin() {
		return min;
	}

	/**
	 * @return highjest coordinates xy
	 */
	public Coord getMax() {
		return max;
	}

	/**
	 * Check if point is inside this rectangle
	 * 
	 * @param point point to test
	 * @return is inside
	 */
	public boolean isInside(Coord point) {
		return Calc.inRange(point.x, min.x, max.x) && Calc.inRange(point.y, min.y, max.y);
	}

	/**
	 * Get size (width, height) as (x,y)
	 * 
	 * @return coord of width,height
	 */
	public Coord getSize() {
		return new Coord(Math.abs(min.x - max.x), Math.abs(min.y - max.y));
	}

	/**
	 * Get rect center
	 * 
	 * @return center
	 */
	public Coord getCenter() {
		return min.midTo(max);
	}

	/**
	 * Get center of the lower edge.
	 * 
	 * @return center
	 */
	public Coord getCenterDown() {
		return new Coord((max.x + min.x) / 2, min.y);
	}

	/**
	 * Get center of the left edge.
	 * 
	 * @return center
	 */
	public Coord getCenterLeft() {
		return new Coord(min.x, (max.y + min.y) / 2);
	}

	/**
	 * Get center of the top edge.
	 * 
	 * @return center
	 */
	public Coord getCenterTop() {
		return new Coord((max.x + min.x) / 2, max.y);
	}

	/**
	 * Get center of the right edge.
	 * 
	 * @return center
	 */
	public Coord getCenterRight() {
		return new Coord(max.x, (max.y + min.y) / 2);
	}

	/**
	 * @return lower x
	 */
	public double x1() {
		return min.x;
	}

	/**
	 * @return lower y
	 */
	public double y1() {
		return min.y;
	}

	/**
	 * @return upper x
	 */
	public double x2() {
		return max.x;
	}

	/**
	 * @return upper y
	 */
	public double y2() {
		return max.y;
	}

	/**
	 * Set to other rect's coordinates
	 * 
	 * @param r other rect
	 */
	public void setTo(Rect r) {
		min.setTo(r.min);
		max.setTo(r.max);
	}


	/**
	 * Set to coordinates
	 * 
	 * @param x1 lower x
	 * @param y1 lower y
	 * @param x2 upper x
	 * @param y2 upper y
	 */
	public void setTo(double x1, double y1, double x2, double y2) {
		min.x = Calc.min(x1, x2);
		min.y = Calc.min(y1, y2);
		max.x = Calc.max(x1, x2);
		max.y = Calc.max(y1, y2);
	}

	@Override
	public String toString() {
		return "rect{ " + min + " - " + max + " }";
	}

	/**
	 * Add X and Y to all coordinates in place
	 * 
	 * @param x x to add
	 * @param y y to add
	 * @return this
	 */
	public Rect add_ip(double x, double y) {
		return add_ip(new Vec(x, y));
	}


	/**
	 * Subtract X and Y from all coordinates in place
	 * 
	 * @param x x to subtract
	 * @param y y to subtract
	 * @return this
	 */
	public Rect sub_ip(double x, double y) {
		return sub_ip(new Vec(x, y));
	}

	/**
	 * Add X and Y to all coordinates in a copy
	 * 
	 * @param x x to add
	 * @param y y to add
	 * @return copy changed
	 */
	public Rect add(double x, double y) {
		return add(new Vec(x, y));
	}

	/**
	 * Subtract X and Y from all coordinates in a copy
	 * 
	 * @param x x to subtract
	 * @param y y to subtract
	 * @return copy changed
	 */
	public Rect sub(double x, double y) {
		return sub(new Vec(x, y));
	}
}
