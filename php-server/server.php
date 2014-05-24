<?php

# Error codes and messages
$ERR = array(
	"NO_COMMAND"			=> array(0, "No command received."),
	"INVALID_COMMAND" 		=> array(1, "Invalid command requested."),
	"INTERNAL_ERROR" 		=> array(2, "Internal server error."),
	"INCOMPLETE_COMMAND" 	=> array(3, "Incomplete command received"),
	"REGISTRATION_FAILED" 	=> array(4, "Registration failed."),
	"LOGIN_FAILED" 			=> array(5, "Login failed."),
	"INVALID_TOKEN" 		=> array(6, "Authentication failed."),
	"FILE_NOT_FOUND" 		=> array(7, "File does not exist."),
	"BAD_FILE_FORMAT" 		=> array(8, "Bad file format."),
	"LEVEL_ALREADY_ADDED" 	=> array(9, "Level file is already registered to the Global Leaderboard."),
	"LEVEL_NAME_NOT_UNIQUE" => array(10, "Title already used by other level."),
	"NO_SUCH_LEVEL" 		=> array(11, "No such level exists."),
	"HACKING_DETECTED" 		=> array(12, "Access denied."),
);

define("CFG_FAKE_ERROR_FOR_HACKERS",true);



require_once("mysql.php");

require_once("class.SimpleDocument.php");
require_once("class.XmlGen.php");
require_once("class.DbUtil.php");
require_once("class.Util.php");



if(isset($_REQUEST["cmd"]) && ($_REQUEST["cmd"]=="ADD_LEVEL")){}else{ //||$_REQUEST["cmd"]=="HASH"

	// Check if the request came from a genuine Sector game
	$headers = apache_request_headers();

	if(
		$headers["User-Agent"] != "Sector/HttpHelper"
		or !isset($headers["X-SECTOR-VERSION"])
		or isset($headers["Cookie"])
		or isset($headers["Accept-Encoding"])
		or isset($headers["Accept-Language"])
		or isset($headers["Accept-Charset"])
		or isset($headers["Accept"])
	){
		if(CFG_FAKE_ERROR_FOR_HACKERS){
			XmlGen::hacking_exit();
		}else{
			XmlGen::error_exit("HACKING_DETECTED","Unauthorized server access.");
		}
	}

	$_REQUEST["VERSION"] = $headers["X-SECTOR-VERSION"]+0;
}





if(!isset($_REQUEST["cmd"])){
	XmlGen::error_exit("NO_COMMAND");
}




