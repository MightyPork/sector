package net.sector.gui.panels.profiles;


import net.sector.gui.panels.PanelGui;
import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.dialogs.PanelDialogModal.IDialogListener;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.ListItemProfile;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.TextInput;
import net.sector.gui.widgets.input.TextInputCountry;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.network.UserProfile;
import net.sector.network.communication.ServerError;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


public class PanelProfileEdit extends PanelGui {

	private static final int CANCEL = 0;
	private static final int SUBMIT = 1;
	private static final int DELETE = 2;

	private static final int NAME_EDIT = 10;
	private static final int PASSWORD_EDIT = 11;
	private static final int COUNTRY_EDIT = 12;
	private static final int EMAIL_EDIT = 13;


	private Button bnSubmit;
	private TextInput edName;
	private TextInput edPassword;
	private TextInputCountry edCountry;
	private TextInput edEmail;
	private TextInput edPassword2;
	private UserProfile profile;
	private TextInput edPasswordCurrent;
	private ListItemProfile profileItem;
	private Button bnDel;
	private Button bnCancel;


	public PanelProfileEdit(Screen screen, ListItemProfile profileItem) {
		super(screen);
		this.profile = profileItem.profile;
		this.profileItem = profileItem;
	}



	@Override
	public void initGui(GuiRoot root) {
		//@formatter:off
		WindowFrame frame = new WindowFrame();
		//frame.setTheme(ETheme.GREEN_FRAME);
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Edit Profile", "dialog_heading").setMarginsV(10, 15));
				
				LayoutV v2 = new LayoutV(Align.LEFT);
					Widget h;
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Username:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						h.add(edName = (TextInput) new TextInput(NAME_EDIT, profile.uname, "small_text").setMinWidth(300));					
					v2.add(h);

					v2.add(new Gap(0, 10));
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Password:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						edPassword = (TextInput) new TextInput(PASSWORD_EDIT, profile.password, "small_text").setPasswordMode(true).setMinWidth(300);
						h.add(edPassword);					
					v2.add(h);
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Repeat:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						edPassword2 = (TextInput) new TextInput(PASSWORD_EDIT, profile.password, "small_text").setPasswordMode(true).setMinWidth(300);
						h.add(edPassword2);				
					v2.add(h);
					v2.add(new Gap(0, 10));
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("E-mail:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						h.add(edEmail = (TextInput) new TextInput(EMAIL_EDIT, profile.email, "small_text").setMinWidth(400));					
					v2.add(h);
					
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Country:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						h.add(edCountry = (TextInputCountry) new TextInputCountry(COUNTRY_EDIT, "", "small_text").setMinWidth(400));					
						edCountry.setCountry(profile.country);
					v2.add(h);
									
					v2.add(new Gap(0, 15));
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Gap(30,0));
						h.add(new Text("Enter current password", "small_text").setTextAlign(Align.LEFT).setColorText(new RGB(0xff3344)).setMinWidth(450));
					v2.add(h);
										
					h = new LayoutH(Align.CENTER).setMarginsV(2, 2);
						h.add(new Text("Password:", "small_text").setTextAlign(Align.RIGHT).setMinWidth(130));					
						h.add(new Gap(5,0));
						edPasswordCurrent = (TextInput) new TextInput(PASSWORD_EDIT, "", "small_text").setPasswordMode(true).setMinWidth(300);
						h.add(edPasswordCurrent);					
					v2.add(h);
					
					v2.add(new Gap(0, 15));
					
					edName.setAllowedChars(TextInput.CHARS_USERNAME);
					edEmail.setAllowedChars(TextInput.CHARS_EMAIL);
				
				v.add(v2);
		
				h = new LayoutH(Align.CENTER);
					h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
					h.add(new Gap(100,0));
					h.add(bnSubmit = (Button) new Button(SUBMIT, "Save changes", "small_text").setEnabled(false));
					h.add(new Gap(10,0));
					h.add(bnDel = (Button) new Button(DELETE, "Delete", "small_text").setEnabled(false));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);
//		
//		ETheme theme = ETheme.GREEN;			
//		edName.setTheme(theme);
//		edPassword.setTheme(theme);
//		edPassword2.setTheme(theme);
//		edCountry.setTheme(theme);
//		edEmail.setTheme(theme);
//		edPasswordCurrent.setTheme(theme);
//		bnSubmit.setTheme(theme);
//		bnCancel.setTheme(theme);
//		bnDel.setTheme(theme);

		//@formatter:on
	}

	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == NAME_EDIT || widget.id == PASSWORD_EDIT) {

			bnDel.setEnabled(true);
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

			if (!edPasswordCurrent.getText().equals(profile.password)) {
				bnSubmit.setEnabled(false);
				bnDel.setEnabled(false);
			}

			return;
		}

		if (widget.id == CANCEL) {
			closePanel();
			return;
		}

		if (widget.id == DELETE) {

			PanelDialogModal p;

			IDialogListener listener = new IDialogListener() {
				@Override
				public void onDialogButton(int dialogId, int button) {
					if (button == 0) return;
					if (button == 1) {

						try {
							profile.deleteProfile();
							closePanel();
						} catch (ServerError e) {
							openPanel(new PanelDialogModal(screen, null, -1, true, e.getMessage(), "OK"));
						}

					}
				}
			};

			String msg = "Do you really want to\ndelete this account?";

			p = new PanelDialogModal(screen, listener, -1, true, msg, "No", "Yes");
			p.setEnterButton(1);
			openPanel(p);

			return;
		}

		if (widget.id == SUBMIT) {

			if (!edPasswordCurrent.getText().equals(profile.password)) {
				openPanel(new PanelDialogModal(screen, null, -1, true, "Wrong password!", "OK"));
				return;
			}

			String uname = edName.getText().trim();
			String password = edPassword.getText().trim();
			String email = edEmail.getText().trim();
			String country = edCountry.getTag().trim();

			try {
				profile.editProfile(uname, password, email, country);
			} catch (ServerError e) {
				openPanel(new PanelDialogModal(screen, null, -1, true, e.getMessage(), "OK"));
				return;
			}

			profileItem.refreshAll();

			closePanel();

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