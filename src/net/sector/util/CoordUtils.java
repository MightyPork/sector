package net.sector.util;


import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc.Deg;
import com.porcupine.math.Polar;


/**
 * Functions for advanced coordinate manipulation.
 * 
 * @author MightyPork
 */
public class CoordUtils {

	/**
	 * Get absolute coordinate of a point in local axis system, rotated from the
	 * standard one by ANGLE around Y axis.
	 * 
	 * @param rotAngle angle of rotation
	 * @param theCoord the relative coordinate to transform
	 * @return absolute position of the transformed coordinate
	 */
	public static Coord coordToLocalSystem(double rotAngle, Coord theCoord) {
		Vec[] axes = getLocalAxes(rotAngle);
		return axes[0].scale(theCoord.x).add(axes[1].scale(theCoord.z)).add(0, theCoord.y, 0);
	}

	/**
	 * Get "Z" axis vector of Cartesian coordinate system rotated by angle in
	 * degrees
	 * 
	 * @param rotAngle angle in degrees
	 * @return Z axis unit vector
	 */
	public static Vec getLocalAxisZ(double rotAngle) {
		Polar p = new Polar(Deg.toRad(rotAngle + 90), 1);
		Coord c = p.toCoord();
		return new Vec(c.x, 0, c.y);
	}

	/**
	 * Get "X" axis vector of Cartesian coordinate system rotated by angle in
	 * degrees
	 * 
	 * @param rotAngle angle in degrees
	 * @return X axis unit vector
	 */
	public static Vec getLocalAxisX(double rotAngle) {
		Vec fw = getLocalAxisZ(rotAngle);
		Polar pr = Polar.fromCoord(fw.x, fw.z);
		pr.angle += Math.PI / 2;
		Coord c = pr.toCoord();
		return new Vec(-c.x, 0, -c.y);
	}

	/**
	 * Get "X" and "Z" axis vectors of Cartesian coordinate system rotated by
	 * angle in degrees
	 * 
	 * @param rotAngle angle in degrees
	 * @return local axes {X,Z} - {1,0,0} and {0,0,1} if angle is 0
	 */
	public static Vec[] getLocalAxes(double rotAngle) {
		return new Vec[] { getLocalAxisX(rotAngle), getLocalAxisZ(rotAngle) };
	}


}
