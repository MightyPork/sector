package com.porcupine.mutable;


/**
 * Mutable string
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class MString extends AbstractMutable<String> {
	/**
	 * Mutable string
	 * 
	 * @param o value
	 */
	public MString(String o) {
		super(o);
	}

	/**
	 * Imp.c.
	 */
	public MString() {}

	@Override
	protected String getDefault() {
		return "";
	}
}
