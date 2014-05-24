package com.porcupine.util;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sector.util.Log;


/**
 * Utilities for filesystem
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class FileUtils {

	private enum EnumOS {
		linux, solaris, windows, macos, unknown;
	}

	/**
	 * get working directory path ending with slash
	 * 
	 * @param dirname name of the directory, dot will be added automatically
	 * @return File path to the folder
	 */
	public static File getAppDir(String dirname) {
		String s = System.getProperty("user.home", ".");
		File file;

		switch (getOs()) {
			case linux:
			case solaris:
				file = new File(s, "." + dirname + '/');
				break;

			case windows:
				String s1 = System.getenv("APPDATA");

				if (s1 != null) {
					file = new File(s1, "." + dirname + '/');
				} else {
					file = new File(s, "." + dirname + '/');
				}

				break;

			case macos:
				file = new File(s, "Library/Application Support/" + dirname);
				break;

			default:
				file = new File(s, dirname + "/");
				break;
		}

		if (!file.exists() && !file.mkdirs()) {
			throw new RuntimeException((new StringBuilder()).append("The working directory could not be created: ").append(file).toString());
		} else {
			return file;
		}
	}

	private static EnumOS getOs() {
		String s = System.getProperty("os.name").toLowerCase();

		if (s.contains("win")) {
			return EnumOS.windows;
		}

		if (s.contains("mac")) {
			return EnumOS.macos;
		}

		if (s.contains("solaris")) {
			return EnumOS.solaris;
		}

		if (s.contains("sunos")) {
			return EnumOS.solaris;
		}

		if (s.contains("linux")) {
			return EnumOS.linux;
		}

		if (s.contains("unix")) {
			return EnumOS.linux;
		} else {
			return EnumOS.unknown;
		}
	}

	/**
	 * Get files in a folder (create folder if needed)
	 * 
	 * @param dir folder
	 * @param filter file filter
	 * @return list of files
	 */
	public static List<File> listFolder(File dir, FileFilter filter) {
		try {
			dir.mkdir();
		} catch (RuntimeException e) {
			Log.e("Error creating folder " + dir, e);
		}

		List<File> list = new ArrayList<File>();

		try {
			for (File f : dir.listFiles(filter)) {
				list.add(f);
			}
		} catch (Exception e) {
			Log.e("Error listing folder " + dir, e);
		}

		return list;
	}

	/**
	 * Get files in a folder (create folder if needed)
	 * 
	 * @param dir folder
	 * @return list of files
	 */
	public static List<File> listFolder(File dir) {
		return listFolder(dir, null);
	}

	/**
	 * Remove extension.
	 * 
	 * @param file file
	 * @return filename without extension
	 */
	public static String removeExtension(File file) {
		return removeExtension(file.getName());
	}

	/**
	 * Remove extension.
	 * 
	 * @param filename
	 * @return filename without extension
	 */
	public static String removeExtension(String filename) {
		String[] parts = filename.split("[.]");
		String out = "";
		for (int i = 0; i < parts.length - 1; i++) {
			out += parts[i];
		}

		return out;
	}

	/**
	 * Read entire file to a string.
	 * 
	 * @param file file
	 * @return file contents
	 * @throws IOException
	 */
	public static String fileToString(File file) throws IOException {
		String result = null;
		DataInputStream in = null;

		byte[] buffer = new byte[(int) file.length()];
		in = new DataInputStream(new FileInputStream(file));
		in.readFully(buffer);
		result = new String(buffer);
		in.close();

		return result;
	}

}
