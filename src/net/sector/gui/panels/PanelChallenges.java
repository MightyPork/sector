package net.sector.gui.panels;


import java.util.Collections;
import java.util.List;

import net.sector.App;
import net.sector.gui.panels.profiles.PanelProfiles;
import net.sector.gui.screens.Screen;
import net.sector.gui.screens.ScreenMenuMain;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.composite.ListItemLevel;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.RadioButton;
import net.sector.gui.widgets.input.RadioButton.RadioGroup;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.input.Function;
import net.sector.level.LevelBundle;
import net.sector.level.LevelRegistry;
import net.sector.level.SuperContext;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


public class PanelChallenges extends PanelGui {

	private static final int MENU = 0;
	private static final int CHANGE_PROFILE = 2;
	private static final int RADIO = 1;

	private CompositeScrollBox scrollBox;
	private Text txUser;
	private Text txUserLabel;
	private LayoutH topLayout;
	private RadioButton ckLocal;
	private RadioButton ckGlobal;
	private RadioButton ckInternal;
	private RadioButton ckAll;
	private Button bnClose;
	private Widget bnProfiles;

	private static int lastCheckbox = 3;


	public PanelChallenges(Screen screen) {
		super(screen);
	}


	private static class LevelListItemFactory implements IWidgetFactory {
		@Override
		public Widget getWidget() {
			return getItem();
		}

		public ListItemLevel getItem() {
			return getItem(null);
		}

		public ListItemLevel getItem(LevelBundle level) {
			return (ListItemLevel) new ListItemLevel(level).setMargins(4, 2, 4, 2);
		}
	}


	private static LevelListItemFactory scrollBoxItemFactory = new LevelListItemFactory();

	@Override
	public void initGui(GuiRoot root) {
		//@formatter:off
		
		LayoutV mainLayout = new LayoutV(Align.CENTER);
		
			topLayout = new LayoutH(Align.CENTER);
				topLayout.add(bnClose = new Button(MENU, "Menu", "small_text"));
			
				LayoutV v1 = (LayoutV) new LayoutV(Align.CENTER).setMinWidth(350);
					LayoutH h1 = new LayoutH(Align.CENTER);		
					
						h1.add(txUserLabel = new Text("User: ", "login_display1"));				
						h1.add(txUser =  new Text("username", "login_display1"));
						
						Function<Boolean> handler = new Function<Boolean>() {							
							@Override
							public Boolean run(Object... args) {								
								if(App.offlineMode) return false;
								actionPerformed(bnProfiles);
								return true;
							}
						};
						
						txUser.addOnClickHandler(handler);
						txUserLabel.addOnClickHandler(handler);
						
						
						RGB c1main = new RGB(0x0B74E3);
						RGB c1blur = new RGB(0x003185, 0.2);						
						txUserLabel.setColorText(c1main).setBlur(c1blur, 2);						
						
						RGB c2main = new RGB(0x0CADED);
						RGB c2blur = new RGB(0x0C62A8, 0.1);						
						txUser.setColorText(c2main).setBlur(c2blur, 2);
						
					v1.add(h1);
				topLayout.add(v1);
			
				topLayout.add(bnProfiles = new Button(CHANGE_PROFILE, "Profiles", "small_text").setEnabled(!App.offlineMode));
				
		mainLayout.add(topLayout);
		
			WindowFrame frame = new WindowFrame();
			
			frame.setPadding(5, 5, 5, 5);
			frame.enableShadow(false);
				
				LayoutV v = new LayoutV(Align.CENTER);
				
					v.add(new Text("Challenge Levels", "small_heading").setMarginsV(10, 15));

					LayoutH h = new LayoutH(Align.CENTER);
					
						RadioGroup group = RadioButton.newGroup();
						h.add(ckInternal = new RadioButton(RADIO, "Built-in", "small_text").setGroup(group));
						h.add(new Gap(10,0));
						h.add(ckLocal = new RadioButton(RADIO, "Local", "small_text").setGroup(group));
						h.add(new Gap(10,0));
						h.add(ckGlobal = new RadioButton(RADIO, "Shared", "small_text").setGroup(group));
						h.add(new Gap(10,0));
						h.add(ckAll = new RadioButton(RADIO, "All", "small_text").setGroup(group));				
						ckAll.setChecked(true);
						
					v.add(h);
											
					scrollBox = new CompositeScrollBox(5, scrollBoxItemFactory);
					
					v.add(scrollBox);
				
				frame.add(v);
				
		mainLayout.add(frame);
		
		root.setRootWidget(mainLayout);

		updateProfileText();
		
		switch(lastCheckbox) {
			case 0:
				ckInternal.setChecked(true);
				insertLevels(LevelRegistry.internalLevels);
				break;
				
			case 1:
				ckLocal.setChecked(true);
				insertLevels(LevelRegistry.localLevels);
				break;
				
			case 2:
				ckGlobal.setChecked(true);
				insertLevels(LevelRegistry.netLevels);
				break;
				
			case 3:
				ckAll.setChecked(true);
				insertLevels(LevelRegistry.getAllLevels());
				break;
		}
		
		//@formatter:on
	}

	public void updateProfileText() {
		txUser.setMinWidth(10);
		txUser.setText(SuperContext.selectedUser == null ? "Guest" : SuperContext.selectedUser.uname);
		updateWidgetPositions();
	}

	public void insertLevels(List<LevelBundle> levels) {

		scrollBox.removeAll();
		Collections.sort(levels);

		for (LevelBundle level : levels) {
			if (!level.isCompatible()) continue;

			scrollBox.addItem(scrollBoxItemFactory.getItem(level));
		}

		scrollBox.refresh();
	}

	@Override
	public void onPostInit() {
		gui.updatePositions();
	}

	@Override
	public void onFocus() {
		super.onFocus();
		updateProfileText();
		updateWidgetPositions();
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (widget.id == MENU) {
			app.replaceScreen(new ScreenMenuMain());
			return;
		}
		if (widget.id == CHANGE_PROFILE) {
			openPanel(new PanelProfiles(screen));
			return;
		}
		if (widget.id == RADIO) {

			if (ckInternal.isChecked()) {
				lastCheckbox = 0;
				insertLevels(LevelRegistry.internalLevels);
			} else if (ckLocal.isChecked()) {
				lastCheckbox = 1;
				insertLevels(LevelRegistry.localLevels);
			} else if (ckGlobal.isChecked()) {
				lastCheckbox = 2;
				insertLevels(LevelRegistry.netLevels);
			} else if (ckAll.isChecked()) {
				lastCheckbox = 3;
				insertLevels(LevelRegistry.getAllLevels());
			}

			return;
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		if (key == Keyboard.KEY_ESCAPE && down) {
			actionPerformed(bnClose);
		}
	}

	@Override
	public boolean hasBackgroundLayer() {
		return false;
	}


}