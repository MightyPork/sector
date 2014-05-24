package com.porcupine.mutable;


/**
 * Mutable object
 * 
 * @author MightyPork
 * @param <T> type
 */
public abstract class AbstractMutable<T> {
	/** The wrapped value */
	public T o = getDefault();

	/**
	 * Implicint constructor
	 */
	public AbstractMutable() {}

	/**
	 * new mutable object
	 * 
	 * @param o value
	 */
	public AbstractMutable(T o) {
		this.o = o;
	}

	/**
	 * Get the wrapped value
	 * 
	 * @return value
	 */
	public T get() {
		return o;
	}

	/**
	 * Set value
	 * 
	 * @param o new value to set
	 */
	public void set(T o) {
		this.o = o;
	}

	/**
	 * Get default value
	 * 
	 * @return default value
	 */
	protected abstract T getDefault();
}
