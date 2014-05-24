package net.sector.threads;


import static net.sector.threads.EThreadStatus.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import net.sector.Constants;
import net.sector.level.ELevel;
import net.sector.level.LevelContainer;
import net.sector.level.LevelRegistry;
import net.sector.level.loading.ResourceDirectoryLoader;
import net.sector.level.loading.XmlUtil;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.jdom2.Element;

import com.porcupine.util.FileUtils;


/**
 * Thread checking version of the latest release.
 * 
 * @author MightyPork
 */
public class ThreadLoadOfflineLevels extends Thread {

	/** Thread status */
	public static EThreadStatus status = UNSTARTED;

	@Override
	public void run() {
		status = WORKING;
		try {

			// internal
			ResourceDirectoryLoader rdl = new ResourceDirectoryLoader("res/levels");
			Map<String, Element> levels = XmlUtil.loadFromDirectoryWithManifest(rdl, "levels");

			for (Entry<String, Element> e : levels.entrySet()) {
				try {
					LevelRegistry.internalLevels_inPotentia.add(new LevelContainer(e.getKey(), e.getValue(), ELevel.INTERNAL));
				} catch (Exception err) {
					Log.e("Could not load local level from file " + e.getKey(), err);
				}
			}


			// local
			File folder = Utils.getGameSubfolder(Constants.DIR_LEVELS_LOCAL);
			folder.mkdirs();

			for (File file : FileUtils.listFolder(folder)) {
				if (file.isFile()) {
					String filename = file.getName();
					if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
						try {
							InputStream in = new FileInputStream(new File(folder, filename));
							LevelRegistry.localLevels_inPotentia.add(new LevelContainer(filename, XmlUtil.getRootElement(in), ELevel.LOCAL));
						} catch (Exception e) {
							Log.e("Could not load local level from file " + filename, e);
						}

					}
				}
			}

			status = SUCCESS;

		} catch (Exception e) {
			Log.w("THREAD: Could not load offline levels: " + e.getMessage());
			status = FAILURE;
		}

	}
}
