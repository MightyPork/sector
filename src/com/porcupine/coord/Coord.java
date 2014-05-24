package com.porcupine.coord;


import java.util.Random;

import com.porcupine.math.Calc;


/**
 * Coordinate class, object with three or two double coordinates.<br>
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Coord {
	/** Zero Coord */
	public static final Coord ZERO = new Coord(0, 0);
	/** RNG */
	protected static Random rand = new Random();
	/** X coordinate */
	public double x = 0;

	/** Y coordinate */
	public double y = 0;

	/** Z coordinate */
	public double z = 0;

	/**
	 * Create zero coord
	 */
	public Coord() {}

	/**
	 * Create 2D coord
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	public Coord(Number x, Number y) {
		setTo(x, y);
	}

	/**
	 * Create 3D coord
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public Coord(Number x, Number y, Number z) {
		setTo(x, y, z);
	}

	/**
	 * Create coord as a copy of another
	 * 
	 * @param copied copied coord
	 */
	public Coord(Coord copied) {
		this.x = copied.x;
		this.y = copied.y;
		this.z = copied.z;
	}

	/**
	 * Convert X and Y coordinates of this coord to a new CoordI.
	 * 
	 * @return the new CoordI
	 */
	public CoordI toCoordI() {
		return new CoordI((int) Math.round(x), (int) Math.round(y));
	}

	/**
	 * Generate random coord (gaussian)
	 * 
	 * @param max max distance from 0
	 * @return new coord
	 */
	public static Coord random(double max) {
		return new Coord(Calc.clampd(rand.nextGaussian() * max, -max * 2, max * 2), Calc.clampd(rand.nextGaussian() * max, -max * 2, max * 2),
				Calc.clampd(rand.nextGaussian() * max, -max * 2, max * 2));
	}

	/**
	 * Generate random coord (min-max)
	 * 
	 * @param min min offset
	 * @param max max offset
	 * @return new coord
	 */
	public static Coord random(double min, double max) {
		return new Coord((rand.nextBoolean() ? -1 : 1) * (min + rand.nextDouble() * (max - min)), (rand.nextBoolean() ? -1 : 1)
				* (min + rand.nextDouble() * (max - min)), (rand.nextBoolean() ? -1 : 1) * (min + rand.nextDouble() * (max - min)));
	}

	/**
	 * offset randomly in place
	 * 
	 * @param max max +- offset
	 * @return this
	 */
	public Coord random_offset_ip(double max) {
		return add(random(max));
	}

	/**
	 * offset randomly in place
	 * 
	 * @param min min offset
	 * @param max max offset
	 * @return this
	 */
	public Coord random_offset_ip(double min, double max) {
		add(random(min, max));
		return this;
	}

	/**
	 * offset randomly
	 * 
	 * @param max max +- offset
	 * @return offset coord
	 */
	public Coord random_offset(double max) {
		Coord r = random(1);
		Vec v = new Vec(r);
		v.norm_ip(0.00001 + rand.nextDouble() * max);
		return copy().add_ip(v);
	}

	/**
	 * offset randomly
	 * 
	 * @param min min offset
	 * @param max max offset
	 * @return offset coord
	 */
	public Coord random_offset(double min, double max) {
		return copy().add_ip(random(min, max));
	}

	/**
	 * @return X as double
	 */
	public double x() {
		return x;
	}

	/**
	 * @return Y as double
	 */
	public double y() {
		return y;
	}

	/**
	 * @return Z as double
	 */
	public double z() {
		return z;
	}

	/**
	 * @return X as double
	 */
	public double xd() {
		return x;
	}

	/**
	 * @return Y as double
	 */
	public double yd() {
		return y;
	}

	/**
	 * @return Z as double
	 */
	public double zd() {
		return z;
	}

	/**
	 * @return X as double
	 */
	public float xf() {
		return (float) x;
	}

	/**
	 * @return Y as double
	 */
	public float yf() {
		return (float) y;
	}

	/**
	 * @return Z as double
	 */
	public float zf() {
		return (float) z;
	}


	/**
	 * @return X as double
	 */
	public int xi() {
		return (int) Math.round(x);
	}

	/**
	 * @return Y as double
	 */
	public int yi() {
		return (int) Math.round(y);
	}

	/**
	 * @return Z as double
	 */
	public int zi() {
		return (int) Math.round(z);
	}

	/**
	 * Set 3D coordinates to
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return this
	 */
	public Coord setTo(Number x, Number y, Number z) {
		this.x = x.doubleValue();
		this.y = y.doubleValue();
		this.z = z.doubleValue();
		return this;
	}

	/**
	 * Set 2D coordinates to
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return this
	 */
	public Coord setTo(Number x, Number y) {
		setTo(x, y, 0);
		return this;
	}

	/**
	 * Set coordinates to match other coord
	 * 
	 * @param copied coord whose coordinates are used
	 * @return this
	 */
	public Coord setTo(Coord copied) {
		setTo(copied.x, copied.y, copied.z);
		return this;
	}

	/**
	 * Set X coordinate in place
	 * 
	 * @param x x coordinate
	 * @return this
	 */
	public Coord setX_ip(Number x) {
		this.x = x.doubleValue();
		return this;
	}

	/**
	 * Set Y coordinate in place
	 * 
	 * @param y y coordinate
	 * @return this
	 */
	public Coord setY_ip(Number y) {
		this.y = y.doubleValue();
		return this;
	}

	/**
	 * Set Z coordinate in place
	 * 
	 * @param z z coordinate
	 * @return this
	 */
	public Coord setZ_ip(Number z) {
		this.z = z.doubleValue();
		return this;
	}

	/**
	 * Set X coordinate in a copy
	 * 
	 * @param x x coordinate
	 * @return copy with set coordinate
	 */
	public Coord setX(Number x) {
		return copy().setX_ip(x);
	}

	/**
	 * Set Y coordinate in a copy
	 * 
	 * @param y y coordinate
	 * @return copy with set coordinate
	 */
	public Coord setY(Number y) {
		return copy().setY_ip(y);
	}

	/**
	 * Set Z coordinate in a copy
	 * 
	 * @param z z coordinate
	 * @return copy with set coordinate
	 */
	public Coord setZ(Number z) {
		return copy().setZ_ip(z);
	}



	/**
	 * Get a copy subtracted by 3D coordinate
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 * @return the offset copy
	 */
	public Coord sub(Number x, Number y, Number z) {
		return copy().sub_ip(x, y, z);
	}

	/**
	 * Get a copy subtracted by 2D coordinate
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @return the offset copy
	 */
	public Coord sub(Number x, Number y) {
		return copy().sub_ip(x, y);
	}

	/**
	 * Get a copy subtracted by vector
	 * 
	 * @param vec offset
	 * @return the offset copy
	 */
	public Coord sub(Coord vec) {
		return copy().sub_ip(vec);
	}

	/**
	 * Offset by 3D coordinate in place
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 * @return this
	 */
	public Coord sub_ip(Number x, Number y, Number z) {
		this.x -= x.doubleValue();
		this.y -= y.doubleValue();
		this.z -= z.doubleValue();
		return this;
	}

	/**
	 * Offset by 2D coordinate in place
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @return this
	 */
	public Coord sub_ip(Number x, Number y) {
		this.x -= x.doubleValue();
		this.y -= y.doubleValue();
		return this;
	}

	/**
	 * Offset by vector in place
	 * 
	 * @param vec offset
	 * @return this
	 */
	public Coord sub_ip(Coord vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}

	/**
	 * Get a copy offset by 3D coordinate
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 * @return the offset copy
	 */
	public Coord add(Number x, Number y, Number z) {
		return copy().add_ip(x, y, z);
	}

	/**
	 * Get a copy offset by 2D coordinate
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @return the offset copy
	 */
	public Coord add(Number x, Number y) {
		return copy().add_ip(x, y);
	}

	/**
	 * Get a copy offset by vector
	 * 
	 * @param vec offset
	 * @return the offset copy
	 */
	public Coord add(Coord vec) {
		return copy().add_ip(vec);
	}

	/**
	 * Offset by 3D coordinate in place
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @param z z offset
	 * @return this
	 */
	public Coord add_ip(Number x, Number y, Number z) {
		this.x += x.doubleValue();
		this.y += y.doubleValue();
		this.z += z.doubleValue();
		return this;
	}

	/**
	 * Offset by 2D coordinate in place
	 * 
	 * @param x x offset
	 * @param y y offset
	 * @return this
	 */
	public Coord add_ip(Number x, Number y) {
		this.x += x.doubleValue();
		this.y += y.doubleValue();
		return this;
	}

	/**
	 * Offset by vector in place
	 * 
	 * @param vec offset
	 * @return this
	 */
	public Coord add_ip(Coord vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		return this;
	}

	/**
	 * @return copy of this vector
	 */
	public Coord copy() {
		return new Coord(x, y, z);
	}


	/**
	 * Get distance to other point
	 * 
	 * @param point other point
	 * @return distance in units
	 */
	public double distTo(Coord point) {
		return Math.sqrt((point.x - x) * (point.x - x) + (point.y - y) * (point.y - y) + (point.z - z) * (point.z - z));
	}

	/**
	 * Create vector from this point to other point
	 * 
	 * @param point second point
	 * @return vector
	 */
	public Vec vecTo(Coord point) {
		return (Vec) (new Vec(point)).add(new Vec(this).neg());
	}

	/**
	 * Get distance to other point
	 * 
	 * @param a point a
	 * @param b point b
	 * @return distance in units
	 */
	public static double dist(Coord a, Coord b) {
		return a.distTo(b);
	}

	/**
	 * Get middle of line to other point
	 * 
	 * @param other other point
	 * @return middle
	 */
	public Coord midTo(Coord other) {
		return add(vecTo(other).scale(0.5));
	}

	private Coord last;
	private Vec offs;

	/**
	 * Store current value as LAST
	 */
	public void pushLast() {
		if (last == null) last = new Coord();
		if (offs == null) offs = new Vec();
		last.setTo(this);
	}

	/**
	 * Apply coordinates change for delta time
	 */
	public void update() {
		if (last == null) last = new Coord();
		if (offs == null) offs = new Vec();
		offs = last.vecTo(this);
	}

	/**
	 * Get coordinate at delta time since LAST
	 * 
	 * @param delta delta time 0-1
	 * @return delta pos
	 */
	public Coord getDelta(double delta) {
		if (last == null) last = new Coord();
		if (offs == null) offs = new Vec();
		return new Coord(this.add(offs.scale(delta)));
	}

	@Override
	public String toString() {
		return "[ " + x + " ; " + y + " ; " + z + " ]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!obj.getClass().isAssignableFrom(Vec.class)) return false;
		Vec other = (Vec) obj;
		return x == other.x && y == other.y && z == other.z;
	}

	@Override
	public int hashCode() {
		return Double.valueOf(x).hashCode() ^ Double.valueOf(y).hashCode() ^ Double.valueOf(z).hashCode();
	}

	/**
	 * Multiply by number
	 * 
	 * @param d number
	 * @return multiplied copy
	 */
	public Coord mul(double d) {
		return copy().mul_ip(d);
	}

	/**
	 * Multiply by number in place
	 * 
	 * @param d multiplier
	 * @return this
	 */
	public Coord mul_ip(double d) {
		x *= d;
		y *= d;
		z *= d;
		return this;
	}

	/**
	 * Multiply coords by number
	 * 
	 * @param xd x multiplier
	 * @param yd y multiplier
	 * @param zd z multiplier
	 * @return multiplied copy
	 */
	public Coord mul(double xd, double yd, double zd) {
		return copy().mul_ip(xd, yd, zd);
	}

	/**
	 * Multiply coords by number in place
	 * 
	 * @param xd x multiplier
	 * @param yd y multiplier
	 * @param zd z multiplier
	 * @return this
	 */
	public Coord mul_ip(double xd, double yd, double zd) {
		x *= xd;
		y *= yd;
		z *= zd;
		return this;
	}

	/**
	 * Divide by number in place
	 * 
	 * @param d number to divide by
	 * @return this
	 */
	public Coord div_ip(double d) {
		x /= d;
		y /= d;
		z /= d;
		return this;
	}

	/**
	 * Get copy divided by number
	 * 
	 * @param d number to divide by
	 * @return divided copy
	 */
	public Coord div(double d) {
		return copy().div_ip(d);
	}

	/**
	 * Round in place
	 * 
	 * @return this
	 */
	public Coord round_ip() {
		x = Math.round(x);
		y = Math.round(y);
		z = Math.round(z);
		return this;
	}


	/**
	 * Get a copy with rounded coords
	 * 
	 * @return rounded copy
	 */
	public Coord round() {
		return copy().round_ip();
	}

	/**
	 * Check if this rectangle in inside a rectangular zone
	 * 
	 * @param min min coord
	 * @param max max coord
	 * @return is inside
	 */
	public boolean isInRect(Coord min, Coord max) {
		return (x >= min.x && x <= max.x) && (y >= min.y && y <= max.y) && (z >= min.z && z <= max.z);
	}
}
