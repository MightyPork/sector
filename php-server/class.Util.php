<?php
class Util{
	// this needs name for salt.
	public static function calcSecureHash($name, $password){
		// !!! When changing this, it must also be changed in the client piece!
		return sha1( $name."S^1edT@R+ kN0w9e".md5( "troe(l01".$password."d*G -? df lo%iUq" )."myL!tT1e(P)0nNY" );
	}

	public static function uniqueString($len){
		$scale = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		$token = "";
		for($i=0;$i<$len;$i++){
			$token .= substr($scale, rand(0, strlen($scale)-1), 1);
		}
		return $token;
	}

	public static function remoteFileExists($url) {
		$curl = curl_init($url);
		curl_setopt($curl, CURLOPT_NOBODY, true);
		$result = curl_exec($curl);
		$ret = false;
		if ($result !== false) {
			$statusCode = curl_getinfo($curl, CURLINFO_HTTP_CODE);  

			if ($statusCode == 200) {
				$ret = true;   
			}
		}
		curl_close($curl);
		return $ret;
	}

	public static function trimNullSafe($string){
		if($string == null) return null;
		return trim($string);
	}
}