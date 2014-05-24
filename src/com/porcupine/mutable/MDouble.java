package com.porcupine.mutable;


/**
 * Mutable double
 * 
 * @author MightyPork
 */
public class MDouble extends AbstractMutable<Double> {
	/**
	 * Mutable double
	 * 
	 * @param o value
	 */
	public MDouble(Double o) {
		super(o);
	}

	/**
	 * Imp.c.
	 */
	public MDouble() {}

	@Override
	protected Double getDefault() {
		return 0d;
	}
}
