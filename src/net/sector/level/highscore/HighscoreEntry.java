package net.sector.level.highscore;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sector.CustomIonMarks;

import com.porcupine.ion.Ion;
import com.porcupine.ion.IonizableOptional;


/**
 * Hiscore entry (score - name)
 * 
 * @author MightyPork
 */
public class HighscoreEntry implements Comparable<HighscoreEntry>, IonizableOptional {
	/** Score */
	public int score = 0;

	/** Name */
	public String name = "";

	/** Uid, empty for guests. */
	public String uid = "";

	/**
	 * Flag that this entry is local. Used when guest plays global level, to
	 * differentiate entries of guests and of the active registered accounts.
	 */
	public boolean isLocal = true;

	/**
	 * Flag for guests highscore table, indicating that this entry should be
	 * highlighted
	 */
	public boolean justAdded = false;

	/**
	 * Entry of highscore table - implicit constructor
	 */
	public HighscoreEntry() {}

	/**
	 * Highscore entry
	 * 
	 * @param score score
	 * @param name name
	 */
	public HighscoreEntry(int score, String name) {
		this.score = score;
		this.name = name;
	}

	@Override
	public int compareTo(HighscoreEntry o) {
		if (this == o) return 0;
		return -new Integer(score).compareTo(o.score);
	}

	@Override
	public String toString() {
		return "( " + score + " - " + name + "; uid = \"" + uid + "\" )";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof HighscoreEntry)) return false;
		HighscoreEntry other = (HighscoreEntry) obj;
		return other.score == score && other.name.equals(name) && other.uid.equals(uid);
	}

	@Override
	public int hashCode() {
		return score ^ name.hashCode() ^ uid.hashCode();
	}

	@Override
	public void ionRead(InputStream in) throws IOException {
		score = (Integer) Ion.readObject(in);
		name = (String) Ion.readObject(in);
		uid = (String) Ion.readObject(in);
	}

	@Override
	public void ionWrite(OutputStream out) throws IOException {
		Ion.writeObject(out, Integer.valueOf(score));
		Ion.writeObject(out, name);
		Ion.writeObject(out, uid);

	}

	@Override
	public byte ionMark() {
		return CustomIonMarks.HIGHSCORE_ENTRY;
	}

	@Override
	public boolean ionShouldSave() {
		return isLocal;
	}
}
