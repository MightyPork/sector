package net.sector.network.responses;


/**
 * Table of informations
 * 
 * @author MightyPork
 */
public class ObjInfoTable {

	/** Number of the newest version released */
	public int latest_version_num;

	/** Name of the latest version */
	public String latest_version;

	@Override
	public String toString() {

		String s = "";
		s += "InfoTable\n";
		s += "| latest version number = " + latest_version_num + "\n";
		s += "| latest version = " + latest_version;

		return s;
	}
}
