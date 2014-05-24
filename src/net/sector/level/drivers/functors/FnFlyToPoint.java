package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Range;


/**
 * AI fly to point...<br>
 * <br>
 * "target" where to fly<br>
 * "weight" eg. 1 strength of steering
 */
public class FnFlyToPoint extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		Coord target = AiObjParser.getCoord(args.get("target")).toCoord(drone);

		Range x = AiObjParser.getRange(args.get("x"));
		Range z = AiObjParser.getRange(args.get("z"));

		if (!args.containsKey("target")) {
			// use min-max
			if (memory.containsKey("tg")) {
				target = (Coord) memory.get("tg");
			} else {
				target = new Coord(x.randDouble(), 0, z.randDouble());
				memory.put("tg", target);
			}
		}

		double strength = AiObjParser.getDouble(args.get("weight"), 1) * speedMul(drone);

		double desiredSpeed = drone.getDesiredSpeed();

		Vec direction = drone.getPos().vecTo(target);

		drone.getMotion().add_ip(direction.norm(0.001 * strength));
		drone.getMotion().norm_ip(desiredSpeed);
		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
