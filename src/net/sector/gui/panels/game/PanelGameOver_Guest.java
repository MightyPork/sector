package net.sector.gui.panels.game;


import java.util.Collections;

import net.sector.App;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.panels.highscore.HighscoreEntryFactory;
import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenDesigner;
import net.sector.gui.screens.ScreenLevels;
import net.sector.gui.screens.ScreenMenuMain;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.TextInput;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.level.ELevel;
import net.sector.level.GameContext;
import net.sector.level.SuperContext;
import net.sector.level.highscore.HighscoreEntry;
import net.sector.level.highscore.HighscoreTable;
import net.sector.network.UserProfile;
import net.sector.network.communication.LeaderboardClient;
import net.sector.network.communication.ServerError;
import net.sector.network.responses.ObjScoreList;
import net.sector.sounds.Music;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.Log;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.ion.Ion;
import com.porcupine.util.StringUtils;


/**
 * Highscore panel for guests (net and local levels)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PanelGameOver_Guest extends PanelGui {

	private static final int OK = 0, RETRY = 1, GOTO_LEVELS = 2, GOTO_MAIN_MENU = 3;

	private int score;

	private Widget edName;

	private Button bnOk;

	private HighscoreTable table;

	private boolean hiscoreAdded = false;

	private CompositeScrollBox scrollBox;

	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 */
	public PanelGameOver_Guest(Screen screen) {
		super(screen);
		GameContext ctx = SuperContext.getGameContext();

		this.score = ctx.getCursor().scoreTotal;
		this.table = ctx.levelBundle.getHighscoreTable();

		if (!App.offlineMode && ctx.levelType == ELevel.NET) {
			ObjScoreList netscores;
			try {
				netscores = LeaderboardClient.getLevelScores(ctx.levelBundle.lid);

				for (UserProfile user : SuperContext.userProfiles) {
					if (user.isRemoved || !user.isLoggedIn) continue;
					int userScore = netscores.getScoreForUid(user.uid);

					if (userScore != -1) {
						HighscoreEntry he;
						table.add(he = new HighscoreEntry(userScore, user.uname));
						he.isLocal = false;
					}
				}
			} catch (ServerError e) {
				Log.e("Error getting level scores.", e);
			}
		}

	}

	private static HighscoreEntryFactory scrollBoxItemFactory = HighscoreEntryFactory.instance;

	@Override
	public void initGui(GuiRoot root) {
		Sounds.shield_loop.stop();
		Sounds.timer_loop.stop();
		Music.playMenu();

		//@formatter:off
		WindowFrame frame = new WindowFrame();
			frame.setPadding(5, 5, 5, 5);
			frame.enableShadow(true);
	
			LayoutV v = new LayoutV(Align.CENTER);
				v.add(new Text("Game over!", "gameover").setMarginsV(10, 3));
				v.add(new Text("Score: " + StringUtils.formatInt(score), "gameover_score").setColorText(new RGB(1, 0.8, 0)));
				v.add(new Gap(0, 4));
				
				Collections.sort(table);

				int pos = 1;
				for (HighscoreEntry entry : table) {
					if (entry.score < score) {
						break;
					}
					pos++;
				}
				
				v.add(new Text("You are "+StringUtils.numberToOrdinal(pos)+" on this machine!", "larger_text").setColorText(RGB.YELLOW));	
		
				LayoutH h;
					h = new LayoutH(Align.CENTER);
					h.setMargins(5, 0, 5, 0);
					h.add(new Text("Name:", "small_text"));
					h.add(edName = new TextInput(-1, "", "small_text").setMinWidth(340));
					h.add(bnOk = (Button) new Button(OK, "OK", "small_text").setEnabled(false));		
				v.add(h);
				
				if(SuperContext.selectedUser != null) {
					edName.setText(SuperContext.selectedUser.uname);
					bnOk.setEnabled(true);
				}
										
				scrollBox = new CompositeScrollBox(9, scrollBoxItemFactory);
				
				v.add(scrollBox);
		
				h = new LayoutH(Align.CENTER);
					h.add(new Button(GOTO_MAIN_MENU, "Main menu", "small_text"));
					h.add(new Gap(30, 0));
					h.add(new Button(RETRY, "Try again", "small_text"));
					h.add(new Button(GOTO_LEVELS, "Select level", "small_text"));
				v.add(h);
	
			frame.add(v);
		root.setRootWidget(frame);

		printHiscore();
		//@formatter:on
	}

	private void printHiscore() {

		scrollBox.removeAll();

		Collections.sort(table);

		int i = 1;
		for (HighscoreEntry he : table) {
			scrollBox.addItem(scrollBoxItemFactory.getItem(i, he));
			i++;
		}

		scrollBox.refresh();
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (!hiscoreAdded) bnOk.setEnabled(edName.getText().length() > 0);

		if (bnOk.isEnabled() && widget.id == OK) {
			HighscoreEntry entry = table.addScore(score, edName.getText().trim());
			entry.justAdded = true;
			table.sort();
			printHiscore();
			edName.setEnabled(false);
			bnOk.setEnabled(false);
			hiscoreAdded = true;
			Ion.toFile(SuperContext.getGameContext().levelBundle.getHighscoreFile(), table);
			return;
		}

		if (widget.id == GOTO_LEVELS) {
			app.replaceScreen(new ScreenLevels());
			return;
		}

		if (widget.id == GOTO_MAIN_MENU) {
			app.replaceScreen(new ScreenMenuMain());
			return;
		}

		if (widget.id == RETRY) {
			SuperContext.getGameContext().restoreCursor();
			app.replaceScreen(new ScreenDesigner());
			return;
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);
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
		return new RGB(0, 0.6);
	}

}