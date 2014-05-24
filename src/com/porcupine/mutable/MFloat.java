package com.porcupine.mutable;


/**
 * Mutable float
 * 
 * @author MightyPork
 */
public class MFloat extends AbstractMutable<Float> {
	/**
	 * Mutable float
	 * 
	 * @param o value
	 */
	public MFloat(Float o) {
		super(o);
	}


	/**
	 * Imp.c.
	 */
	public MFloat() {}

	@Override
	protected Float getDefault() {
		return 0f;
	}
}
