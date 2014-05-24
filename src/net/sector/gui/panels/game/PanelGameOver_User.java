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
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.level.ELevel;
import net.sector.level.GameContext;
import net.sector.level.SuperContext;
import net.sector.level.highscore.HighscoreEntry;
import net.sector.level.highscore.HighscoreTable;
import net.sector.sounds.Music;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.Log;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.ion.Ion;
import com.porcupine.util.StringUtils;



/**
 * Highscore panel for local levels and user.
 * 
 * @author MightyPork
 */
public class PanelGameOver_User extends PanelGui {

	private static final int RETRY = 1, GOTO_LEVELS = 2, GOTO_MAIN_MENU = 3;

	private int score;

	private HighscoreTable table;

	private CompositeScrollBox scrollBox;

	private int lastScore;

	private boolean improvedScore;

	private int position;

	private boolean firstTime;

	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 */
	public PanelGameOver_User(Screen screen) {
		super(screen);

		GameContext ctx = SuperContext.getGameContext();

		if (ctx.levelBundle.type == ELevel.NET) {
			throw new RuntimeException("Cannot open PanelGameOver_User for shared level.");
		}

		if (SuperContext.selectedUser == null) {
			throw new RuntimeException("Cannot open PanelGameOver_User for Guest user.");
		}

		if (App.offlineMode) {
			Log.e("Cannot open PanelGameOver_User while offline.");
		}

		this.score = ctx.getCursor().scoreTotal;
		this.table = ctx.levelBundle.getHighscoreTable();


		String uid = SuperContext.selectedUser.uid;

		lastScore = -1;
		firstTime = true;
		position = -1;

		for (HighscoreEntry entry : table) {
			if (entry.uid.equals(uid)) {
				lastScore = entry.score;

				if (score > lastScore) {
					entry.score = score;
				}

				firstTime = false;
				break;
			}
		}

		if (firstTime) {
			HighscoreEntry entry = table.addScore(score, SuperContext.selectedUser.uname);
			entry.uid = uid;
		}

		Collections.sort(table);

		int pos = 1;
		for (HighscoreEntry entry : table) {
			if (entry.uid.equals(uid)) {
				position = pos;
				break;
			}
			pos++;
		}

		Ion.toFile(ctx.levelBundle.getHighscoreFile(), table);

		improvedScore = score > lastScore;
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
				v.add(new Gap(0, 6));
						
				v.add(new Text("You are "+StringUtils.numberToOrdinal(position)+" on this machine!", "larger_text").setColorText(RGB.YELLOW));				
				
				if(!firstTime) {
					if(improvedScore) {
						v.add(new Text("You improved your record\n"+StringUtils.formatInt(lastScore)+" to "+StringUtils.formatInt(score)));
					}else {
						v.add(new Text("You didn't beat your record: "+StringUtils.formatInt(lastScore)));
					}
				}
				
				v.add(new Gap(0,6));
										
				scrollBox = new CompositeScrollBox(9, scrollBoxItemFactory);
				
				v.add(scrollBox);
		
				LayoutH h = new LayoutH(Align.CENTER);
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