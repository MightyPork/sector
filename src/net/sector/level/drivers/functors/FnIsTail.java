package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;


/**
 * Check if drone is in rect
 * 
 * @author MightyPork
 */
public class FnIsTail extends FunctorBase {

	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		return drone.formationIsTail();
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.CHECK;
	}

}
