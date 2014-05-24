package net.sector.level.drivers.functors;


import java.util.Map;
import java.util.Set;

import net.sector.entities.Entity;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * AI fly to closest of type, accelerate when getting close.<br>
 * <br>
 * "target" type of target entity PLAYER, ENEMY, SHOT_GOOD, SHOT_BAD<br>
 * "weight" eg. 0.4 strength of steering<br>
 * "range" scan range, eg. 15
 */
public class FnMagnet extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double desiredSpeed = drone.getDesiredSpeed();


		double range = AiObjParser.getDouble(args.get("range"), 15);
		double weight = AiObjParser.getDouble(args.get("weight"), 1) * 0.4 * speedMul(drone);
		String target = AiObjParser.getString(args.get("target"), "");


		Set<Entity> ents = drone.getScene().getEntitiesInRange(drone.getPos(), range);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == drone) continue;
				if (target.contains(e.getType().toString())) {
					double dist = e.getPos().distTo(drone.getPos()) - e.getRadius();
					if (dist < 0) dist = 0;
					double move = weight / dist;
					move = Calc.clampd(move, 0, weight);
					drone.getMotion().add_ip(((Vec) drone.getPos().vecTo(e.getPos()).setY(0)).norm(move));
					drone.getMotion().norm_ip(desiredSpeed);
				}
			}
		}

		drone.getMotion().norm_ip(desiredSpeed);
		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
