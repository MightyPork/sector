package net.sector.network.levels;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import net.sector.Constants;
import net.sector.level.loading.XmlUtil;
import net.sector.network.communication.EncryptionHelper;
import net.sector.network.communication.HttpHelper;
import net.sector.network.responses.ObjLevelInfo;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.jdom2.Element;


/**
 * Level descriptor and level data container
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NetLevelContainer {

	/** Flag that this level is valid - original. */
	public boolean isValid = false;

	/** Root node of the level */
	public Element levelRootNode = null;

	/** Level ID */
	public String lid;

	/** Level name */
	public String title;

	/** level download URL */
	public String url;

	/** level file MD5 checksum */
	public String checksum;

	/** time of level creation */
	public int created_time;

	/**
	 * Empty constructor
	 */
	public NetLevelContainer() {}

	/**
	 * Construct from ObjLevelInfo object
	 * 
	 * @param oli ObjLevelInfo to parse
	 */
	public NetLevelContainer(ObjLevelInfo oli) {
		this.lid = oli.lid;
		this.title = oli.title;
		this.url = oli.url;
		this.checksum = oli.checksum;
		this.created_time = oli.created_time;
	}


	/**
	 * Local filename
	 * 
	 * @return filename
	 */
	public File getLocalFile() {
		File dir = Utils.getGameSubfolder(Constants.DIR_LEVELS_SHARED);
		File file = new File(dir, lid + ".xml");
		return file;
	}

	/**
	 * Get if this level file is downloaded
	 * 
	 * @return is downloaded
	 */
	public boolean isDownloaded() {
		return getLocalFile().exists();
	}

	/**
	 * Try to download level file.
	 * 
	 * @throws MalformedURLException on error
	 * @throws IOException on error
	 */
	public void download() throws MalformedURLException, IOException {
		HttpHelper.downloadFile(url, getLocalFile());
	}

	/**
	 * Load level data and check if checksum matches.
	 * 
	 * @return true if file is original and valid - false if requires
	 *         re-download.
	 */
	public boolean loadCheckIfOriginal() {

		MessageDigest md;
		try {

			md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(getLocalFile());
			try {
				is = new DigestInputStream(is, md);
				levelRootNode = XmlUtil.getRootElement(is);
			} finally {
				is.close();
			}
			byte[] digest = md.digest();
			String md5 = EncryptionHelper.getHexHash(digest);
			return (isValid = md5.equals(checksum));

		} catch (Exception e) {
			Log.e("Error loading local file for level.", e);
		}

		isValid = false;
		return false;
	}

}
