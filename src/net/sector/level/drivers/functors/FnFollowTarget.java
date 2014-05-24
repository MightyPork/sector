package net.sector.level.drivers.functors;


import java.util.Map;
import java.util.Set;

import net.sector.collision.Scene;
import net.sector.entities.Entity;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * AI lock on one enemy and kill it<br>
 * <br>
 * "target" type of target entity PLAYER, ENEMY, SHOT_GOOD, SHOT_BAD<br>
 * "range" scan range, eg. 60
 */
public class FnFollowTarget extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double desiredSpeed = drone.getDesiredSpeed();

		double range = AiObjParser.getDouble(args.get("range"), 60);

		String targetType = AiObjParser.getString(args.get("target"), "").toUpperCase();

		Scene scene = drone.getScene();
		Coord pos = drone.getPos();
		Vec motion = drone.getMotion();

		if (drone.getTargetEntity() == null || drone.getTargetEntity().isDead()) {
			Set<Entity> entities = scene.getEntitiesInRange(pos, range);
			double mini = 180;
			double mind = 1000;
			for (Entity entity : entities) {

				// if not an allowed target, go on. (allows ENEMY|NATURAL kind of notation)
				if (!targetType.contains(entity.getType().toString())) continue;

				if (entity == drone) continue;
				double i = 0;
				double d = entity.getPos().distTo(pos);
				i = Utils.observerAngleToCoord(pos, entity.getPos(), motion);
				if (i < 70 && i < mini && d < mind) {
					drone.setTargetEntity(entity);
					mini = i;
					mind = d;
				}
			}
		}

		// fly to
		if (drone.getTargetEntity() != null) {
			Entity target = drone.getTargetEntity();
			Vec direction = pos.vecTo(target.getPos());
			direction.norm_ip(desiredSpeed);
			double dist = target.getPos().distTo(pos) - target.getRadius();
			if (dist < 0) dist = 0;
			double spd = 0.2 / dist;
			if (spd < 0.05) spd = 0.05;
			motion.add_ip(direction.norm(spd));
			motion.norm_ip(desiredSpeed);
		}

		motion.norm_ip(desiredSpeed);

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
