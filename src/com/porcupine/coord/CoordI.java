package com.porcupine.coord;


/**
 * Simple integer coordinate class<br>
 * Unlike Coord, this is suitable for using in array indices etc.
 * 
 * @author MightyPork
 */
public class CoordI {

	/** X coordinate */
	public int x = 0;
	/** Y coordinate */
	public int y = 0;

	/**
	 * Integer 2D Coord
	 * 
	 * @param x x coord
	 * @param y y coord
	 */
	public CoordI(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create CoordI as copy of other
	 * 
	 * @param other coord to copy
	 */
	public CoordI(CoordI other) {
		setTo(other);
	}

	/**
	 * Get copy
	 * 
	 * @return copy
	 */
	public CoordI copy() {
		return new CoordI(x, y);
	}

	/**
	 * Set coords to
	 * 
	 * @param x x coord to set
	 * @param y y coord to set
	 */
	public void setTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set to coords from other coord
	 * 
	 * @param other source coord
	 */
	public void setTo(CoordI other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Convert to double Coord
	 * 
	 * @return coord with X and y from this CoordI
	 */
	public Coord toCoord() {
		return new Coord(x, y);
	}

	@Override
	public String toString() {
		return "[ " + x + " ; " + y + " ]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof CoordI) return ((CoordI) obj).x == x && ((CoordI) obj).y == y;
		return false;
	}

	@Override
	public int hashCode() {
		return x ^ y;
	}

	/**
	 * Add other coordI coordinates in place
	 * 
	 * @param move coordI to add
	 * @return this
	 */
	public CoordI add_ip(CoordI move) {
		x += move.x;
		y += move.y;
		return this;
	}

	/**
	 * Subtract other coordI coordinates in place
	 * 
	 * @param move coordI to subtract
	 * @return this
	 */
	public CoordI sub_ip(CoordI move) {
		x -= move.x;
		y -= move.y;
		return this;
	}

	/**
	 * Middle of this and other coordinate, rounded to CoordI - integers
	 * 
	 * @param other other coordI
	 * @return middle CoordI
	 */
	public CoordI midTo(CoordI other) {
		return new CoordI((x + other.x) / 2, (y + other.y) / 2);
	}

	/**
	 * Subtract x,y in a copy
	 * 
	 * @param x x to subtract
	 * @param y y to subtract
	 * @return copy subtracted
	 */
	public CoordI sub(int x, int y) {
		return copy().sub_ip(new CoordI(x, y));
	}

	/**
	 * Subtract other coordI coordinates in a copy
	 * 
	 * @param other coordI to subtract
	 * @return copy subtracted
	 */
	public CoordI sub(CoordI other) {
		return copy().sub_ip(other);
	}

	/**
	 * Add other coordI coordinates in a copy
	 * 
	 * @param other coordI to add
	 * @return copy modified
	 */
	public CoordI add(CoordI other) {
		return copy().add_ip(other);
	}


}
