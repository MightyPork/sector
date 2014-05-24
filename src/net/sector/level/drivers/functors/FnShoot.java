package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.mutable.MInt;


/**
 * AI shoot<br>
 * <br>
 * "count" shots in group<br>
 * "gap_shot" pause between shots in group (sec)<br>
 * "gap_group" pause between groups (sec)<br>
 * "gun" gun index (0)
 */
public class FnShoot extends FunctorBase {

	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		// DIR

		//Vec dir = AiObjParser.getCoord(args.get("dir"), new AiCoord(drone.getMotion())).toVec(drone);

		int cInGroup = AiObjParser.getInteger(args.get("count"), 1);
		double cShotDelay = AiObjParser.getDouble(args.get("gap_shot"), 1);
		double cGroupDelay = AiObjParser.getDouble(args.get("gap_group"), 2);
		int cGunIndex = AiObjParser.getInteger(args.get("gun"), 0);
		int cAtOnce = AiObjParser.getInteger(args.get("bullets"), 1);

		if (cInGroup == 1) cShotDelay = 0;

		MInt shotsInGroup = null;
		if ((shotsInGroup = (MInt) memory.get("cnt")) == null) {
			memory.put("cnt", shotsInGroup = new MInt(cInGroup));
		}

		MInt groupDelay = null;
		if ((groupDelay = (MInt) memory.get("gap_g")) == null) {
			memory.put("gap_g", groupDelay = new MInt((int) (cGroupDelay * Constants.SPEED_MUL)));
		}

		MInt shotDelay = null;
		if ((shotDelay = (MInt) memory.get("gap_s")) == null) {
			memory.put("gap_s", shotDelay = new MInt((int) (cShotDelay * Constants.SPEED_MUL)));
		}

		if (groupDelay.o > 0) groupDelay.o--;

		// SHOOT
		if (groupDelay.o == 0) { //collider.pos.z > 0
			if (shotsInGroup.o == 0) {
				shotsInGroup.o = cInGroup;
				groupDelay.o = (int) (Constants.FPS_UPDATE * cGroupDelay);
				shotDelay.o = 0;
			} else {

				if (shotDelay.o > 0) shotDelay.o--;

				if (shotDelay.o == 0) {
					shotDelay.o = (int) (Constants.FPS_UPDATE * cShotDelay);

					for (; cAtOnce > 0; cAtOnce--)
						drone.shootOnce(cGunIndex);

					shotsInGroup.o--;
				}
			}
		}


		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
