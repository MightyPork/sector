package net.sector.network.responses;


import java.util.ArrayList;


/**
 * List of users in global leaderboard system
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ObjUserList extends ArrayList<ObjUserInfo> {

	@Override
	public String toString() {
		String s = "";
		s += "\n# USER LIST BEGIN #\n";
		for (ObjUserInfo u : this) {
			s += "\n";
			s += u.toString();
			s += "\n";
		}
		s += "\n# USER LIST END #\n";
		return s;
	}

}
