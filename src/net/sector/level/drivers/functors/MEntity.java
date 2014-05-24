package net.sector.level.drivers.functors;


import net.sector.entities.Entity;

import com.porcupine.mutable.AbstractMutable;


/**
 * Mutable entity reference
 * 
 * @author MightyPork
 */
public class MEntity extends AbstractMutable<Entity> {

	/**
	 * New mutable entity reference
	 * 
	 * @param o entity
	 */
	public MEntity(Entity o) {
		super(o);
	}

	/**
	 * Imp.c.
	 */
	public MEntity() {}

	@Override
	protected Entity getDefault() {
		return null;
	}

}
