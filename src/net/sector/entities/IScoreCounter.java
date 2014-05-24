package net.sector.entities;


/**
 * This be an interface for player's ship, which counts score.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public interface IScoreCounter {
	/**
	 * Add score to counter.
	 * 
	 * @param points points to add.
	 */
	public void addScore(int points);
}
