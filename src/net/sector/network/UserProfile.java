package net.sector.network;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sector.CustomIonMarks;
import net.sector.level.SuperContext;
import net.sector.network.communication.EncryptionHelper;
import net.sector.network.communication.LeaderboardClient;
import net.sector.network.communication.ServerError;
import net.sector.network.responses.ObjScoreList;
import net.sector.network.responses.ObjSessionInfo;
import net.sector.util.Log;

import com.porcupine.ion.Ion;
import com.porcupine.ion.IonizableOptional;
import com.porcupine.util.StringUtils;


/**
 * User profile object
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class UserProfile implements IonizableOptional, Comparable<UserProfile> {

	/** [LOGIN] Profile UID */
	public String uid = "";

	/** [LOGIN] Profile login AUTH_TOKEN */
	public String auth_token = "";

	/** E-mail */
	public String email = "";

	/** Country code */
	public String country = "";

	/** Registration time */
	public int reg_time = -1;

	/** Flag that this profile is logged in = active */
	public boolean isLoggedIn = false;

	/**
	 * Flag that this profile info should be saved to disk and reloaded next
	 * startup
	 */
	public boolean isPermanent = true;

	/**
	 * Removed profile flag - indicates profile that should not be saved to disk
	 * nor shown in profile list.
	 */
	public boolean isRemoved = false;

	/** [NEEDS SAVE] User name */
	public String uname = null;

	/** [NEEDS SAVE] User password */
	public String password = null;


	/**
	 * Create profile of name and password. Not logged in.
	 * 
	 * @param uname username
	 * @param password password
	 */
	public UserProfile(String uname, String password) {
		this.uname = uname;
		this.password = password;
	}


	/**
	 * Build profile from name, password and just received session info object
	 * 
	 * @param name
	 * @param password
	 * @param sesinfo
	 */
	public UserProfile(String name, String password, ObjSessionInfo sesinfo) {
		loadFromSessionInfo(sesinfo);
		this.password = password;
		this.isLoggedIn = true;
	}


	/**
	 * Set name and password for login.
	 * 
	 * @param username
	 * @param password
	 */
	public void setNamePassword(String username, String password) {
		this.uname = username;
		this.password = password;
	}


	/**
	 * Set permanence flag (do not delete on exit)
	 * 
	 * @param state permanent
	 */
	public void setPermanent(boolean state) {
		isPermanent = state;
	}


	/**
	 * Make this user the selected one
	 */
	public void selectThisUser() {
		if (!isLoggedIn) Log.w("Cannot select user which isn't logged in.");
		SuperContext.selectedUser = this;
	}


	/**
	 * Get if this is the selected user.
	 * 
	 * @return is selected
	 */
	public boolean isSelected() {
		return SuperContext.selectedUser == this;
	}


	/**
	 * Get UID if activated, otherwise throw runtime exception.
	 * 
	 * @return UID (String)
	 */
	public String getUserId() {
		if (!isActivated()) throw new RuntimeException("Cannot get UID if not logged in.");
		return uid;
	}


	/**
	 * Get if profile login is authenticated by the server
	 * 
	 * @return is logged in
	 */
	public boolean isActivated() {
		return isLoggedIn == true;
	}

	/**
	 * Get if profile is permanent = should be saved to disk
	 * 
	 * @return is permanent
	 */
	public boolean isPermanent() {
		return isPermanent;
	}

	/**
	 * Get if this profile is marked for removal.
	 * 
	 * @return is removed.
	 */
	public boolean isRemoved() {
		return isRemoved;
	}


	/**
	 * Log into online highscore system and get a valid auth_token
	 * 
	 * @throws ServerError in case of error
	 */
	public void logIn() throws ServerError {
		loadFromSessionInfo(LeaderboardClient.logIn(uname, password));
		isLoggedIn = true;
	}


	/**
	 * Get new auth_token and update local copy of profile info.
	 * 
	 * @throws ServerError in case of error
	 */
	public void refreshProfileData() throws ServerError {
		if (!isActivated()) throw new RuntimeException("Cannot GET_PROFILE_INFO if not logged in.");
		loadFromSessionInfo(LeaderboardClient.getProfileInfo(uid, auth_token));
	}

	/**
	 * Submit score to server.
	 * 
	 * @param lid level ID
	 * @param score score to submit
	 * @return score list object with "score_improved" flag
	 * @throws ServerError in case of error
	 */
	public ObjScoreList submitScore(String lid, int score) throws ServerError {
		if (!isActivated()) throw new RuntimeException("Cannot submit score if not logged in.");
		return LeaderboardClient.submitScore(lid, score, uid, auth_token);
	}


	/**
	 * Edit the profile - if field isn't changed, insert old value.
	 * 
	 * @param uname new username
	 * @param password new password
	 * @param email new email
	 * @param country new country code
	 * @throws ServerError on error
	 */
	public void editProfile(String uname, String password, String email, String country) throws ServerError {
		loadFromSessionInfo(LeaderboardClient.editProfile(uid, auth_token, uname, password, email, country));
		this.password = password;
	}

	/**
	 * Initializer for Ion
	 */
	public UserProfile() {
		isPermanent = true;
		isLoggedIn = false;
	}

	/**
	 * Fill profile data fields with data from SessionInfo object
	 * 
	 * @param sesinfo session info object to parse
	 */
	public void loadFromSessionInfo(ObjSessionInfo sesinfo) {
		this.auth_token = sesinfo.auth_token;
		this.country = sesinfo.country;
		this.email = sesinfo.email;
		this.reg_time = sesinfo.reg_time;
		this.uid = sesinfo.uid;
		this.uname = sesinfo.uname;
	}


	@Override
	public void ionRead(InputStream in) throws IOException {
		uname = (String) Ion.readObject(in);
		uid = (String) Ion.readObject(in);
		String enc = (String) Ion.readObject(in);
		password = EncryptionHelper.simpleDecryptString(enc);
	}

	@Override
	public void ionWrite(OutputStream out) throws IOException {
		Ion.writeObject(out, uname);
		Ion.writeObject(out, uid);
		String enc = EncryptionHelper.simpleEncryptString(password);
		Ion.writeObject(out, enc);
	}

	@Override
	public byte ionMark() {
		return CustomIonMarks.USER_PROFILE;
	}


	@Override
	public String toString() {

		String s = "";
		s += "UserProfile\n";
		s += "| permanent = " + isPermanent + "\n";
		s += "| loggedIn = " + isLoggedIn + "\n";
		s += "|\n";
		s += "| uname = " + uname + "\n";
		s += "| password = " + StringUtils.passwordify(password, "*") + "\n";
		s += "|\n";
		s += "| uid = " + uid + "\n";
		s += "| auth_token = " + auth_token + "\n";
		s += "|\n";
		s += "| email = " + email + "\n";
		s += "| country = " + country + "\n";
		s += "| reg_time = " + reg_time + "\n";

		return s;
	}


	@Override
	public boolean ionShouldSave() {
		return isPermanent && !isRemoved;
	}


	@Override
	public int compareTo(UserProfile o) {
		return this.uname.compareToIgnoreCase(o.uname);
	}


	/**
	 * Mark as removed (don't show in level list)
	 * 
	 * @param state is removed
	 */
	public void setRemoved(boolean state) {
		this.isRemoved = state;
		if (isRemoved && isSelected()) SuperContext.selectedUser = null;
	}


	/**
	 * Delete this profile from online database
	 * 
	 * @throws ServerError on error
	 */
	public void deleteProfile() throws ServerError {
		LeaderboardClient.deleteProfile(uid, auth_token);
		setRemoved(true);
	}
}
