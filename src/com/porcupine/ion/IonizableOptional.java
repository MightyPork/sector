package com.porcupine.ion;


/**
 * Optional ionizable
 * 
 * @author MightyPork
 */
public interface IonizableOptional extends Ionizable {
	/**
	 * Get if this ionizable should be saved to a list
	 * 
	 * @return should save
	 */
	public boolean ionShouldSave();
}
