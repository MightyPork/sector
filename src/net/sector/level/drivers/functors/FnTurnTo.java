package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Vec;
import com.porcupine.math.Calc.Deg;
import com.porcupine.math.Calc.Rad;
import com.porcupine.math.Polar;


/**
 * AI rotate in direction of a vector<br>
 * <br>
 * "vec" the directional vector<br>
 */
public class FnTurnTo extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		Vec vec = new Vec(AiObjParser.getCoord(args.get("dir")).toCoord(drone));

		if (vec.x == 0 && vec.z == 0) return false;

		Polar p = Polar.fromCoord(vec.x, vec.z);
		double current = drone.getRotAngle().get();
		double goal = -90 + Rad.toDeg(p.angle);
		double delta = Deg.delta(current, goal);
		double add = 0;
		if (delta > 0) add = Math.min(7, Math.abs(delta));
		if (delta < 0) add = -Math.min(7, Math.abs(delta));

		drone.getRotAngle().add(add * Constants.SPEED_MUL);

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
