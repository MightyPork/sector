package net.sector.threads;


import static net.sector.threads.EThreadStatus.*;
import net.sector.level.SuperContext;
import net.sector.network.UserProfile;
import net.sector.network.communication.ServerError;
import net.sector.util.Log;


/**
 * Thread activating all user profiles in SuperContext.
 * 
 * @author MightyPork
 */
public class ThreadLoadAndActivateProfiles extends Thread {
	/** Thread status */
	public static EThreadStatus status = UNSTARTED;

	@Override
	public void run() {
		status = WORKING;
		Log.f2("THREAD: Loading file with user logins.");
		SuperContext.loadUserList();

		for (UserProfile p : SuperContext.userProfiles) {
			try {
				Log.f2("THREAD: Activating profile " + p.uname);
				p.logIn();
				Log.f2("THREAD: Profile " + p.uname + " activated.");
			} catch (ServerError e) {
				Log.w("THREAD: Could not activate profile " + p.uname + ": " + e.getMessage());
			}
		}

		if (SuperContext.selectedUser != null && !SuperContext.selectedUser.isActivated()) SuperContext.selectedUser = null;
		status = SUCCESS;
	}
}
