package net.sector.level.loading;


import java.io.InputStream;


/**
 * Directory access interface
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public interface DirectoryLoader {
	/**
	 * Get if file exists in this directory
	 * 
	 * @param filename file name
	 * @return exists
	 */
	public boolean fileExists(String filename);

	/**
	 * Open file as InputStream
	 * 
	 * @param filename file name
	 * @return input stream
	 */
	public InputStream openFile(String filename);
}
