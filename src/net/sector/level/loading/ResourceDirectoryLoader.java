package net.sector.level.loading;


import java.io.InputStream;

import org.newdawn.slick.util.ResourceLoader;


/**
 * Directory access in resources
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ResourceDirectoryLoader implements DirectoryLoader {

	private String path;

	/**
	 * Create resource directory access
	 * 
	 * @param path the path
	 */
	public ResourceDirectoryLoader(String path) {
		this.path = path;
		if (path.endsWith("/")) this.path = path.substring(0, path.length() - 1);
	}

	@Override
	public boolean fileExists(String filename) {
		return ResourceLoader.resourceExists(path + "/" + filename);
	}

	@Override
	public InputStream openFile(String filename) {
		return ResourceLoader.getResourceAsStream(path + "/" + filename);
	}

	@Override
	public String toString() {
		return "Res('" + path + "')";
	}

}
