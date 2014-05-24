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
import net.sector.gui.widgets.input.TextInputCountry;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.level.SuperContext;
import net.sector.network.UserProfile;
import net.sector.network.communication.LeaderboardClient;
import net.sector.network.communication.ServerError;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


public class PanelProfileRegister extends PanelGui {

	private static final int CANCEL = 0;
	private static final int SUBMIT = 1;

	private static final int NAME_EDIT = 10;
	private static final int PASSWORD_EDIT = 11;
	private static final int COUNTRY_EDIT = 12;
	private static final int EMAIL_EDIT = 13;


	private Button bnSubmit;
	private TextInput edName;
	private TextInput edPassword;
	private TextInput edCountry;
	private TextInput edEmail;
	private IOnLoggedInHandler handler;
	private TextInput edPassword2;
	private Button bnCancel;


	public PanelProfileRegister(Screen screen, IOnLoggedInHandler handler) {
		super(screen);
		this.handler = handler;
	}



	@Override
	public void initGui(GuiRoot root) {
		//@formatter:off
		WindowFrame frame = new WindowFrame();
		//frame.setTheme(ETheme.GREEN_FRAME);
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Create Profile", "dialog_heading").setMarginsV(10, 15));
				
				LayoutV v2 = new LayoutV(Align.LEFT);
					Widget h;
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Username:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						h.add(edName = (TextInput) new TextInput(NAME_EDIT, "", "small_text").setMinWidth(300));					
					v2.add(h);

					v2.add(new Gap(0, 10));
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Password:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						edPassword = (TextInput) new TextInput(PASSWORD_EDIT, "", "small_text").setPasswordMode(true).setMinWidth(300);
						h.add(edPassword);					
					v2.add(h);
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Repeat:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						edPassword2 = (TextInput) new TextInput(PASSWORD_EDIT, "", "small_text").setPasswordMode(true).setMinWidth(300);
						h.add(edPassword2);				
					v2.add(h);
					
					v2.add(new Gap(0, 10));
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Gap(30,0));
						h.add(new Text("Optional", "small_text").setTextAlign(Align.LEFT).setColorText(new RGB(0x99ccff)).setMinWidth(450));
					v2.add(h);
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("E-mail:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						h.add(edEmail = (TextInput) new TextInput(EMAIL_EDIT, "", "small_text").setMinWidth(400));					
					v2.add(h);
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Country:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						h.add(edCountry = (TextInput) new TextInputCountry(COUNTRY_EDIT, "", "small_text").setMinWidth(400));					
					v2.add(h);
									
					v2.add(new Gap(0, 15));
					
					edName.setAllowedChars(TextInput.CHARS_USERNAME);
					edEmail.setAllowedChars(TextInput.CHARS_EMAIL);
				
				v.add(v2);
		
				h = new LayoutH(Align.CENTER);
					h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
					h.add(new Gap(10,0));
					h.add(bnSubmit = (Button) new Button(SUBMIT, "Register", "small_text").setEnabled(false));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);
		
		
//		ETheme theme = ETheme.GREEN;			
//		edName.setTheme(theme);
//		edPassword.setTheme(theme);
//		edPassword2.setTheme(theme);
//		edEmail.setTheme(theme);
//		edCountry.setTheme(theme);
//		bnSubmit.setTheme(theme);
//		bnCancel.setTheme(theme);

		//@formatter:on
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == NAME_EDIT || widget.id == PASSWORD_EDIT) {

			bnSubmit.setEnabled(true);
			if (edName.getText().trim().length() == 0) {
				bnSubmit.setEnabled(false);
			}

			if (edPassword.getText().length() == 0) {
				bnSubmit.setEnabled(false);
			}

			if (!edPassword.getText().equals(edPassword2.getText())) {
				bnSubmit.setEnabled(false);
			}

			return;
		}

		if (widget.id == CANCEL) {
			closePanel();
			return;
		}

		if (widget.id == SUBMIT) {
			String uname = edName.getText().trim();
			String password = edPassword.getText().trim();
			String email = edEmail.getText().trim();
			String country = edCountry.getTag().trim();

			UserProfile up;
			try {
				up = LeaderboardClient.createProfileRegister(uname, password, email, country);
			} catch (ServerError e) {

				PanelDialogModal p = new PanelDialogModal(screen, null, -1, true, e.getMessage(), "OK");
				openPanel(p);

				return;
			}

			SuperContext.userProfiles.add(up);
			closePanel();

			if (handler != null) handler.onLoggedIn(up);

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