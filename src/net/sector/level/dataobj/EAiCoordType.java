package net.sector.level.dataobj;


/**
 * Special AI Coord types
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public enum EAiCoordType {
	/** Real coordinates */
	BASIC,
	/** Return player position */
	PLAYER_POS,
	/** Return player position */
	PLAYER_MOTION,
	/** Return drone motion */
	MOTION,
	/** alias for MOTION */
	MOVE_DIR,
	/** Return vector to player ship */
	PLAYER_DIR,
	/** Direction where the ship is turned to */
	ROTATE_DIR,
	/** Return drone position */
	POS,
	/**
	 * leader entity of a swarm formation (in case of snake the previous
	 * article)
	 */
	LEADER_POS,
	/** leader's motion vector */
	LEADER_DIR,
	/** Position of the target entity (if any) */
	TARGET_POS,
	/** Direction to the target entity (if any) */
	TARGET_DIR
}
