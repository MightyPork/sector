package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.Constants;
import net.sector.entities.EntityNavigable;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;


/**
 * AI set speed (not the stable speed)<br>
 * <br>
 * "factor" 0.5 half of full speed, 2 twice faster<br>
 * incompatible with KeepDistance
 */
public class FnSetSpeed extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double factor = AiObjParser.getDouble(args.get("speed"), 1);

		EntityNavigable navent = (EntityNavigable) drone;

		if (navent.speedMul1 > factor) {
			navent.speedMul1 -= 0.05 * Constants.SPEED_MUL;
			if (navent.speedMul1 < factor) navent.speedMul1 = factor;
		} else if (navent.speedMul1 < factor) {
			navent.speedMul1 += 0.05 * Constants.SPEED_MUL;
			if (navent.speedMul1 > factor) navent.speedMul1 = factor;
		}

		//drone.getPos().add_ip(drone.getMotion().scale(-1+factor));
		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
