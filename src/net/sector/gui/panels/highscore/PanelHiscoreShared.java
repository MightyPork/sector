package net.sector.gui.panels.highscore;


import java.util.Collections;

import net.sector.App;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.RadioButton;
import net.sector.gui.widgets.input.RadioButton.RadioGroup;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.level.ELevel;
import net.sector.level.LevelBundle;
import net.sector.level.SuperContext;
import net.sector.level.highscore.HighscoreEntry;
import net.sector.level.highscore.HighscoreTable;
import net.sector.network.UserProfile;
import net.sector.network.communication.LeaderboardClient;
import net.sector.network.communication.ServerError;
import net.sector.network.responses.ObjScoreInfo;
import net.sector.network.responses.ObjScoreList;
import net.sector.util.Align;
import net.sector.util.Log;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


/**
 * Highscore panel for shared levels
 * 
 * @author MightyPork
 */
public class PanelHiscoreShared extends PanelGui {

	private static final int BACK = 0;
	private static final int RADIO = 1;

	private HighscoreTable localTable;

	private CompositeScrollBox scrollBox;

	private LevelBundle level;

	private Button bnClose;

	private ObjScoreList netScores = new ObjScoreList();

	private RadioButton ckLocal;
	@SuppressWarnings("unused")
	private RadioButton ckShared;

	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 * @param level the level
	 */
	public PanelHiscoreShared(Screen screen, LevelBundle level) {
		super(screen);

		if (level.type != ELevel.NET) {
			throw new RuntimeException("Cannot open PanelHiscoreShowShared for local level.");
		}

		if (App.offlineMode) {
			Log.e("Cannot open PanelGameOverShared_User while offline.");
			netScores = new ObjScoreList();
		} else {
			try {
				netScores = LeaderboardClient.getLevelScores(level.lid);
			} catch (ServerError e) {
				Log.e("Error getting leaderboard.", e);
				Log.e("Error loading scores from server.", e);
				netScores = new ObjScoreList();
			}
		}

		this.level = level;
		this.localTable = level.getHighscoreTable();
		if (localTable == null) localTable = new HighscoreTable();


		for (UserProfile user : SuperContext.userProfiles) {
			if (user.isRemoved || !user.isLoggedIn) continue;
			int userScore = netScores.getScoreForUid(user.uid);

			if (userScore != -1) {
				HighscoreEntry he;
				localTable.add(he = new HighscoreEntry(userScore, user.uname));
				he.isLocal = false;
				he.uid = user.uid;
			}
		}

		localTable.sort();
		Collections.sort(netScores);
	}


	private static HighscoreEntryFactory entryFactory = HighscoreEntryFactory.instance;

	@Override
	public void initGui(GuiRoot root) {
		//@formatter:off
		WindowFrame frame = new WindowFrame();
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text(level.title, "highscore_level_name").setColorText(new RGB(0xFFFF00)).setMarginsV(10, 15));
										
				scrollBox = new CompositeScrollBox(10, entryFactory);
				
				
				LayoutH h = new LayoutH(Align.CENTER);
					
					RadioGroup group = RadioButton.newGroup();
					h.add(new Text("Scores: ", "small_text").setColorText(new RGB(RGB.WHITE, 0.6)));
					h.add(new Gap(10,0));
					h.add(ckLocal = new RadioButton(RADIO, "Local", "small_text").setGroup(group));
					h.add(new Gap(10,0));
					h.add(ckShared = (RadioButton) new RadioButton(RADIO, "Global", "small_text").setGroup(group).setChecked(true));
					
				v.add(h);
			
				v.add(scrollBox);
		
				h = new LayoutH(Align.CENTER);
					h.add(bnClose = new Button(BACK, "Back", "small_text"));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);

		printHiscore(1);
		//@formatter:on
	}

	private void printHiscore(int page) {

		scrollBox.removeAll();

		if (page == 0) {

			int i = 1;
			for (HighscoreEntry entry : localTable) {
				scrollBox.addItem(entryFactory.getItem(i, entry));
				i++;
			}

		} else {

			int i = 1;
			for (ObjScoreInfo entry : netScores) {
				scrollBox.addItem(entryFactory.getItem(i, entry));
				i++;
			}

		}

		scrollBox.refresh();
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (widget.id == BACK) {
			closePanel();
			return;
		}
		if (widget.id == RADIO) {
			printHiscore(ckLocal.isChecked() ? 0 : 1);
			return;
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);
		if (key == Keyboard.KEY_ESCAPE && down) {
			actionPerformed(bnClose);
		}
	}

	@Override
	public void onFocus() {
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onBlur() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean hasBackgroundLayer() {
		return true;
	}
	
	@Override
	public RGB getBackgroundColor() {
		return new RGB(0, 0.4);
	}

}