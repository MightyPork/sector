package net.sector.level.drivers.functors;


import java.util.Map;
import java.util.Random;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;


/**
 * AI rotation add<br>
 * <br>
 * "angle" degrees to add
 */
public class FnRotate extends FunctorBase {

	private static Random rand = new Random();

	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double add = AiObjParser.getDouble(args.get("add"), 0);
		boolean randDir = AiObjParser.getBoolean(args.get("random_dir"), false);

		int dir = 1;

		if (memory.get("dir") == null) {
			if (randDir) dir = rand.nextBoolean() ? 1 : -1;
			memory.put("dir", dir);
		} else {
			dir = (Integer) memory.get("dir");
		}

		drone.getRotAngle().pushLast();
		drone.getRotAngle().add(dir * add * Constants.SPEED_MUL);

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
