package net.sector.level.drivers;


import java.util.HashMap;
import java.util.Map;

import net.sector.level.drivers.functors.*;
import net.sector.util.Log;


/**
 * Entity registry for easier spawning
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class FunctorRegistry {

	private static Map<String, FunctorBase> functors = new HashMap<String, FunctorBase>();

	/**
	 * Initialize
	 */
	public static void init() {
		Log.f1("Initializing driver functor list...");

		functors.put("FLY_TO_POINT", new FnFlyToPoint());
		functors.put("FLY_TO_RECT", new FnFlyToRect());
		functors.put("FOLLOW_TARGET", new FnFollowTarget());
		functors.put("MAGNET", new FnMagnet());

		functors.put("AVOID", new FnAvoid());

		functors.put("SET_SPEED", new FnSetSpeed());
		functors.put("KEEP_DISTANCE", new FnKeepDistance());

		functors.put("ROTATE", new FnRotate());
		functors.put("TURN_TO", new FnTurnTo());

		functors.put("SHOOT", new FnShoot());
		functors.put("HEAL", new FnHeal());


		// checks
		functors.put("CAN_SHOOT", new FnCanShoot());
		functors.put("IS_IN_RECT", new FnIsInRect());
		functors.put("IS_LEADER", new FnIsLeader());
		functors.put("IS_TAIL", new FnIsTail());
		functors.put("CHECK_HEALTH", new FnCheckHealth());
	}

	/**
	 * Get functor for name
	 * 
	 * @param fn fn name
	 * @return the functor
	 */
	public static FunctorBase get(String fn) {
		return functors.get(fn.toUpperCase());
	}

}
