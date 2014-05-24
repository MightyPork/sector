package net.sector.level.dataobj;


import net.sector.entities.Entity;
import net.sector.level.drivers.INavigated;
import net.sector.util.Log;

import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;
import com.porcupine.coord.Vec;
import com.porcupine.math.PolarDeg;


/**
 * Simple [X;Z] coordinate for AI
 * 
 * @author MightyPork
 */
public class AiCoord {
	/** Zero AI Coord */
	public static final AiCoord ZERO = new AiCoord();

	/** X coordinate */
	private double x;
	/** Z coordinate */
	private double z;

	/** Special type of this coord (runtime replacement) */
	private EAiCoordType specialType = EAiCoordType.BASIC;

	/**
	 * Create new zero AI coordinate
	 */
	public AiCoord() {
		x = z = 0;
	}

	/**
	 * Create AI Coord as copy of other
	 * 
	 * @param other copied
	 */
	public AiCoord(AiCoord other) {
		this.x = other.x;
		this.z = other.z;
		this.specialType = other.specialType;
	}

	/**
	 * Create AI Coord from double Coord
	 * 
	 * @param coord double coord with X,Z coordinates to use
	 */
	public AiCoord(Coord coord) {
		this.x = coord.x;
		this.z = coord.z;
		this.specialType = EAiCoordType.BASIC;
	}

	/**
	 * Create AI Coord from integer CoordI (x,y)->(x,z)
	 * 
	 * @param coordi source CoordI
	 */
	public AiCoord(CoordI coordi) {
		this.x = coordi.x;
		this.z = coordi.y;
		this.specialType = EAiCoordType.BASIC;
	}

	/**
	 * Create AI Coord from numbers
	 * 
	 * @param x x coordinate
	 * @param z z coordinate
	 * @param type special coord type
	 */
	public AiCoord(double x, double z, EAiCoordType type) {
		this.x = x;
		this.z = z;
		this.specialType = type;
	}

	/**
	 * Make copy
	 * 
	 * @return the copy
	 */
	public AiCoord copy() {
		return new AiCoord(this);
	}

//	/**
//	 * Convert to CoordI
//	 * 
//	 * @return CoordI representing this coord
//	 */
//	public CoordI toCoordI() {
//		return new CoordI((int) Math.round(x), (int) Math.round(z));
//	}

	/**
	 * Convert to double Coord
	 * 
	 * @param drone the controlled ship
	 * @return Coord representing this coord
	 */
	public Coord toCoord(INavigated drone) {
		if (specialType == EAiCoordType.BASIC) return new Coord(x, 0, z);
		if (drone == null) {
			Log.w("Trying to use magic Coord \"" + specialType + "\" but no drone specified.");
			return Coord.ZERO;
		}
		if (specialType == EAiCoordType.MOTION) return drone.getMotion();
		if (specialType == EAiCoordType.MOVE_DIR) return drone.getMotion();
		if (specialType == EAiCoordType.POS) return drone.getPos();

		if (specialType == EAiCoordType.PLAYER_MOTION) {
			return drone.getScene().getPlayerShip().getMotion();
		}

		if (specialType == EAiCoordType.PLAYER_POS) {
			return drone.getScene().getPlayerShip().getPos();
		}

		if (specialType == EAiCoordType.PLAYER_DIR) {
			return drone.getPos().vecTo(drone.getScene().getPlayerShip().getPos());
		}

		if (specialType == EAiCoordType.ROTATE_DIR) {
			double deg = drone.getRotAngle().get();
			PolarDeg pl = new PolarDeg(deg, 1);
			return pl.toCoordXZ();
		}

		if (specialType == EAiCoordType.LEADER_POS) {
			Entity leader = drone.getFormationLeader();
			if (leader == null) return drone.getPos();
			return leader.getPos();
		}

		if (specialType == EAiCoordType.LEADER_DIR) {
			Entity leader = drone.getFormationLeader();
			if (leader == null) return drone.getMotion();
			return leader.getMotion();
		}

		if (specialType == EAiCoordType.TARGET_POS) {
			Entity tg = drone.getTargetEntity();
			if (tg == null) return drone.getPos();
			return tg.getPos();
		}

		if (specialType == EAiCoordType.TARGET_DIR) {
			Entity tg = drone.getTargetEntity();
			if (tg == null) return drone.getMotion();
			return tg.getMotion();
		}

		Log.w("Invalid specialType in AiCoord");
		return Coord.ZERO;
	}

	/**
	 * Convert to Vector
	 * 
	 * @param drone drone
	 * @return vec
	 */
	public Vec toVec(INavigated drone) {
		return new Vec(toCoord(drone));
	}

	@Override
	public String toString() {
		if (specialType == EAiCoordType.BASIC) return "AiCoord[" + x + ";" + z + "]";
		return specialType.toString();
	}

}
