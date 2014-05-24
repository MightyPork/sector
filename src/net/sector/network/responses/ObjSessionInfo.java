package net.sector.network.responses;


/**
 * Session info (response from login request and similar)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ObjSessionInfo {

	/** User ID */
	public String uid;

	/** AUTH_TOKEN */
	public String auth_token;

	/** Username */
	public String uname;

	/** E-mail */
	public String email;

	/** Country code */
	public String country;

	/** Registration timestamp */
	public int reg_time;

	@Override
	public String toString() {

		String s = "";
		s += "ObjSessionInfo\n";
		s += "| uid = " + uid + "\n";
		s += "| auth_token = " + auth_token + "\n";
		s += "| uname = " + uname + "\n";
		s += "| email = " + email + "\n";
		s += "| country = " + country + "\n";
		s += "| reg_time = " + reg_time;

		return s;
	}
}
