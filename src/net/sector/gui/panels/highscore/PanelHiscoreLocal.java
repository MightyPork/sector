package net.sector.gui.panels.highscore;


import net.sector.gui.panels.PanelGui;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.level.LevelBundle;
import net.sector.level.highscore.HighscoreEntry;
import net.sector.level.highscore.HighscoreTable;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


/**
 * Highscore panel for local and internal levels
 * 
 * @author MightyPork
 */
public class PanelHiscoreLocal extends PanelGui {

	private static final int BACK = 0;

	private HighscoreTable table;

	private CompositeScrollBox scrollBox;

	private LevelBundle level;

	private Button bnClose;

	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 * @param level level bundle
	 */
	public PanelHiscoreLocal(Screen screen, LevelBundle level) {
		super(screen);
		this.level = level;
		this.table = level.getHighscoreTable();
		if (table == null) table = new HighscoreTable();
		table.sort();
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
				
				v.add(scrollBox);
		
				LayoutH h = new LayoutH(Align.CENTER);
					h.add(bnClose = new Button(BACK, "Back", "small_text"));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);

		printHiscore();
		//@formatter:on
	}

	private void printHiscore() {

		scrollBox.removeAll();

		int i = 1;
		for (HighscoreEntry he : table) {
			scrollBox.addItem(entryFactory.getItem(i, he));
			i++;
		}

		scrollBox.refresh();
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (widget.id == BACK) {
			closePanel();
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