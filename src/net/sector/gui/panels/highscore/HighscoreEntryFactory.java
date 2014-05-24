package net.sector.gui.panels.highscore;


import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.ListItemHighscore;
import net.sector.level.SuperContext;
import net.sector.level.highscore.HighscoreEntry;
import net.sector.network.UserProfile;
import net.sector.network.responses.ObjScoreInfo;


/**
 * Highscore entry factory
 * 
 * @author MightyPork
 */
public class HighscoreEntryFactory implements IWidgetFactory {

	/** Instance of this factory */
	public static HighscoreEntryFactory instance = new HighscoreEntryFactory();

	@Override
	public Widget getWidget() {
		return getItem(0, (HighscoreEntry) null);
	}

	private ListItemHighscore getDefaultItem() {
		return (ListItemHighscore) new ListItemHighscore(0, "", 0, false, false).setMargins(2, 1, 2, 1);
	}

	private ListItemHighscore getItem(int position, String name, int score, boolean user, boolean active) {
		return (ListItemHighscore) new ListItemHighscore(position, name, score, user, active).setMargins(2, 1, 2, 1);
	}

	/**
	 * Get item from ObjScoreInfo
	 * 
	 * @param position position in list
	 * @param sc ObjScoreInfo
	 * @return list item
	 */
	public ListItemHighscore getItem(int position, ObjScoreInfo sc) {
		if (sc == null) {
			return getDefaultItem();
		} else {
			boolean active = false;
			boolean user = true;

			if (SuperContext.selectedUser != null && sc.uid.equals(SuperContext.selectedUser.uid)) {
				active = true;
			}

			return getItem(position, sc.uname, sc.score, user, active);
		}
	}

	/**
	 * Get item from HighscoreEntry
	 * 
	 * @param position position in list
	 * @param entry HighscoreEntry
	 * @return list item
	 */
	public ListItemHighscore getItem(int position, HighscoreEntry entry) {
		if (entry == null) return getDefaultItem();

		boolean active = false;
		boolean user = false;

		// entry injected from net scores
		if (!entry.isLocal) user = true;

		// check if this entry belongs to any active user
		if (entry.uid.length() > 0) {
			for (UserProfile user1 : SuperContext.userProfiles) {
				if (user1.isRemoved || !user1.isLoggedIn) continue;
				if (user1.uid.equals(entry.uid)) {
					user = true; // is active user's score					
					break;
				}
			}

			// check if this score belongs to the selected user
			if (SuperContext.selectedUser != null && entry.uid.equals(SuperContext.selectedUser.uid)) {
				active = true;
			}
		}

		if (entry.justAdded) active = true;

		return getItem(position, entry.name, entry.score, user, active);
	}

}
