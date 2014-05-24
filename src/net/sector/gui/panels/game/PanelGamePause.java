package net.sector.gui.panels.game;



import net.sector.App;
import net.sector.gui.panels.PanelConfig;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.panels.dialogs.EDialogColor;
import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.dialogs.PanelDialogModal.IDialogListener;
import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenDesigner;
import net.sector.gui.screens.ScreenLevels;
import net.sector.gui.screens.ScreenMenuMain;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.menu.MenuButton;
import net.sector.gui.widgets.menu.MenuTitle;
import net.sector.level.SuperContext;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


/**
 * Overlay panel for paused game.
 * 
 * @author MightyPork
 */
public class PanelGamePause extends PanelGui {

	private static final int RESUME = 0;
	private static final int RESTART = 1;
	private static final int LEVEL_LIST = 2;
	private static final int MAIN_MENU = 3;
	private static final int EXIT = 4;
	private static final int CFG = 5;
	private MenuButton bnRestart;
	private MenuButton bnResume;

	public PanelGamePause(Screen screen) {
		super(screen);
	}

	@Override
	public void initGui(GuiRoot root) {
		LayoutV v = new LayoutV(Align.CENTER);
		root.setRootWidget(v);
		v.add(new MenuTitle("Game paused").setMargins(0, 0, 0, 20));
		v.add(bnResume = new MenuButton(RESUME, "Resume"));
		v.add(bnRestart = new MenuButton(RESTART, "Try again"));
		v.add(new Gap(0, 20 * App.inst.fs2()));
		v.add(new MenuButton(LEVEL_LIST, "Select level"));
		v.add(new MenuButton(MAIN_MENU, "Main menu"));
		v.add(new Gap(0, 20 * App.inst.fs2()));
		v.add(new MenuButton(CFG, "Settings"));
		v.add(new MenuButton(EXIT, "Quit"));
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (widget.id == MAIN_MENU) {

			PanelDialogModal p;

			IDialogListener listener = new IDialogListener() {
				@Override
				public void onDialogButton(int dialogId, int button) {
					if (button == 1) {
						app.replaceScreen(new ScreenMenuMain());
					}
				}
			};

			p = new PanelDialogModal(screen, listener, -1, true, "Abort current game\nand go to Main menu?", "No", "Yes").setEnterButton(1);
			p.setColorTheme(EDialogColor.BLUE);

			openPanel(p);
			return;
		}

		if (widget.id == RESUME) {
			closePanel();
			return;
		}

		if (widget.id == RESTART) {

			PanelDialogModal p;

			IDialogListener listener = new IDialogListener() {
				@Override
				public void onDialogButton(int dialogId, int button) {
					if (button == 1) {
						SuperContext.getGameContext().restoreCursor();
						app.replaceScreen(new ScreenDesigner());
					}
				}
			};

			p = new PanelDialogModal(screen, listener, -1, true, "Abort current game\nand try again?", "No", "Yes").setEnterButton(1);
			p.setColorTheme(EDialogColor.BLUE);

			openPanel(p);

			return;
		}

		if (widget.id == LEVEL_LIST) {

			PanelDialogModal p;

			IDialogListener listener = new IDialogListener() {
				@Override
				public void onDialogButton(int dialogId, int button) {
					if (button == 1) {
						app.replaceScreen(new ScreenLevels());
					}
				}
			};

			p = new PanelDialogModal(screen, listener, -1, true, "Abort current game\nand go to level list?", "No", "Yes").setEnterButton(1);
			p.setColorTheme(EDialogColor.BLUE);

			openPanel(p);
			return;
		}

		if (widget.id == CFG) {
			openPanel(new PanelConfig(screen, 1));
			return;
		}

		if (widget.id == EXIT) {

			PanelDialogModal p;

			IDialogListener listener = new IDialogListener() {
				@Override
				public void onDialogButton(int dialogId, int button) {
					if (button == 1) App.inst.exit();
				}
			};

			p = new PanelDialogModal(screen, listener, -1, true, "Do you really want to quit?", "No", "Yes").setEnterButton(1);
			p.setColorTheme(EDialogColor.BLUE);

			openPanel(p);

			return;
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);

		if (key == Keyboard.KEY_ESCAPE && down) {
			actionPerformed(bnResume);
		}

		if (key == Keyboard.KEY_R && down) {
			actionPerformed(bnRestart);
		}
	}

	@Override
	public boolean hasBackgroundLayer() {
		return true;
	}
	
	@Override
	public RGB getBackgroundColor() {
		return new RGB(0, 0.7);
	}

}