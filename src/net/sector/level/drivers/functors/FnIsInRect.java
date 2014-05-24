package net.sector.level.drivers.functors;


import java.util.Map;

import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.EAiTaskType;
import net.sector.level.drivers.FunctorBase;
import net.sector.level.drivers.INavigated;

import com.porcupine.coord.Coord;
import com.porcupine.math.Range;


/**
 * Check if drone is in rect
 * 
 * @author MightyPork
 */
public class FnIsInRect extends FunctorBase {

	@Override
	public boolean execute(INavigated drone, Map<String, Object> memory, Map<String, Object> args) {

		Range x_range = AiObjParser.getRange(args.get("x"));
		Range z_range = AiObjParser.getRange(args.get("z"));

		Coord min = new Coord(x_range.getMin(), 0, z_range.getMin());
		Coord max = new Coord(x_range.getMax(), 0, z_range.getMax());

		return drone.getPos().isInRect(min, max);
	}

	@Override
	public EAiTaskType getFunctorType() {
		return EAiTaskType.CHECK;
	}

}
