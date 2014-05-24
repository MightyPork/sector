<?php
class DbUtil{
	public static function registerNewUser($name, $password, $email = "", $country = ""){
		$name = mysql_real_escape_string($name);
		$password = mysql_real_escape_string($password);
		$country = mysql_real_escape_string($country);
		$email = mysql_real_escape_string($email);
		
		$result = self::query("SELECT COUNT(*) FROM `SECTOR_USERS` WHERE `name` = '$name';");
		$rows=0;
		list($rows) = mysql_fetch_row($result);
		if($rows != 0){
			XmlGen::error_exit("REGISTRATION_FAILED","Entered name is already taken.");
		}

		$uid = "";
		while(true){
			$uid = "U-".Util::uniqueString(12);
			$result = self::query("SELECT COUNT(*) FROM `SECTOR_USERS` WHERE `uid` = '$uid' AND `removed` = '0';");
			$row = mysql_fetch_row($result);
			if($row[0] == 0) break;
		}

		$time = time();

		self::query("
			INSERT 
				INTO `SECTOR_USERS`
					(`uid`,`name`,`password`,`email`,`reg_time`,`country`)
				VALUES 
					('$uid','$name','$password','$email','$time','$country');
		");

		self::refreshLogin($uid);

		exit();
	}
/*
		$_REQUEST["uid"],
		$_REQUEST["name"],
		$_REQUEST["password"],
		$_REQUEST["email"],
		$_REQUEST["country"]
*/

	public static function deleteProfile($uid){

		$u = mysql_real_escape_string($uid);
		self::query("UPDATE `SECTOR_USERS` SET `removed`='1' WHERE `uid` = '$u' LIMIT 1;");
		self::query("UPDATE `SECTOR_SCORES` SET `removed`='1' WHERE `uid` = '$u';");

//		self::query("DELETE FROM `SECTOR_USERS` WHERE `uid` = '$u' LIMIT 1;");
//		self::query("DELETE FROM `SECTOR_SCORES` WHERE `uid` = '$u';");

		echo XmlGen::deleteMessage();

		exit();
	}



	public static function modifyProfile($uid,$name,$password,$email,$country){

		if($name == null && $password == null && $email == null && $country == null){
			XmlGen::error_exit("INCOMPLETE_COMMAND","Nothing to change.");
		}

		$n = mysql_real_escape_string($name);
		$u = mysql_real_escape_string($uid);
		$result = self::query("SELECT COUNT(*) FROM `SECTOR_USERS` WHERE `name` = '$n' AND `uid` != '$u';");
		$rows=0;
		list($rows) = mysql_fetch_row($result);
		if($rows != 0){
			XmlGen::error_exit("NAME_NOT_UNIQUE");
		}


		$sql = "";
		
		if($name != null){
			$sql .= ",`name` = '".mysql_real_escape_string($name)."'";
		}

		if($password != null){
			$sql .= ",`password` = '".mysql_real_escape_string($password)."'";
		}

		if($email == null) $email = "";
		
		$sql .= ",`email` = '".mysql_real_escape_string($email)."'";
		

		if($country == null) $country = "";

		$sql .= ",`country` = '".mysql_real_escape_string($country)."'";
		

		$sql = substr($sql,1);

		self::query("
			UPDATE `SECTOR_USERS`
				SET $sql
				WHERE `uid` = '$uid'
				LIMIT 1;
		");

		self::refreshLogin($uid);

		exit();		
	}

	public static function logIn($name, $passwordHash){
		$name = mysql_real_escape_string(trim($name));
		
		$result = self::query("SELECT `uid`,`password` FROM `SECTOR_USERS` WHERE `name` = '$name' AND `removed` = '0';");

		if(mysql_num_rows($result) == 0){
			XmlGen::error_exit("LOGIN_FAILED","Bad name or password.");
		}

		$row = mysql_fetch_assoc($result);
		$dbPwd = $row['password'];
		$uid = $row['uid'];
		
		// double hash with ugly salt!
		if( Util::calcSecureHash($name,$dbPwd) != $passwordHash){
			XmlGen::error_exit("LOGIN_FAILED","Bad name or password.");
		}

		self::refreshLogin($uid);

		exit();
	}

	public static function isTokenValid($uid, $token){
		$uid = mysql_real_escape_string($uid);
		$result = self::query("SELECT `auth_token` FROM `SECTOR_USERS` WHERE `uid` = '$uid' AND `removed` = '0';");

		if(mysql_num_rows($result) == 0){
			return false; // bad UID
		}

		$row = mysql_fetch_array($result);
		return $row[0] == $token;
	}
	
	public static function getInfo(){
		
		$result = self::query("SELECT * FROM `SECTOR_INFO`;");

		$entries = array();

		while($row = mysql_fetch_row($result)){
			$entries[$row[0]] = $row[1];
		}
		
		$version = $entries['VERSION_NUMBER']+0;
		
		if($_REQUEST["VERSION"]<=$version){
			// only publicly available releases are counted,
			// not prepared ones with higher version number
			
			// add to counter.
			$midnight = strtotime('midnight');
			
			$result = self::query("SELECT COUNT(*) FROM `SECTOR_COUNTER` WHERE `date` = '$midnight';");
			$row = mysql_fetch_array($result);
			if($row[0] == 0){
				self::query("INSERT INTO `SECTOR_COUNTER`(`date`,`visits`) VALUES ('$midnight','1');");
			}else{
				self::query("UPDATE `SECTOR_COUNTER` SET `visits`=`visits`+1 WHERE `date` = '$midnight' LIMIT 1;");
			}
		}

		echo XmlGen::infoTable($entries);

		exit();
	}

	public static function getUsers(){
		
		$result = self::query("SELECT `name`,`reg_time`,`country` FROM `SECTOR_USERS` WHERE `removed` = '0';");

		$entries = array();

		while($row = mysql_fetch_array($result)){
			$entries[] = $row;
		}

		echo XmlGen::userList($entries);

		exit();
	}


	public static function getLevels(){

		$result = self::query("SELECT `value` FROM `SECTOR_INFO` WHERE `key` = 'LEVELS_PATH';");
		$row = mysql_fetch_array($result);
		$path = $row[0];

		
		$result = self::query("SELECT `lid`,`title`,`filename`,`checksum`,`time` FROM `SECTOR_LEVELS` WHERE `removed` = '0';");

		$entries = array();

		while($row = mysql_fetch_array($result)){
			$row[2] = $path.$row[2];
			$entries[] = $row;
		}

		echo XmlGen::levelList($entries);

		exit();
	}

	public static function getLevelScores($lid, $changeFlag = null, $lastRecord = null){
		$lid = mysql_real_escape_string($lid);

		$result = self::query("SELECT COUNT(*) FROM `SECTOR_LEVELS` WHERE `lid` = '$lid' AND `removed` = '0';");
		$row = mysql_fetch_array($result);
		$cnt = $row[0];
		
		if($cnt==0) XmlGen::error_exit("NO_SUCH_LEVEL","No level with matching ID was found.");

		$result = self::query("
			SELECT 
				`SECTOR_USERS`.`name` AS `username`,
				`SECTOR_SCORES`.`uid`,
				`SECTOR_SCORES`.`time`,
				`SECTOR_SCORES`.`score`
			FROM `SECTOR_SCORES` JOIN `SECTOR_USERS`
			WHERE 
				(`SECTOR_SCORES`.`uid` = `SECTOR_USERS`.`uid`)
				AND	(`lid`='$lid')
				AND (`SECTOR_SCORES`.`removed` = '0')
			ORDER BY `score` DESC, `time` DESC;
		");
		
		// username, uid, time, score
		$entries = array();

		while($row = mysql_fetch_array($result)){
			$entries[] = $row;
		}

		echo XmlGen::scoreList($lid, $entries, $changeFlag, $lastRecord);

		exit();
	}

	public static function submitScore($uid, $lid, $score){
		$lid = mysql_real_escape_string($lid);
		$uid = mysql_real_escape_string($uid);
		$score = $score+0;

		$result = self::query("SELECT COUNT(*) FROM `SECTOR_LEVELS` WHERE `lid` = '$lid' AND `removed` = '0';");
		$row = mysql_fetch_array($result);
		$cnt = $row[0];

		if($cnt==0) XmlGen::error_exit("NO_SUCH_LEVEL","No level with matching ID was found.");

		
		$result = self::query("SELECT COUNT(*) FROM `SECTOR_SCORES` WHERE `lid` = '$lid' AND `uid` = '$uid';");
		$row = mysql_fetch_array($result);
		$cnt = $row[0];

		$time = time();
		
		$change = "false";
		$lastRecord = "-1";

		if($cnt==0){
			// INSERT
			self::query("INSERT INTO `SECTOR_SCORES`(`uid`,`lid`,`time`,`score`) VALUES ('$uid','$lid','$time','$score');");
			$change = "true";
		}else{
			$result = self::query("SELECT `score` FROM `SECTOR_SCORES` WHERE `lid` = '$lid' AND `uid` = '$uid';");
			$row = mysql_fetch_array($result);
			$scoreOld = $row[0];
			
			$lastRecord = "$scoreOld";
			if($scoreOld > $score){
				
			}else{			
				// UPDATE
				self::query("UPDATE `SECTOR_SCORES` SET `time`='$time', `score`='$score' WHERE `lid` = '$lid' AND `uid` = '$uid' LIMIT 1;");
				if($scoreOld != $score) $change = "true";
			}
		}
		
		self::getLevelScores($lid, $change, $lastRecord);

		exit();
	}

	public static function refreshLogin($uid){
		$token = Util::uniqueString(20);

		self::query("
			UPDATE `SECTOR_USERS`
				SET `auth_token` = '$token'
				WHERE `uid` = '$uid'
				LIMIT 1;
		");

		$result = self::query("SELECT `name`,`email`,`reg_time`,`country` FROM `SECTOR_USERS` WHERE `uid` = '$uid';");
		
		$row = mysql_fetch_assoc($result);

		$name = $row["name"];
		$email = $row["email"];
		$reg_time = $row["reg_time"];
		$country = $row["country"];

		echo XmlGen::sessionInfo($uid, $token, $name, $email, $reg_time, $country);
	}



	public static function addLevel($title, $filename){

		$result = self::query("SELECT COUNT(*) FROM `SECTOR_LEVELS` WHERE `filename` = '".mysql_real_escape_string($filename)."';");
		$row = mysql_fetch_array($result);
		$cnt = $row[0];
		
		if($cnt>0) XmlGen::error_exit("LEVEL_ALREADY_ADDED");

		$result = self::query("SELECT COUNT(*) FROM `SECTOR_LEVELS` WHERE `title` = '".mysql_real_escape_string($title)."';");
		$row = mysql_fetch_array($result);
		$cnt = $row[0];
		
		if($cnt>0) XmlGen::error_exit("LEVEL_NAME_NOT_UNIQUE");

		$result = self::query("SELECT `value` FROM `SECTOR_INFO` WHERE `key` = 'LEVELS_PATH_RELATIVE_TO_SERVER';");
		$row = mysql_fetch_array($result);
		$path = $row[0];

		$result = self::query("SELECT `value` FROM `SECTOR_INFO` WHERE `key` = 'LEVELS_PATH';");
		$row = mysql_fetch_array($result);
		$apath = $row[0];
		
		$fpath = $path.$filename;


		if(!file_exists($fpath)){
			XmlGen::error_exit("FILE_NOT_FOUND","Level file does not exist: ".$fpath);
		}

		if(substr($filename,strlen($filename)-4) != ".xml"){
			XmlGen::error_exit("BAD_FILE_FORMAT", "Level file must be XML: ".$fpath);
		}


		// generate a LID
		$lid = "";
		while(true){
			$lid = "L-".Util::uniqueString(9);
			$result = self::query("SELECT COUNT(*) FROM `SECTOR_LEVELS` WHERE `lid` = '$lid';");
			$row = mysql_fetch_row($result);
			if($row[0] == 0) break;
		}


		$hash = md5_file($fpath);
		
		$title = mysql_real_escape_string($title);
		$filename = mysql_real_escape_string($filename);
		
		$time = time();
		
		
		self::query("
			INSERT 
				INTO `SECTOR_LEVELS`
					(`lid`,`title`,`filename`,`checksum`,`time`)
				VALUES 
					('$lid','$title','$filename','$hash','$time');
		");

		echo XmlGen::levelAddedInfo($lid, $title, $apath.$filename, $hash, $time);

		exit();
	}

	public static function query($q){
		$res = mysql_query($q) or die(XmlGen::error("INTERNAL_ERROR", "DbError: ".mysql_error()));
		return $res;
	}
}
