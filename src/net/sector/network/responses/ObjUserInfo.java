package net.sector.network.responses;


/**
 * User info entry in user list
 * 
 * @author MightyPork
 */
public class ObjUserInfo {

	/** Username */
	public String uname;

	/** Registration timestamp */
	public int reg_time;

	/** Country code */
	public String country;

	@Override
	public String toString() {

		String s = "";
		s += "UserInfo\n";
		s += "| uname = " + uname + "\n";
		s += "| country = " + country + "\n";
		s += "| reg_time = " + reg_time;

		return s;
	}
}
