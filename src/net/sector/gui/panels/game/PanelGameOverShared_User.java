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
import net.sector.network.communication.ServerError;
import net.sector.network.responses.ObjScoreInfo;
import net.sector.network.responses.ObjScoreList;
import net.sector.sounds.Music;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.Log;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.util.StringUtils;


/**
 * Game over screen for Shared levels and registered user.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PanelGameOverShared_User extends PanelGui {

	private static final int RETRY = 0, GOTO_LEVELS = 1, GOTO_MAIN_MENU = 2;

	private int score;

	private CompositeScrollBox scrollBox;

	private ObjScoreList scores;

	private String lid;

	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 */
	public PanelGameOverShared_User(Screen screen) {
		super(screen);
		GameContext ctx = SuperContext.getGameContext();

		this.score = ctx.getCursor().scoreTotal;

		if (ctx.levelBundle.type != ELevel.NET) {
			throw new RuntimeException("Cannot open PanelGameOverShared_User for local level.");
		}

		if (SuperContext.selectedUser == null) {
			throw new RuntimeException("Cannot open PanelGameOverShared_User for Guest user.");
		}

		if (App.offlineMode) {
			throw new RuntimeException("Cannot open PanelGameOverShared_User while offline.");
		}

		lid = ctx.levelBundle.lid;

		scores = null;
		try {
			scores = SuperContext.selectedUser.submitScore(lid, score);
		} catch (ServerError e) {
			Log.e("Error loading scores from server.", e);
			scores = new ObjScoreList();
		}

		Collections.sort(scores);
	}


	private static HighscoreEntryFactory entryFactory = HighscoreEntryFactory.instance;

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
				v.add(new Text("Game over!", "gameover").setMarginsV(10, 5));
				v.add(new Text("Score: " + StringUtils.formatInt(score), "gameover_score").setColorText(new RGB(1, 0.8, 0)));
				v.add(new Gap(0, 6));
				
				String uid = SuperContext.selectedUser.uid;
								
				int position = scores.getUserPosition(uid);
				
				if(position == -1) {
					v.add(new Text("Sorry, could not connect to highscore server.", "larger_text").setColorText(RGB.RED));				
				}else {		
					v.add(new Text("You are "+StringUtils.numberToOrdinal(position)+" in the Universe!", "larger_text").setColorText(RGB.YELLOW));				
					
					if(scores.scoreImproved && scores.lastScore!=-1) {
						v.add(new Text("You improved your record\n"+StringUtils.formatInt(scores.lastScore)+" to "+StringUtils.formatInt(score)));
					}else if(scores.lastScore!=-1) {
						v.add(new Text("You didn't beat your record: "+StringUtils.formatInt(scores.lastScore)));
					}
				}
				
				v.add(new Gap(0,10));
										
				scrollBox = new CompositeScrollBox(9, entryFactory);
				
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

		Collections.sort(scores);

		int i = 1;
		for (ObjScoreInfo sc : scores) {
			scrollBox.addItem(entryFactory.getItem(i, sc));
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