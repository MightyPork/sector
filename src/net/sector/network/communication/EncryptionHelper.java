package net.sector.network.communication;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sector.util.Log;


/**
 * Some algorithms used for the server-client communication and general purpose
 * hashing.
 * 
 * @author MightyPork
 */
public class EncryptionHelper {

	/**
	 * Calculate secure hash for password authentication
	 * 
	 * @param name user name (used as salt)
	 * @param password user password (hashed)
	 * @return the hash
	 */
	public static String calcSecureHash(String name, String password) {
		String hash = sha1(name + "S^1edT@R+ kN0w9e" + md5("troe(l01" + password + "d*G -? df lo%iUq") + "myL!tT1e(P)0nNY");
		return hash;
	}

	/**
	 * Take SHA-1 hash of a string
	 * 
	 * @param s strign to hash
	 * @return sha-1 hash of the string
	 */
	private static String sha1(String s) {
		String sha = hash(s, "SHA-1");
		while (sha.length() < 40) {
			sha = "0" + sha;
		}
		return sha;
	}

	/**
	 * Take MD5 hash of a string
	 * 
	 * @param s strign to hash
	 * @return md5 hash of the string
	 */
	private static String md5(String s) {
		String md5 = hash(s, "MD5");
		while (md5.length() < 32) {
			md5 = "0" + md5;
		}
		return md5;
	}

	/**
	 * Take hash of a string
	 * 
	 * @param s string to hash
	 * @param algorithm algorithm used (MessageDigest.getInstance)
	 * @return the hash
	 */
	private static String hash(String s, String algorithm) {
		String hashword = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance(algorithm);
			md5.update(s.getBytes());
			hashword = getHexHash(md5.digest());
		} catch (NoSuchAlgorithmException nsae) {
			Log.e("Error while hashing string.", nsae);
		}
		return hashword;
	}

	/**
	 * Get hash hex from bytes obtained from md5 digest
	 * 
	 * @param bytes byte array
	 * @return hash as hex string
	 */
	public static String getHexHash(byte[] bytes) {
		BigInteger hash = new BigInteger(1, bytes);
		return hash.toString(16);
	}

	private static final String replaceTable0 = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY^Z0123456789'\"<>.,-_~@!?;$|*/+%=#&{}()[]";
	private static final String replaceTable1 = "zw&QkD^RZe%3#b[@dv!6o>JB?=4U.I-a'lV8$7GcOM<9(Y*Wn|K,}_p+S]xt~E5j LTh{Fiq/gsC2A;mH0\"uNfXPry)1";

	/**
	 * Encrypt string using a simple substitution
	 * 
	 * @param plain string to encrypt
	 * @return encrypted
	 */
	public static String simpleEncryptString(String plain) {
		return transcode(plain, replaceTable0, replaceTable1);
	}

	/**
	 * Decrypt a string encrypted using "simpleEncryptString"
	 * 
	 * @param encrypted encrypted string
	 * @return plain
	 */
	public static String simpleDecryptString(String encrypted) {
		return transcode(encrypted, replaceTable1, replaceTable0);
	}

	public static String transcode(String input, String table1, String table2) {
		String out = "";

		for (int i = 0; i < input.length(); i++) {

			char charSearched = input.charAt(i);

			boolean found = false;

			for (int j = 0; j < table1.length(); j++) {

				char charTblA = table1.charAt(j);

				if (charTblA == charSearched) {
					out += table2.charAt(j);
					found = true;
					break;
				}

			}

			if (!found) out += charSearched;
		}

		return out;
	}
}
