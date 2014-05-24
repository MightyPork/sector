package net.sector.util;


import com.porcupine.math.Calc;


/**
 * Delta double for angles in degrees
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class DeltaDoubleDeg extends DeltaDouble {
	/**
	 * Delta double for angle
	 * 
	 * @param d angle
	 */
	public DeltaDoubleDeg(double d) {
		super(d);
	}

	@Override
	public double delta(double dtime) {
		return Calc.interpolateDeg(dlast, d, dtime);
	}
}