switch($_REQUEST["cmd"]){

	case "REGISTER":
		// check if name and password exist
		if(!isset($_REQUEST["name"]) || !isset($_REQUEST["password"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Missing 'name' or 'password'.");
		}

		// trim, null -> ""
		if(!isset($_REQUEST["email"])) $_REQUEST["email"] = "";
		if(!isset($_REQUEST["country"])) $_REQUEST["country"] = "";

		$name = trim($_REQUEST["name"]);
		$email = trim($_REQUEST["email"]);
		$password = trim($_REQUEST["password"]);
		$country = trim($_REQUEST["country"]);

		// check name and password length
		if(strlen($_REQUEST["name"])==0 || strlen($_REQUEST["password"])==0){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Zero-length 'name' or 'password'.");
		}
		
		// register and return uid+auth_token
		DbUtil::registerNewUser(
			$name,
			$password,
			$email,
			$country
		);

		exit();



	case "EDIT_PROFILE":
		// check if name and password exist
		if(!isset($_REQUEST["uid"]) || !isset($_REQUEST["auth_token"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Missing 'uid' or 'auth_token'.");
		}

		if(!DbUtil::isTokenValid($_REQUEST["uid"], $_REQUEST["auth_token"])){
			XmlGen::error_exit("INVALID_TOKEN");
		}

		// replace not set variables with nulls
		if(!isset($_REQUEST["email"])) $_REQUEST["email"] = "";
		if(!isset($_REQUEST["country"])) $_REQUEST["country"] = "";
		if(!isset($_REQUEST["name"])) $_REQUEST["name"] = null;
		if(!isset($_REQUEST["password"])) $_REQUEST["password"] = null;

		$uid = $_REQUEST["uid"];
		$name = Util::trimNullSafe($_REQUEST["name"]);
		$email = trim($_REQUEST["email"]);
		$password = Util::trimNullSafe($_REQUEST["password"]);
		$country = trim($_REQUEST["country"]);
		
		// register and return uid+auth_token
		DbUtil::modifyProfile(
			$uid,
			$name,
			$password,
			$email,
			$country
		);

		exit();



	case "DELETE_PROFILE":
		// check if name and password exist
		if(!isset($_REQUEST["uid"]) || !isset($_REQUEST["auth_token"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Missing 'uid' or 'auth_token'.");
		}

		if(!DbUtil::isTokenValid($_REQUEST["uid"], $_REQUEST["auth_token"])){
			XmlGen::error_exit("INVALID_TOKEN");
		}
		
		$uid = $_REQUEST["uid"];
		
		// register and return uid+auth_token
		DbUtil::deleteProfile(
			$uid
		);

		exit();


	case "ADD_LEVEL":
		// check if name and password exist
		if(!isset($_REQUEST["title"]) || !isset($_REQUEST["filename"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Missing 'title' or 'filename'.");
		}

		$title = trim($_REQUEST["title"]);
		$file = trim($_REQUEST["filename"]);

		DbUtil::addLevel(
			$title,
			$file
		);

		exit();



	case "GET_LEVELS":
		DbUtil::getLevels();
		exit();



	case "GET_SCORES":
		if(!isset($_REQUEST["lid"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Missing 'lid'.");
		}
		
		DbUtil::getLevelScores($_REQUEST["lid"]);

		exit();



	case "LOG_IN":
		// check if name and password exist
		if(!isset($_REQUEST["name"]) || !isset($_REQUEST["password"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Missing 'name' or 'password'.");
		}

		$name = trim($_REQUEST["name"]);
		$password = trim($_REQUEST["password"]);
		
		// log in and return uid+auth_token
		DbUtil::logIn(
			$name,
			$password
		);

		exit();



	case "GET_PROFILE_INFO":
		if(!isset($_REQUEST["uid"]) || !isset($_REQUEST["auth_token"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND", "Missing 'uid' or 'auth_token'.");
		}

		if(!DbUtil::isTokenValid($_REQUEST["uid"], $_REQUEST["auth_token"])){
			XmlGen::error_exit("INVALID_TOKEN");
		}
		
		// log in and return uid+auth_token
		DbUtil::refreshLogin(
			$_REQUEST["uid"],
			$_REQUEST["auth_token"]
		);

		exit();



	case "SUBMIT_SCORE":
		if(!isset($_REQUEST["uid"]) || !isset($_REQUEST["auth_token"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND", "Missing 'uid' or 'auth_token'.");
		}

		if(!isset($_REQUEST["lid"]) || !isset($_REQUEST["score"])){
			XmlGen::error_exit("INCOMPLETE_COMMAND", "Missing 'lid' or 'score'.");
		}

		if(!DbUtil::isTokenValid($_REQUEST["uid"], $_REQUEST["auth_token"])){
			XmlGen::error_exit("INVALID_TOKEN");
		}
		
		// log in and return uid+auth_token
		DbUtil::submitScore(
			$_REQUEST["uid"],
			$_REQUEST["lid"],
			$_REQUEST["score"]
		);

		exit();



// 	case "HASH":
// 		// check if name and password exist
// 		if(!isset($_REQUEST["password"]) || !isset($_REQUEST["name"])){
// 			XmlGen::error_exit("INCOMPLETE_COMMAND", "Missing 'name' or 'password'.");
// 		}
// 		
// 		echo XmlGen::hashCode(Util::calcSecureHash($_REQUEST["name"],$_REQUEST["password"]));
// 
// 		exit();



	case "GET_INFO":
		DbUtil::getInfo();
		exit();



	case "GET_USERS":
		DbUtil::getUsers();

		exit();



	default:
		XmlGen::error_exit("INVALID_COMMAND");
}
