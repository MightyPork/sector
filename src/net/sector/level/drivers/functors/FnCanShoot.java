package net.sector.level.drivers.functors;


import java.util.Map;
import java.util.Set;

import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Vec;


/**
 * Check if can safely shoot in vec <br>
 * "vec" shoot vec<br>
 * "spare" spared entity types (PLAYER|NATURAL)<br>
 * *
 * 
 * @author MightyPork
 */
public class FnCanShoot extends FunctorBase {

	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {


		Vec dir = drone.getGunShotDir(AiObjParser.getInteger(args.get("gun"), 0));
		if (dir == null) return false;

		String spared = AiObjParser.getString(args.get("spare"), "");


		Set<Entity> ents = drone.getScene().getEntitiesInLineOfSight(drone.getPos().add(dir.norm(1)), dir, 0.5, 60);


		boolean ignf = AiObjParser.getBoolean(args.get("ignore_formation"), false);

		if (ents.size() == 0) return false;
		Entity closest = null;
		double closestDist = 100;

		// get closest entity
		for (Entity e : ents) {
			if (e == drone) continue;

			if (ignf && (e instanceof EntityNavigable)) {
				if (((EntityNavigable) e).formationContains((Entity) drone)) continue;
			}

			// ignore shots
			EEntity type = e.getType();
			if (type == EEntity.SHOT_BAD || type == EEntity.SHOT_GOOD) continue;

			double dist;
			if ((dist = e.getPos().distTo(drone.getPos())) < closestDist) {
				closest = e;
				closestDist = dist;
			}
		}

		if (closest == null) return false;

		EEntity type = closest.getType();
		if (!spared.contains(type.toString())) return true;

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.CHECK;
	}

}
