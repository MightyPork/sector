package net.sector.network;


import java.io.InputStream;
import java.io.OutputStream;

import net.sector.CustomIonMarks;
import net.sector.level.SuperContext;
import net.sector.util.Log;

import com.porcupine.ion.AbstractIonList;
import com.porcupine.ion.Ion;


/**
 * List of profile entries
 * 
 * @author MightyPork
 */
public class ProfileList extends AbstractIonList<UserProfile> {

	@Override
	public byte ionMark() {
		return CustomIonMarks.USER_PROFILE_LIST;
	}

	@Override
	public String toString() {
		String s = "";
		s += "\n# PROFILE ENTRY LIST BEGIN #\n";
		for (UserProfile u : this) {
			s += "\n";
			s += u.toString();
			s += "\n";
		}
		s += "# PROFILE ENTRY LIST END #\n";
		return s;
	}

	@Override
	public void ionWriteCustomData(OutputStream out) {
		String selectedUid = "";
		if (SuperContext.selectedUser != null) {
			selectedUid = SuperContext.selectedUser.uid;
		}

		Ion.writeObject(out, selectedUid);
	}

	@Override
	public void ionReadCustomData(InputStream in) {
		String selectedUid = (String) Ion.readObject(in);
		if (selectedUid.length() == 0) {
			SuperContext.selectedUser = null;
		} else {
			for (UserProfile user : this) {
				if (user.uid.equals(selectedUid)) {
					Log.f3("USERS: Selected user: " + user.uid + ", " + user.uname);

					SuperContext.selectedUser = user;
					break;
				}
			}
			if (SuperContext.selectedUser == null) Log.w("No user with UID " + selectedUid + " (selected user) found in saved ION list.");
		}
	}

}
