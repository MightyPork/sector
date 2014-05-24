package net.sector.network.responses;


import java.util.ArrayList;
import java.util.Collections;


/**
 * List of scores for level
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ObjScoreList extends ArrayList<ObjScoreInfo> {

	/** Flag that last submitted score improved personal record. */
	public boolean scoreImproved = false;

	/** Last score - before submitting new one; -1 if this is the 1st time */
	public int lastScore = -1;

	@Override
	public String toString() {
		String s = "";
		s += "\n# SCORE LIST BEGIN #\n";

		s += "Score improved = " + scoreImproved + "\n";
		s += "Last score = " + lastScore + "\n";

		for (ObjScoreInfo x : this) {
			s += "\n";
			s += x.toString();
			s += "\n";
		}
		s += "\n# SCORE LIST END #\n";
		return s;
	}

	public int getScoreForUid(String uid) {
		for (ObjScoreInfo sc : this) {
			if (sc.uid.equals(uid)) return sc.score;
		}
		return -1;
	}

	public int getUserPosition(String uid) {

		Collections.sort(this);

		int pos = 1;
		for (ObjScoreInfo sc : this) {
			if (sc.uid.equals(uid)) return pos;
			pos++;
		}
		return -1;
	}

}
