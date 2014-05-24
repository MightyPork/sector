<?php

global $ERR;

# XML generator
class XmlGen{

	public static function sendHeaders(){
		header("content-type: application/xml; charset=UTF-8");
		header("Cache-Control: no-cache, must-revalidate");
	}

	public static function hashCode($hash){
		self::sendHeaders();
		$doc = new SimpleDocument("hash");
		$doc->appendChildToRoot( $doc->createElement("hash", null, $hash) );
		return $doc->saveXML();
	}

	public static function deleteMessage(){
		self::sendHeaders();
		$doc = new SimpleDocument("status");
		$doc->appendChildToRoot( $doc->createElement("msg", null, "Profile deleted.") );
		return $doc->saveXML();
	}

	public static function infoTable($info){
		self::sendHeaders();
		$doc = new SimpleDocument("info");
		foreach($info as $k => $v){
			$eElem = $doc->createElement("entry");
			$enode = $doc->appendChildToRoot($eElem);

			$enode->appendChild($doc->createElement("key", null, $k));
			$enode->appendChild($doc->createElement("value", null, $v));
		}
		return $doc->saveXML();
	}

	public static function userList($users){
		self::sendHeaders();
		$doc = new SimpleDocument("users");
		foreach($users as $u){
			$userElem = $doc->createElement("user");
			$unode = $doc->appendChildToRoot($userElem);

			$unode->appendChild($doc->createElement("name", null, $u[0]));
			$unode->appendChild($doc->createElement("reg_time", null, $u[1]));
			$unode->appendChild($doc->createElement("country", null, $u[2]));
		}
		return $doc->saveXML();
	}

	public static function levelList($data){ // `lid`,`title`,`filename`,`checksum`,`time`
		self::sendHeaders();
		$doc = new SimpleDocument("levels");
		foreach($data as $l){
			$level = $doc->createElement("level");
			
			$lnode = $doc->appendChildToRoot($level);
			$lnode->appendChild($doc->createElement("lid", null, $l[0]));
			$lnode->appendChild($doc->createElement("title", null, $l[1]));
			$lnode->appendChild($doc->createElement("url", null, $l[2]));
			$lnode->appendChild($doc->createElement("checksum", null, $l[3]));
			$lnode->appendChild($doc->createElement("created", null, $l[4]));
		}
		return $doc->saveXML();
	}

	public static function scoreList($lid, $data, $changeFlag=null, $lastRecord=null){ // username, uid, time, score
		self::sendHeaders();
		$attrs = array();
		if($changeFlag!=null) $attrs["score_improved"] = $changeFlag;
		if($changeFlag!=null) $attrs["last_score"] = $lastRecord;
		$doc = new SimpleDocument("scores", $attrs);
		foreach($data as $l){
			$level = $doc->createElement("score");
			
			$lnode = $doc->appendChildToRoot($level);
			$lnode->appendChild($doc->createElement("uid", null, $l[1]));
			$lnode->appendChild($doc->createElement("name", null, $l[0]));
			$lnode->appendChild($doc->createElement("time", null, $l[2]));
			$lnode->appendChild($doc->createElement("score", null, $l[3]));
		}
		return $doc->saveXML();
	}

	public static function levelAddedInfo($lid, $title, $url, $hash, $time){
		self::sendHeaders();
		$doc = new SimpleDocument("level");

		$doc->appendChildToRoot( $doc->createElement("lid", null, $lid) );
		$doc->appendChildToRoot( $doc->createElement("title", null, $title) );
		$doc->appendChildToRoot( $doc->createElement("url", null, $url) );
		$doc->appendChildToRoot( $doc->createElement("checksum", null, $hash) );
		$doc->appendChildToRoot( $doc->createElement("created", null, $time) );

		return $doc->saveXML();
	}

	public static function sessionInfo($uid, $token, $name=null, $email=null, $reg_time=null, $country=null){
		self::sendHeaders();
		$doc = new SimpleDocument("session");

		$doc->appendChildToRoot( $doc->createElement("uid", null, $uid) );
		$doc->appendChildToRoot( $doc->createElement("auth_token", null, $token) );

		$doc->appendChildToRoot( $doc->createElement("name", null, $name) );
		$doc->appendChildToRoot( $doc->createElement("email", null, $email) );
		$doc->appendChildToRoot( $doc->createElement("reg_time", null, $reg_time) );
		$doc->appendChildToRoot( $doc->createElement("country", null, $country) );

		return $doc->saveXML();
	}

	public static function error($error, $msg=""){
		self::sendHeaders();
		global $ERR;
		$e = $ERR[$error];
		$doc = new SimpleDocument("error");

		$doc->appendChildToRoot( $doc->createElement("code", null, $e[0]) );
		$doc->appendChildToRoot( $doc->createElement("message", null, $e[1]) );
		$doc->appendChildToRoot( $doc->createElement("cause", null, $msg) );

		return $doc->saveXML();
	}

	public static function error_exit($error, $msg=""){
		self::sendHeaders();
		global $ERR;
		$e = $ERR[$error];
		$doc = new SimpleDocument("error");

		$doc->appendChildToRoot( $doc->createElement("code", null, $e[0]) );
		$doc->appendChildToRoot( $doc->createElement("message", null, $e[1]) );
		$doc->appendChildToRoot( $doc->createElement("cause", null, $msg) );

		echo $doc->saveXML();
		exit();
	}



	public static function hacking_exit(){

		srand(round(time()/7200));

		if(rand(0,10)==0){
			$full_url_path = "http://".$_SERVER['HTTP_HOST'].preg_replace("#/[^/]*\.php$#simU", "/", $_SERVER["PHP_SELF"])."nothingHere.txt";

			$ch = curl_init();
			$timeout = 5;
			curl_setopt($ch, CURLOPT_URL, $full_url_path);
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
			curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
			echo curl_exec($ch);
			curl_close($ch);

/*		echo "<HTML>
<HEAD>
<TITLE>404 Not Found</TITLE>
</HEAD>
<BODY>
<H1>Not Found</H1>
The requested document was not found on this server.
<P>
<HR>
<ADDRESS>
Web Server at ".$_SERVER['SERVER_NAME']."
</ADDRESS>
</BODY>
</HTML>";*/

			exit();
		}

		self::sendHeaders();

		$doc = new SimpleDocument("error");

		$msgs = array(
			15 => "Internal server error.",
			"Database connection timed out.",
			"Service temporarily overloaded.",
			"Service temporarily not available.",
			"Operation not permitted.",
			"Session has expired.",
			"Brandwidth limit reached, aborting.",
			"Access denied.",
			"Unauthorised server access.",
			"Invalid command exception.",
			"Operation aborted.",
			"Bad database entry format.",
		);


		$e = rand(15,15+count($msgs)-1);

		$doc->appendChildToRoot( $doc->createElement("code", null, $e) );
		$doc->appendChildToRoot( $doc->createElement("message", null, $msgs[$e]) );
		$doc->appendChildToRoot( $doc->createElement("cause", null, "") );

		echo $doc->saveXML();
		exit();
	}
}