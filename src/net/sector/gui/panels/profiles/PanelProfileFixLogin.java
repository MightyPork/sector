package net.sector.gui.panels.profiles;


import net.sector.gui.panels.PanelGui;
import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.profiles.PanelProfiles.IOnLoggedInHandler;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.TextInput;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.network.UserProfile;
import net.sector.network.communication.ServerError;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


public class PanelProfileFixLogin extends PanelGui {

	private static final int CANCEL = 0;
	private static final int SUBMIT = 1;
	private static final int NAME_EDIT = 10;
	private static final int PASSWORD_EDIT = 11;


	private Button bnSubmit;
	private TextInput edName;
	private TextInput edPassword;
	private IOnLoggedInHandler handler;
	private UserProfile profile;
	private Button bnCancel;


	public PanelProfileFixLogin(Screen screen, IOnLoggedInHandler handler, UserProfile profile) {
		super(screen);
		this.handler = handler;
		this.profile = profile;
	}



	@Override
	public void initGui(GuiRoot root) {
		//@formatter:off
		WindowFrame frame = new WindowFrame();
		//frame.setTheme(ETheme.GREEN_FRAME);
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Correct login", "dialog_heading").setMarginsV(10, 15));
				
				LayoutH h;
				h = new LayoutH(Align.CENTER);
					h.add(new Text("Username:", "small_text").setMinWidth(130));					
					h.add(new Gap(5,0));
					h.add(edName = (TextInput) new TextInput(NAME_EDIT, profile.uname, "small_text").setMinWidth(270));					
				v.add(h);
				
				h = new LayoutH(Align.CENTER);
					h.add(new Text("Password:", "small_text").setMinWidth(130));					
					h.add(new Gap(5,0));
					edPassword = (TextInput) new TextInput(PASSWORD_EDIT, profile.password, "small_text").setMinWidth(270);
					edPassword.setPasswordMode(true);
					h.add(edPassword);		
					
				v.add(h);
								
				v.add(new Gap(0, 15));
		
				h = new LayoutH(Align.CENTER);
					h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
					h.add(new Gap(10,0));
					h.add(bnSubmit = new Button(SUBMIT, "Log in", "small_text"));
				v.add(h);
				
				edName.setAllowedChars(TextInput.CHARS_USERNAME);
			
			frame.add(v);
		
		root.setRootWidget(frame);
		

//		ETheme theme = ETheme.GREEN;			
//		edName.setTheme(theme);
//		edPassword.setTheme(theme);
//		bnSubmit.setTheme(theme);
//		bnCancel.setTheme(theme);

		//@formatter:on
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == NAME_EDIT || widget.id == PASSWORD_EDIT) {
			bnSubmit.setEnabled(true);
			if (edName.getText().trim().length() == 0) bnSubmit.setEnabled(false);
			if (edPassword.getText().trim().length() == 0) bnSubmit.setEnabled(false);
			return;
		}

		if (widget.id == CANCEL) {
			closePanel();
			return;
		}

		if (widget.id == SUBMIT) {
			String uname = edName.getText().trim();
			String password = edPassword.getText().trim();

			try {
				profile.setNamePassword(uname, password);
				profile.logIn();
			} catch (ServerError e) {
				PanelDialogModal p = new PanelDialogModal(screen, null, -1, true, e.getMessage(), "OK");
				openPanel(p);
				return;
			}

			closePanel();

			if (handler != null) handler.onLoggedIn(profile);

			return;
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);

		if (key == Keyboard.KEY_ESCAPE && down) {
			actionPerformed(bnCancel);
		}

		if (key == Keyboard.KEY_RETURN && down) {
			actionPerformed(bnSubmit);
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