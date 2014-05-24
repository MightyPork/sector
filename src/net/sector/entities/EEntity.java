package net.sector.entities;


/**
 * Entity type
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public enum EEntity {
	/** Shot, bullet, rocket etc. by aliens */
	SHOT_BAD,
	/** Player shot */
	SHOT_GOOD,
	/** Player ship */
	PLAYER,
	/** Enemy ship / space craft */
	ENEMY,
	/** Natural = rocks etc. */
	NATURAL,
	/** Power up */
	BONUS,
	/** MINE */
	MINE,
	/** NONE (fake damage source) */
	NONE;
}
