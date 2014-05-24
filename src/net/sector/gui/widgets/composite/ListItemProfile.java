package net.sector.gui.widgets.composite;


import java.util.ArrayList;

import net.sector.gui.panels.dialogs.PanelDialogModal;
import net.sector.gui.panels.dialogs.PanelDialogModal.IDialogListener;
import net.sector.gui.panels.profiles.PanelProfileEdit;
import net.sector.gui.panels.profiles.PanelProfileFixLogin;
import net.sector.gui.panels.profiles.PanelProfiles;
import net.sector.gui.widgets.IRefreshable;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.display.ColorRectange;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.ButtonIcon;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.input.Function;
import net.sector.network.UserProfile;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


public class ListItemProfile extends LayoutH implements IRefreshable {

	public UserProfile profile = null;

	public ArrayList<Widget> scrollList = null;


	protected RGB indicatorColor = new RGB(0x999999);
	protected ButtonIcon bnSelect, bnEdit, bnRemove;
	protected Text txName;

	private ColorRectange indicator;

	/**
	 * Set scroll list (list of children in CompositeScrollBox
	 * 
	 * @param scrollList arraylist of children
	 * @return this
	 */
	public ListItemProfile setScrollList(ArrayList<Widget> scrollList) {
		this.scrollList = scrollList;
		return this;
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		RGB topC = new RGB(0x5D3DD1, 0.4);
		RGB downC = new RGB(0x3D71D1, 0.4);

		if (profile.isSelected()) {
			topC = new RGB(0x5D3DD1, 0.9);
			downC = new RGB(0x3D71D1, 0.9);
		}

		if (profile.isRemoved()) {
			topC = new RGB(0x888888, 0.5);
			downC = new RGB(0x666666, 0.5);
		}

		RenderUtils.quadRectGradV(rect, topC, downC);

		if (!profile.isRemoved()) {
			superRender(mouse);
		}
	}

	public void superRender(Coord pos) {
		super.render(pos);
	}

	public ListItemProfile(UserProfile profile) {
		this();
		setProfile(profile);
	}

	public ListItemProfile() {
		super(Align.CENTER);
		setMinWidth(500);
		setMinHeight(50);

		add(new Gap(5, 0));
		add(indicator = new ColorRectange(20, 50, 5, 3, indicatorColor));

		String texture = "designer_icons";
		Coord imgSize = new Coord(32, 32);

		add(bnSelect = new ButtonIcon(-1, texture, 0, 3).setColor(RGB.GREEN));
		add(new Gap(5, 0));
		add(txName = (Text) new Text("", "small_menu").setTextAlign(Align.LEFT).setMinWidth(315));
		add(new Gap(5, 0));
		add(bnEdit = new ButtonIcon(-1, texture, 2, 3).setColor(new RGB(0xCCCCCC)));
		add(bnRemove = new ButtonIcon(-1, texture, 1, 3).setColor(RGB.RED));
		calcChildSizes();


		bnSelect.setTooltip("Select", new RGB(0x66ff66));
		bnEdit.setTooltip("Edit", RGB.WHITE);
		bnRemove.setTooltip("Forget", new RGB(0xff6666));

		bnSelect.addOnClickHandler(new Function<Boolean>() {
			@Override
			public Boolean run(Object... args) {
				profile.selectThisUser();
				refreshAll();
				return true;
			}
		});


		bnEdit.addOnClickHandler(new Function<Boolean>() {
			@Override
			public Boolean run(Object... args) {

				if (!profile.isActivated()) {
					getPanel().openPanel(new PanelProfileFixLogin(getPanel().screen, ((PanelProfiles) getPanel()).loginHandler, profile));
				} else {
					getPanel().openPanel(new PanelProfileEdit(getPanel().screen, ListItemProfile.this));
				}

				return true;
			}
		});


		bnRemove.addOnClickHandler(new Function<Boolean>() {
			@Override
			public Boolean run(Object... args) {

				PanelDialogModal p;

				IDialogListener listener = new IDialogListener() {
					@Override
					public void onDialogButton(int dialogId, int button) {
						if (button == 0) return;
						if (button == 1) {
							profile.setRemoved(true);
							refreshAll();
						}
					}
				};

				String msg = "This will only FORGET the login.\n\nTo DELETE a profile, use profile edit screen. ";

				p = new PanelDialogModal(getPanel().screen, listener, -1, true, msg, "Cancel", "Forget " + profile.uname);
				p.setEnterButton(1);
				getPanel().openPanel(p);

				return true;
			}
		});

		refresh();

	}

	public ListItemProfile setProfile(UserProfile profile) {
		this.profile = profile;
		refresh();
		return this;
	}

	public void refreshAll() {
		for (Widget w : scrollList) {
			IRefreshable ref = (IRefreshable) w;
			ref.refresh();
		}
	}

	@Override
	public void refresh() {
		if (profile == null) return;
		txName.setText(profile.uname);

		if (profile.isRemoved()) {
			indicatorColor.setTo(RGB.BLACK);
			bnSelect.setEnabled(false);
			bnRemove.setEnabled(false);
			bnEdit.setEnabled(false);
			bnSelect.setSelected(false);
			return;
		}

		if (profile.isActivated()) {
			indicatorColor.setTo(RGB.GREEN);
			indicator.setTooltip("Logged in", new RGB(0x66ff66));
			bnSelect.setEnabled(true);
			bnRemove.setEnabled(true);
			bnEdit.setEnabled(true);
			bnSelect.setSelected(false);
		} else {
			indicatorColor.setTo(RGB.RED);
			indicator.setTooltip("Login failed", new RGB(0xff6666));
			bnSelect.setEnabled(false);
			bnRemove.setEnabled(true);
			bnEdit.setEnabled(true);
			bnSelect.setSelected(false);
		}

		if (profile.isSelected()) {
			bnSelect.setEnabled(true);
			bnSelect.setSelected(true);
		}
	}

}
