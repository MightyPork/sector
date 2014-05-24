package net.sector.network.communication;


import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.sector.App;
import net.sector.Constants;
import net.sector.annotations.Internal;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.loading.XmlUtil;
import net.sector.network.UserProfile;
import net.sector.network.responses.*;
import net.sector.util.Log;

import org.jdom2.Element;


/**
 * main class for server-client communication
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class LeaderboardClient {

	/**
	 * Get list of users (informative)
	 * 
	 * @return list of users
	 * @throws ServerError on error
	 */
	public static ObjUserList getUserList() throws ServerError {
		Element root = getServerResponse("GET_USERS", null);

		ObjUserList list = new ObjUserList();

		for (Element u : root.getChildren("user")) {

			ObjUserInfo userInfo = new ObjUserInfo();

			userInfo.uname = u.getChildText("name");
			userInfo.country = u.getChildText("country");
			userInfo.reg_time = AiObjParser.getInteger(u.getChildText("reg_time"));

			list.add(userInfo);
		}

		return list;
	}

	/**
	 * Submit score for for level
	 * 
	 * @param lid level ID
	 * @param score score points
	 * @param uid UID
	 * @param auth_token AUTH_TOKEN
	 * @return list of scores in level, with a flag whether last score was
	 *         improved or not.
	 * @throws ServerError on error
	 */
	public static ObjScoreList submitScore(String lid, int score, String uid, String auth_token) throws ServerError {

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("lid", lid);
		args.put("score", score);
		args.put("uid", uid);
		args.put("auth_token", auth_token);

		Element root = getServerResponse("SUBMIT_SCORE", args);

		ObjScoreList list = new ObjScoreList();

		for (Element u : root.getChildren("score")) {

			ObjScoreInfo scoreInfo = new ObjScoreInfo();

			scoreInfo.uid = u.getChildText("uid");
			scoreInfo.time = AiObjParser.getInteger(u.getChildText("time"));
			scoreInfo.score = AiObjParser.getInteger(u.getChildText("score"));
			scoreInfo.uname = u.getChildText("name");

			list.add(scoreInfo);
		}

		list.scoreImproved = AiObjParser.getBoolean(root.getAttributeValue("score_improved"), false);
		list.lastScore = AiObjParser.getInteger(root.getAttributeValue("last_score"), -1);

		return list;
	}

	/**
	 * Get leaderboard for level
	 * 
	 * @param lid level ID
	 * @return list of scores in level, ordered from best to worst and from
	 *         newer to older.
	 * @throws ServerError on error
	 */
	public static ObjScoreList getLevelScores(String lid) throws ServerError {

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("lid", lid);

		Element root = getServerResponse("GET_SCORES", args);

		ObjScoreList list = new ObjScoreList();

		for (Element u : root.getChildren("score")) {

			ObjScoreInfo scoreInfo = new ObjScoreInfo();

			scoreInfo.uid = u.getChildText("uid");
			scoreInfo.time = AiObjParser.getInteger(u.getChildText("time"));
			scoreInfo.score = AiObjParser.getInteger(u.getChildText("score"));
			scoreInfo.uname = u.getChildText("name");

			list.add(scoreInfo);
		}

		return list;
	}

	/**
	 * Get list of users (informative)
	 * 
	 * @return list of users
	 * @throws ServerError on error
	 */
	public static ObjLevelList getLevelList() throws ServerError {
		Element root = getServerResponse("GET_LEVELS", null);

		ObjLevelList list = new ObjLevelList();

		for (Element u : root.getChildren("level")) {

			ObjLevelInfo levelInfo = new ObjLevelInfo();

			levelInfo.checksum = u.getChildText("checksum");
			levelInfo.created_time = AiObjParser.getInteger(u.getChildText("created"));
			levelInfo.lid = u.getChildText("lid");
			levelInfo.title = u.getChildText("title");
			levelInfo.url = u.getChildText("url");

			list.add(levelInfo);
		}

		return list;
	}

	/**
	 * Get list of users (informative)
	 * 
	 * @return list of users
	 * @throws ServerError on error
	 */
	public static ObjInfoTable getInfoTable() throws ServerError {
		Element root = getServerResponse("GET_INFO", null);

		ObjInfoTable table = new ObjInfoTable();

		for (Element u : root.getChildren("entry")) {
			String key = u.getChildText("key");
			String value = u.getChildText("value");

//			if(key.equals("LEVELS_PATH")) table.levels_url = value;
			if (key.equals("VERSION")) table.latest_version = value;
			if (key.equals("VERSION_NUMBER")) table.latest_version_num = AiObjParser.getInteger(value);
		}

		return table;
	}

	/**
	 * Send LOG_IN request.
	 * 
	 * @param username name
	 * @param password password
	 * @return session info
	 * @throws ServerError on error
	 */
	@Internal
	public static ObjSessionInfo logIn(String username, String password) throws ServerError {

		String hash = EncryptionHelper.calcSecureHash(username, password);

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("name", username);
		args.put("password", hash);

		Element root = getServerResponse("LOG_IN", args);

		return parseSessionInfo(root);

	}

	/**
	 * Create profile instance by logging in with name and password with a
	 * LOG_IN request
	 * 
	 * @param username username
	 * @param password password
	 * @return the created profile
	 * @throws ServerError on error
	 */
	public static UserProfile createProfileLogIn(String username, String password) throws ServerError {

		UserProfile p = new UserProfile(username, password);
		p.logIn();
		return p;
	}

	/**
	 * Create profile instance by registering to leaderboard with a REGISTER
	 * request.
	 * 
	 * @param username name
	 * @param password password
	 * @param email email address (optional)
	 * @param country country code (optional)
	 * @return the new profile, logged in
	 * @throws ServerError on error
	 */
	public static UserProfile createProfileRegister(String username, String password, String email, String country) throws ServerError {

		if (email == null) email = "";
		if (country == null) country = "";

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("name", username);
		args.put("password", password);
		args.put("email", email);
		args.put("country", country);

		// if this fails, error will be thrown		
		Element root = getServerResponse("REGISTER", args);

		ObjSessionInfo sesinfo = parseSessionInfo(root);

		return new UserProfile(username, password, sesinfo);

	}


	/**
	 * Send EDIT_PROFILE request.
	 * 
	 * @param uid UID
	 * @param auth_token AUTH_TOKEN from login
	 * @param username name
	 * @param password password
	 * @param email email address (optional)
	 * @param country country code (optional)
	 * @return session info
	 * @throws ServerError on error
	 */
	@Internal
	public static ObjSessionInfo editProfile(String uid, String auth_token, String username, String password, String email, String country)
			throws ServerError {

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("uid", uid);
		args.put("auth_token", auth_token);

		args.put("name", username);
		args.put("password", password);
		args.put("email", email);
		args.put("country", country);

		// if this fails, error will be thrown		
		Element root = getServerResponse("EDIT_PROFILE", args);

		ObjSessionInfo sesinfo = parseSessionInfo(root);

		return sesinfo;

	}



	/**
	 * Delete profile
	 * 
	 * @param uid UID
	 * @param auth_token AUTH_TOKEN from login
	 * @throws ServerError on error
	 */
	@Internal
	public static void deleteProfile(String uid, String auth_token) throws ServerError {
		Map<String, Object> args = new HashMap<String, Object>();

		args.put("uid", uid);
		args.put("auth_token", auth_token);

		getServerResponse("DELETE_PROFILE", args);
	}


	/**
	 * Send GET_PROFILE_INFO request.
	 * 
	 * @param uid uid
	 * @param auth_token auth_token
	 * @return session info
	 * @throws ServerError on error
	 */
	@Internal
	public static ObjSessionInfo getProfileInfo(String uid, String auth_token) throws ServerError {

		Map<String, Object> args = new HashMap<String, Object>();

		args.put("uid", uid);
		args.put("auth_token", auth_token);

		Element root = getServerResponse("GET_PROFILE_INFO", args);

		return parseSessionInfo(root);

	}


	/**
	 * Convert XML element to session info object
	 * 
	 * @param rootElement xml root node
	 * @return session info object
	 */
	private static ObjSessionInfo parseSessionInfo(Element rootElement) {

		ObjSessionInfo resp = new ObjSessionInfo();
		resp.auth_token = rootElement.getChildText("auth_token");
		resp.uid = rootElement.getChildText("uid");
		resp.country = rootElement.getChildText("country");
		resp.email = rootElement.getChildText("email");
		resp.uname = rootElement.getChildText("name");
		resp.reg_time = AiObjParser.getInteger(rootElement.getChildText("reg_time"));
		return resp;

	}


	/**
	 * Check XML server response, if it is a error response, extract and throw
	 * the error.
	 * 
	 * @param rootElement XML root node
	 * @throws ServerError
	 */
	private static void detectServerError(Element rootElement) throws ServerError {

		if (rootElement.getName().equals("error")) {

			int code = AiObjParser.getInteger(rootElement.getChildText("code"));
			String message = rootElement.getChildText("message");
			String cause = rootElement.getChildText("cause");
			
			System.out.println("Error " + code + "\n" + "msg = " + message + "\ncause = " + cause);
			
			throw new ServerError(code, message, cause);
		}
	}

	/**
	 * Contact server and get response as a XML root node.
	 * 
	 * @param cmd command for server
	 * @param argMap arguments key->value
	 * @return the root node of response
	 * @throws ServerError in case of error returned from server
	 */
	private static Element getServerResponse(String cmd, Map<String, Object> argMap) throws ServerError {

		try {
			if (argMap == null) argMap = new HashMap<String, Object>();
			argMap.put("cmd", cmd);

			Element e = XmlUtil.getRootElement(HttpHelper.requestPost(Constants.SERVER_URL, argMap));
			detectServerError(e);
			return e;

		} catch (ServerError se) {
			throw se;
		} catch (Exception e) {
			if (e instanceof UnknownHostException) {
				App.offlineMode = true;
				Log.i("Server not found, entering OFFLINE MODE!");
			}

			//@formatter:off
			throw new ServerError(
					100, 
					"Invalid server response, not a valid XML document.", 
					""
			);
			//@formatter:on

		}
	}
}
