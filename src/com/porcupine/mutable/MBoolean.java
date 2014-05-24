package com.porcupine.mutable;


/**
 * Mutable boolean
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class MBoolean extends AbstractMutable<Boolean> {
	/**
	 * Mutable boolean
	 * 
	 * @param o value
	 */
	public MBoolean(Boolean o) {
		super(o);
	}

	/**
	 * Imp.c.
	 */
	public MBoolean() {}

	@Override
	protected Boolean getDefault() {
		return false;
	}
}
