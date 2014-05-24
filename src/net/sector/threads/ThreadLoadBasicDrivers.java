package net.sector.threads;


import static net.sector.threads.EThreadStatus.*;
import net.sector.level.SuperContext;
import net.sector.level.loading.ResourceDirectoryLoader;
import net.sector.util.Log;


/**
 * Load default drivers
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ThreadLoadBasicDrivers extends Thread {

	/** Thread status */
	public static EThreadStatus status = UNSTARTED;

	@Override
	public void run() {
		status = WORKING;
		try {
			SuperContext.basicDrivers.loadDriversFromDirectory(new ResourceDirectoryLoader("res/drivers"));
			status = SUCCESS;
		} catch (Exception e) {
			status = FAILURE;
			Log.w("THREAD: Could not unpack levels: " + e.getMessage());
		}

	}
}
