package com.porcupine.util;


/**
 * General purpose string utilities
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class StringUtils {

	/**
	 * Get if string is in array
	 * 
	 * @param needle checked string
	 * @param case_sensitive case sensitive comparision
	 * @param haystack array of possible values
	 * @return is in array
	 */
	public static boolean isInArray(String needle, boolean case_sensitive, String... haystack) {
		if (case_sensitive) {
			for (String s : haystack) {
				if (needle.equals(s)) return true;
			}
			return false;
		} else {
			for (String s : haystack) {
				if (needle.equalsIgnoreCase(s)) return true;
			}
			return false;
		}
	}

	/**
	 * Repeat a string
	 * 
	 * @param repeated string
	 * @param count
	 * @return output
	 */
	public static String repeat(String repeated, int count) {
		String s = "";
		for (int i = 0; i < count; i++)
			s += repeated;
		return s;
	}

	/**
	 * convert string to a same-length sequence of # marks
	 * 
	 * @param password password
	 * @return encoded
	 */
	public static String passwordify(String password) {
		return passwordify(password, "#");
	}


	/**
	 * convert string to a same-length sequence of chars
	 * 
	 * @param password password
	 * @param replacing character used in output
	 * @return encoded
	 */
	public static String passwordify(String password, String replacing) {
		return repeat(replacing, password.length());
	}

	/**
	 * Get ordinal version of numbers (1 = 1st, 5 = 5th etc.)
	 * 
	 * @param number number
	 * @return ordinal, string
	 */
	public static String numberToOrdinal(int number) {
		if (number % 100 < 4 || number % 100 > 13) {
			if (number % 10 == 1) return number + "st";
			if (number % 10 == 2) return number + "nd";
			if (number % 10 == 3) return number + "rd";
		}
		return number + "th";
	}

	/**
	 * Format number with thousands separated by a dot.
	 * 
	 * @param number number
	 * @return string 12.004.225
	 */
	public static String formatInt(long number) {
		String num = number + "";
		String out = "";
		String dot = ".";
		int cnt = 1;
		for (int i = num.length() - 1; i >= 0; i--) {
			out = num.charAt(i) + out;
			if (cnt % 3 == 0 && i > 0) out = dot + out;
			cnt++;
		}

		return out;
	}

}
