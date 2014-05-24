package net.sector.network.responses;


/**
 * Level entry in level list
 * 
 * @author MightyPork
 */
public class ObjLevelInfo {

	/** Level ID */
	public String lid;

	/** Level name */
	public String title;

	/** level download URL */
	public String url;

	/** level file MD5 checksum */
	public String checksum;

	/** time of level creation */
	public int created_time;

	@Override
	public String toString() {

		String s = "";
		s += "LevelInfo\n";
		s += "| lid = " + lid + "\n";
		s += "| title = " + title + "\n";
		s += "| url = " + url + "\n";
		s += "| checksum = " + checksum + "\n";
		s += "| created_time = " + created_time;

		return s;
	}
}
