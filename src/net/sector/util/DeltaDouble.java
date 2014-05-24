package net.sector.util;


import com.porcupine.math.Calc;


/**
 * Double which supports delta timing
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class DeltaDouble {
	/** target double */
	public double d = 0;
	/** last tick double */
	public double dlast = 0;

	/**
	 * new DeltaDouble
	 * 
	 * @param d value
	 */
	public DeltaDouble(double d) {
		this.dlast = this.d = d;
	}

	/**
	 * Get target double
	 * 
	 * @return number
	 */
	public double get() {
		return d;
	}

	/**
	 * Set to a value (both last and future)
	 * 
	 * @param d
	 */
	public void set(double d) {
		this.dlast = d;
		this.d = d;
	}

	/**
	 * Copy other
	 * 
	 * @param d
	 */
	public void set(DeltaDouble d) {
		this.dlast = d.dlast;
		this.d = d.d;
	}

	/**
	 * Store value for delta timing
	 */
	public void pushLast() {
		dlast = d;
	}

	/**
	 * Get value at delta time
	 * 
	 * @param dtime time
	 * @return interpolated value
	 */
	public double delta(double dtime) {
		return Calc.interpolate(dlast, d, dtime);
	}

	/**
	 * Add
	 * 
	 * @param num num
	 */
	public void add(double num) {
		d += num;
	}

	/**
	 * Subtract
	 * 
	 * @param num num
	 */
	public void sub(double num) {
		d -= num;
	}

	/**
	 * multiply
	 * 
	 * @param num num
	 */
	public void mul(double num) {
		d *= num;
	}

	/**
	 * Divide
	 * 
	 * @param num num
	 */
	public void div(double num) {
		d /= num;
	}

	/**
	 * Clamp to range
	 * 
	 * @param min min
	 * @param max max
	 */
	public void clamp(double min, double max) {
		d = Calc.clampd(d, min, max);
	}
}
