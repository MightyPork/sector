package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.Constants;
import net.sector.entities.EntityNavigable;
import net.sector.level.dataobj.AiCoord;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Coord;
import com.porcupine.math.Range;


/**
 * AI slow down<br>
 * <br>
 * "factor" 0.5 half of full speed, 2 twice faster
 */
public class FnKeepDistance extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		Coord from = AiObjParser.getCoord(args.get("from"), AiCoord.ZERO).toCoord(drone);

		Range zone = AiObjParser.getRange(args.get("dist"), new Range(5, 8));

		double dist = drone.getPos().distTo(from);

		EntityNavigable navent = (EntityNavigable) drone;

		if (dist > zone.getMax()) {
			navent.speedMul1 += 0.05 * Constants.SPEED_MUL;
			if (navent.speedMul1 > 1.5) navent.speedMul1 = 1.5;
		} else if (dist < zone.getMin()) {
			navent.speedMul1 -= 0.05 * Constants.SPEED_MUL;
			if (navent.speedMul1 < -1.5) navent.speedMul1 = -1.5;
		} else {
			if (navent.speedMul1 > 1) {
				navent.speedMul1 -= 0.3 * Constants.SPEED_MUL;
				if (navent.speedMul1 < 1) navent.speedMul1 = 1;
			}
			if (navent.speedMul1 < 1) {
				navent.speedMul1 += 0.3 * Constants.SPEED_MUL;
				if (navent.speedMul1 > 1) navent.speedMul1 = 1;
			}
		}

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
