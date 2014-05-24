package net.sector.level.highscore;


import java.util.Collections;

import net.sector.CustomIonMarks;

import com.porcupine.ion.AbstractIonList;


/**
 * A highscore table
 * 
 * @author MightyPork
 */
public class HighscoreTable extends AbstractIonList<HighscoreEntry> {

	/**
	 * Add a highscore
	 * 
	 * @param score
	 * @param name
	 * @return the new score
	 */
	public HighscoreEntry addScore(int score, String name) {
		HighscoreEntry e;
		add(e = new HighscoreEntry(score, name));
		return e;
	}

	/**
	 * Sort entries
	 */
	public void sort() {
		Collections.sort(this);
	}

	@Override
	public byte ionMark() {
		return CustomIonMarks.HIGHSCORE_TABLE;
	}
}
