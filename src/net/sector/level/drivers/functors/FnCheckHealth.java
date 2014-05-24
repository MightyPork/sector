package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.math.Range;


/**
 * Check if drone is in rect
 * 
 * @author MightyPork
 */
public class FnCheckHealth extends FunctorBase {

	/*
	 * <check for="var">
	 * 	<fn str="CHECK_HEALTH" />
	 * 	(opt *)<range range="1-2" /> inclusive
	 * 	(opt *)<under num="15" /> or: lower, below, lt
	 * 	(opt *)<over num="15" /> or: above, gt
	 * 	(opt) <percent bool="true" /> or: relative, perc
	 * </check>
	 */

	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {
		Range zone = null;

		zone = AiObjParser.getRange(args.get("range"), zone);
		if (zone == null) zone = AiObjParser.getRange(args.get("zone"), zone);
		if (zone == null) zone = AiObjParser.getRange(args.get("in"), zone);

		Double ltAbs = null;
		Double gtAbs = null;

		ltAbs = AiObjParser.getDouble(args.get("below"), ltAbs);
		if (ltAbs == null) ltAbs = AiObjParser.getDouble(args.get("lower"), ltAbs);
		if (ltAbs == null) ltAbs = AiObjParser.getDouble(args.get("under"), ltAbs);
		if (ltAbs == null) ltAbs = AiObjParser.getDouble(args.get("lt"), ltAbs);


		gtAbs = AiObjParser.getDouble(args.get("over"), gtAbs);
		if (gtAbs == null) gtAbs = AiObjParser.getDouble(args.get("above"), gtAbs);
		if (gtAbs == null) gtAbs = AiObjParser.getDouble(args.get("higher"), gtAbs);
		if (gtAbs == null) gtAbs = AiObjParser.getDouble(args.get("gt"), gtAbs);


		boolean percent = false;

		if (args.containsKey("percent")) percent = AiObjParser.getBoolean(args.get("percent"), false);
		if (args.containsKey("perc")) percent = AiObjParser.getBoolean(args.get("perc"), false);
		if (args.containsKey("relative")) percent = AiObjParser.getBoolean(args.get("relative"), false);


		double health = drone.getHealth();
		double healthperc = drone.getHealthPercent();

		if (zone != null) {
			double cp = (percent ? healthperc : health);
			return cp >= zone.getMin() && cp <= zone.getMax();
		}

		if (ltAbs != null) {
			double cp = (percent ? healthperc : health);
			return cp < ltAbs;
		}

		if (gtAbs != null) {
			double cp = (percent ? healthperc : health);
			return cp > gtAbs;
		}

		return false;
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.CHECK;
	}

}
