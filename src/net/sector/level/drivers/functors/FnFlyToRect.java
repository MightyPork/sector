package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Range;
import com.porcupine.mutable.MInt;


/**
 * AI fly to rectangle...<br>
 * <br>
 * "min" min corner of the rect (-3;20)<br>
 * "max" max corner of the rect (3;25)<br>
 * "weight" eg. 0.001 strength of steering<br>
 */
public class FnFlyToRect extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double strength = AiObjParser.getDouble(args.get("weight"), 1) * speedMul(drone);

		//<x range="-3;3" />
		//<z range="20;25" />

		double desiredSpeed = drone.getDesiredSpeed();

		Range x_range = AiObjParser.getRange(args.get("x"));
		Range z_range = AiObjParser.getRange(args.get("z"));

		Coord min = new Coord(x_range.getMin(), 0, z_range.getMin());
		Coord max = new Coord(x_range.getMax(), 0, z_range.getMax());

		Coord pos = drone.getPos();

		Vec move = Vec.ZERO.copy();

		if (pos.x < min.x - 0.5) move.x = 1;
		if (pos.x > max.x + 0.5) move.x = -1;
		if (pos.z < min.z - 0.5) move.z = 1;
		if (pos.z > max.z + 0.5) move.z = -1;

		if (move.size() != 0) {
			drone.getMotion().norm_ip(desiredSpeed);
			drone.getMotion().add_ip(move.norm(strength * 0.0005));
			drone.getMotion().norm_ip(desiredSpeed);
		} else {
			Coord target = drone.getPos();

			MInt timer;

			boolean changeTarget = false;

			if (memory.containsKey("timer")) {
				timer = (MInt) memory.get("timer");
			} else {
				memory.put("timer", timer = new MInt(0));
				changeTarget = true;
			}

			timer.set(timer.get() + 1);

			if (timer.get() > Constants.FPS_UPDATE * 2) {
				timer.set(0);
				changeTarget = true;
			}

			for (int i = 0; i < 5; i++) {
				// use min-max
				if (memory.containsKey("tg") && !changeTarget) {
					target = (Coord) memory.get("tg");
				} else {
					target = new Coord(x_range.randDouble(), 0, z_range.randDouble());
					memory.put("tg", target);
				}

				if (drone.getPos().distTo(target) < 0.3) {
					changeTarget = true;
					timer.set(0);
					continue;
				}
				break;
			}

			Vec direction = drone.getPos().vecTo(target);

			drone.getMotion().add_ip(direction.norm(0.001 * strength));
			drone.getMotion().norm_ip(desiredSpeed);
		}

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
