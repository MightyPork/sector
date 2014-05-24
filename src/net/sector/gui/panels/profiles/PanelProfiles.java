package net.sector.gui.panels.profiles;


import java.util.Collections;

import net.sector.gui.panels.PanelGui;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.composite.ListItemProfile;
import net.sector.gui.widgets.composite.ListItemProfileGuest;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.level.SuperContext;
import net.sector.network.UserProfile;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


public class PanelProfiles extends PanelGui {

	public interface IOnLoggedInHandler {
		/**
		 * Called when a profile was created and added to
		 * SuperContext.profileList
		 * 
		 * @param profileCreated the new profile
		 */
		public void onLoggedIn(UserProfile profileCreated);
	}

	private static final int BACK = 0;
	private static final int LOG_IN = 1;
	private static final int REGISTER = 2;

	private CompositeScrollBox scrollBox;
	public IOnLoggedInHandler loginHandler = new IOnLoggedInHandler() {
		@Override
		public void onLoggedIn(UserProfile up) {
			SuperContext.selectedUser = up;
			insertProfiles();
			SuperContext.saveUserList();
		}
	};
	private Button bnClose;


	public PanelProfiles(Screen screen) {
		super(screen);
	}


	private static class ProfileEntryFactory implements IWidgetFactory {
		@Override
		public Widget getWidget() {
			return getItem();
		}

		public ListItemProfile getItem() {
			return (ListItemProfile) new ListItemProfile().setMargins(4, 2, 4, 2);
		}

		public ListItemProfile getItem(UserProfile profile) {
			return (ListItemProfile) new ListItemProfile(profile).setMargins(4, 2, 4, 2);
		}
	}


	private static ProfileEntryFactory scrollBoxItemFactory = new ProfileEntryFactory();

	@Override
	public void initGui(GuiRoot root) {
		//@formatter:off
		WindowFrame frame = new WindowFrame();
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Manage Profiles", "small_heading").setMarginsV(10, 15));
										
				scrollBox = new CompositeScrollBox(5, scrollBoxItemFactory);
				
				v.add(scrollBox);
		
				LayoutH h = new LayoutH(Align.CENTER);
					h.add(bnClose = new Button(BACK, "Close", "small_text"));
					h.add(new Gap(100,0));
					h.add(new Button(LOG_IN, "Log in", "small_text"));
					h.add(new Button(REGISTER, "Register", "small_text"));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);

		insertProfiles();
		//@formatter:on
	}

	public void insertProfiles() {

		scrollBox.removeAll();
		Collections.sort(SuperContext.userProfiles);

		scrollBox.addItem(new ListItemProfileGuest().setScrollList(scrollBox.getItems()).setMargins(4, 2, 4, 2));

		for (UserProfile profile : SuperContext.userProfiles) {
			if (profile.isRemoved()) continue;
			scrollBox.addItem(scrollBoxItemFactory.getItem(profile).setScrollList(scrollBox.getItems()));
		}

		scrollBox.refresh();
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == BACK) {
			SuperContext.saveUserList();
			closePanel();
			return;
		}
		if (widget.id == LOG_IN) {
			SuperContext.saveUserList();
			openPanel(new PanelProfileLogIn(screen, loginHandler));
			return;
		}
		if (widget.id == REGISTER) {
			SuperContext.saveUserList();
			openPanel(new PanelProfileRegister(screen, loginHandler));
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
		return false;
	}

}