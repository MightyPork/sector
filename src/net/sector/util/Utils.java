package net.sector.util;


import java.io.File;

import net.sector.Constants;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc.Rad;
import com.porcupine.math.Polar;
import com.porcupine.util.FileUtils;


/**
 * Sector's utils class
 * 
 * @author MightyPork
 */
public class Utils {


	private static File gameDir;


	/**
	 * Internal - check if point can be seen by camera
	 * 
	 * @param pos point position
	 * @param tolerance tolerance in degrees
	 * @return can be seen
	 */
	public static boolean canCoordBeSeen(Coord pos, double tolerance) {
		if (pos.z < 0) return false;
		Polar p = Polar.fromCoord(pos.x, pos.z + Constants.CAM_POS.z);

		return Rad.toDeg(Rad.diff(p.angle, Rad.a90)) <= Constants.CAM_ANGLE / 2D + tolerance;
	}

	/**
	 * Check if rendering of entity/particle located at this point can safely be
	 * skipped. That means, if this entity/particle is completely out of screen.
	 * 
	 * @param pos central point of the entity
	 * @return can be skipped
	 */
	public static boolean canSkipRendering(Coord pos) {
		return !(Utils.canCoordBeSeen(pos, 30) || (pos.z < 5 && Math.abs(pos.x) < 10 && pos.z > -4));
	}

	/**
	 * 2D angle of observer to point
	 * 
	 * @param observer point of observer
	 * @param point point of target
	 * @param lookVec look vector of observer
	 * @return angle
	 */
	public static double observerAngleToCoord(Coord observer, Coord point, Vec lookVec) {
		Vec dist = observer.vecTo(point);

		Polar point_p = Polar.fromCoord(dist.x, dist.z);
		Polar look_p = Polar.fromCoord(lookVec.x, lookVec.z);

		return Rad.toDeg(Rad.diff(point_p.angle, look_p.angle));
	}

	/**
	 * Get working directory ending with slash.
	 * 
	 * @return directory path file
	 */
	public static File getGameFolder() {
		if (gameDir == null) {
			gameDir = FileUtils.getAppDir(Constants.APP_DIR);
		}

		return gameDir;
	}

	/**
	 * Get subfolder of game dir
	 * 
	 * @param subfolderName sibfolder name
	 * @return the file object
	 */
	public static File getGameSubfolder(String subfolderName) {
		return new File(getGameFolder(), subfolderName);
	}

	public static Object fallback(Object... options) {
		for (Object o : options) {
			if (o != null) return o;
		}
		return null; // error
	}
}
