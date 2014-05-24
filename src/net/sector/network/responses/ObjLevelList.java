package net.sector.network.responses;


import java.util.ArrayList;


/**
 * List of levels in database
 * 
 * @author MightyPork
 */
public class ObjLevelList extends ArrayList<ObjLevelInfo> {

	@Override
	public String toString() {
		String s = "";
		s += "\n# LEVEL LIST BEGIN #\n";
		for (ObjLevelInfo u : this) {
			s += "\n";
			s += u.toString();
			s += "\n";
		}
		s += "\n# LEVEL LIST END #\n";
		return s;
	}

}
