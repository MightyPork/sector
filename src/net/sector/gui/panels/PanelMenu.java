package net.sector.gui.panels;


import java.io.IOException;

import net.sector.App;
import net.sector.Constants;
import net.sector.GameConfig;
import net.sector.gui.panels.dialogs.EDialogColor;
import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.dialogs.PanelDialogModal.IDialogListener;
import net.sector.gui.panels.profiles.PanelProfiles;
import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenDesigner;
import net.sector.gui.screens.ScreenLevels;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.menu.MenuButton;
import net.sector.gui.widgets.menu.MenuLoginDisplay;
import net.sector.gui.widgets.menu.MenuTitle;
import net.sector.level.SuperContext;
import net.sector.util.Align;
import net.sector.util.Log;


/**
 * Main menu panel
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class PanelMenu extends PanelGui {
	private static final int EXIT = 0;
	private static final int PLAY = 1;
	private static final int CONFIG = 3;
	private static final int USERS = 4;
	private static final int LEVELS = 5;

	public PanelMenu(Screen screen) {
		super(screen);
	}

	@Override
	public void initGui(GuiRoot root) {
		LayoutV v = new LayoutV(Align.CENTER);
		v.add(new MenuTitle("SECTOR"));
		v.add(new Gap(0, 20));
		//v.add(new MenuButton(PLAY, "Start game"));
		v.add(new MenuButton(LEVELS, "Challenges"));
		//v.add(new MenuButton(HISCORE, "Highscore"));
		v.add(new Gap(0, 15));
		v.add(new MenuLoginDisplay(USERS));
		v.add(new Gap(0, 15));
		v.add(new MenuButton(CONFIG, "Settings"));
		v.add(new MenuButton(EXIT, "Quit game"));
		root.setRootWidget(v);
	}

	@Override
	public void onPostInit() {
		if (!App.offlineMode && !SuperContext.updateAlertShown && GameConfig.enableUpdateAlerts) {
			if (SuperContext.latestVersionNumber > Constants.VERSION_NUMBER) {
				SuperContext.updateAlertShown = true;

				String message = "Update your SECTOR!\n";
				message += "New version " + SuperContext.latestVersionName + " is available.\n";

				IDialogListener listener = new IDialogListener() {

					@Override
					public void onDialogButton(int dialogId, int button) {
						if (button == 0) {
							try {
								java.awt.Desktop.getDesktop().browse(java.net.URI.create(Constants.WEB_URL));
							} catch (IOException e) {
								Log.e("Error openning website.", e);
							}
						}
					}
				};


				openPanel(new PanelDialogModal(screen, listener, -1, true, message, "Open website", "Close").setEnterButton(1).setColorTheme(
						EDialogColor.GREEN));
			}
		}
	}

	@Override
	public boolean hasBackgroundLayer() {
		return false;
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		switch (widget.id) {
			case EXIT:
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
				break;

			case PLAY:
				app.replaceScreen(new ScreenDesigner());
				break;

			case LEVELS:
				app.replaceScreen(new ScreenLevels());
				break;

			case CONFIG:
				openPanel(new PanelConfig(screen, 0));
				break;

			case USERS:
				openPanel(new PanelProfiles(screen));
				break;
		}
	}
}
