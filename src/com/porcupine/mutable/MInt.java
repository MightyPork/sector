package com.porcupine.mutable;


/**
 * Mutable integer
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class MInt extends AbstractMutable<Integer> {
	/**
	 * Mutable int
	 * 
	 * @param o value
	 */
	public MInt(Integer o) {
		super(o);
	}

	/**
	 * Imp.c.
	 */
	public MInt() {}

	@Override
	protected Integer getDefault() {
		return 0;
	}
}
