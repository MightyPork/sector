package net.sector.level.drivers.functors;


import java.util.Map;
import java.util.Set;

import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.entities.shots.EntityFireball;
import net.sector.entities.shots.EntityShotBase;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Rad;


/**
 * AI avoid collision<br>
 * <br>
 * "weight" eg. 1 strength of steering
 */
public class FnAvoid extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double desiredSpeed = drone.getDesiredSpeed();


		double strength = AiObjParser.getDouble(args.get("weight"), 1) * speedMul(drone) * 0.8;

		double dist = AiObjParser.getDouble(args.get("range"), 3);

		boolean sq = AiObjParser.getBoolean(args.get("square"), false);
		boolean ignf = AiObjParser.getBoolean(args.get("ignore_formation"), false);


		String avoid = AiObjParser.getString(args.get("avoid"), "NATURAL,SHOT_GOOD,SHOT_BAD,ENEMY,MINE");

		int fireballs = 0;
		Set<Entity> ents = drone.getScene().getEntitiesInRange(drone.getPos(), drone.getRadius() + dist);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == drone) continue;

				if (!avoid.contains(e.getType().toString())) continue;

				if (ignf && (e instanceof EntityNavigable)) {
					if (((EntityNavigable) e).formationContains((Entity) drone)) continue;
				}

				// ignore own shots
				if (e instanceof EntityShotBase && ((EntityShotBase) e).origin == drone) continue;

				if (e instanceof EntityFireball) {
					fireballs++;
					if (fireballs > 8) continue;
				}

				double move = 0;
				if (sq) {
					double entDist = e.getPos().distTo(drone.getPos()) - e.getRadius() - drone.getRadius();
					if (entDist < 0) entDist = 0;
					move = 0.001 / entDist;
					move = Calc.clampd(move, 0.0001, 0.3);
				} else {
					move = 0.001;
				}

				// redirect factor 0.001

				Vec vec1 = drone.getMotion();
				Vec vec2 = (Vec) e.getPos().vecTo(drone.getPos()).setY(0);

				double angle = Rad.toDeg(Math.acos(Math.abs(vec1.dot(vec2)) / (vec1.size() * vec2.size())));
				if (angle < 18) {
					boolean a = ((int) (System.currentTimeMillis() / 2500)) % 2 == 0;
					vec2.setTo((a ? -1 : 1) * vec2.z, 0, (a ? 1 : -1) * vec2.x);
				}

				drone.getMotion().add_ip((vec2).norm(strength * move));


				drone.getMotion().norm_ip(desiredSpeed);
			}
		}

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
