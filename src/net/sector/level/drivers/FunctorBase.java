package net.sector.level.drivers;


import java.util.Map;

import net.sector.entities.EntityNavigable;


/**
 * Task executing functor
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class FunctorBase {
	public double speedMul(INavigated drone) {
		return ((EntityNavigable) drone).getStableSpeedMultiplier();
	}

	/**
	 * Execute (called each entity update tick)
	 * 
	 * @param drone controlled ship
	 * @param memory task memory, storage of task-instance specific data
	 * @param args task arguments given by the preset
	 * @return if this task is of type TEST, here is returned the output value.
	 */
	public abstract boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args);

	/**
	 * Get functor type.
	 * 
	 * @return fn type
	 */
	public abstract EAiTaskType getFunctorType();
}
