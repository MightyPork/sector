package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.mutable.MInt;


/**
 * AI heal<br>
 * <br>
 * "add" hp points to add<br>
 */
public class FnHeal extends FunctorBase {
	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		if (drone.isEmpParalyzed()) return false;

		double add = AiObjParser.getDouble(args.get("add"), 0);

		double gap = AiObjParser.getDouble(args.get("gap"), 1);


		boolean percent = false;

		if (args.containsKey("percent")) percent = AiObjParser.getBoolean(args.get("percent"), false);
		if (args.containsKey("perc")) percent = AiObjParser.getBoolean(args.get("perc"), false);
		if (args.containsKey("relative")) percent = AiObjParser.getBoolean(args.get("relative"), false);



		MInt gap_cnt = null;
		if ((gap_cnt = (MInt) memory.get("gap")) == null) {
			memory.put("gap", gap_cnt = new MInt((int) (gap * Constants.SPEED_MUL)));
		}



		if (gap_cnt.o-- == 0) {
			drone.addHealth((add * drone.getHealMultiplier()) * (percent ? drone.getHealthMax() / 100D : 1));
			gap_cnt.o = (int) (gap * Constants.SPEED_MUL);
		}

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.TASK;
	}
}
