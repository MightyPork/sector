package net.sector.network.responses;


/**
 * Score entry in score list
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ObjScoreInfo implements Comparable<ObjScoreInfo> {

	/** Username */
	public String uname;

	/** Time when score was taken */
	public int time;

	/** User's score */
	public int score;

	/** User ID */
	public String uid;

	@Override
	public String toString() {

		String s = "";
		s += "Score\n";
		s += "| uid = " + uid + "\n";
		s += "| uname = " + uname + "\n";
		s += "| time = " + time + "\n";
		s += "| score = " + score + "\n";

		return s;
	}

	@Override
	public int compareTo(ObjScoreInfo o) {
		if (this == o) return 0;
		return -new Integer(score).compareTo(o.score);
	}
}
