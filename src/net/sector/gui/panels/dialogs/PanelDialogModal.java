package net.sector.gui.panels.dialogs;


import net.sector.gui.panels.Panel;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.sounds.Sounds;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


/**
 * Main menu panel
 * 
 * @author MightyPork
 */
public class PanelDialogModal extends PanelGui {

	/**
	 * Dialog click handler
	 * 
	 * @author MightyPork
	 */
	public interface IDialogListener {
		/**
		 * on dialog closed by button press
		 * 
		 * @param dialogId id of the dualog used
		 * @param button button index, or -1 on ESC
		 */
		public void onDialogButton(int dialogId, int button);
	}

	private String text;
	private String[] buttons;
	private IDialogListener listener;
	private boolean cancellable = true;
	private int id = -1;

	private int enterIndex = -1;

	public PanelDialogModal setEnterButton(int index) {
		enterIndex = index;
		return this;
	}

	private EDialogColor color = EDialogColor.BLUE;

	/**
	 * Set color theme
	 * 
	 * @param index 0 = green, 1 = blue
	 * @return this
	 */
	public Panel setColorTheme(EDialogColor color) {
		this.color = color;
		return this;
	}

	/**
	 * Create modal dialog
	 * 
	 * @param screen parent screen
	 * @param listener onButton listenner (handles user input)
	 * @param id id
	 * @param cancellable can be cancelled by ESC (listener gets -1)
	 * @param text shown text
	 * @param buttons button texts, IDs 0,1,2...
	 */
	public PanelDialogModal(Screen screen, IDialogListener listener, int id, boolean cancellable, String text, String... buttons) {
		super(screen);
		this.text = text;
		this.buttons = buttons;
		this.listener = listener;
		this.cancellable = cancellable;
		this.id = id;
	}

	@Override
	public void initGui(GuiRoot gui) {
		WindowFrame frame = new WindowFrame();

		switch (color) {
			case GREEN:
				frame.setTheme(ETheme.GREEN_FRAME);
				break;
			case BLUE:
				frame.setTheme(ETheme.BLUE_FRAME);
				break;
		}

		frame.setPadding(10, 10, 15, 15);
		frame.enableShadow(true);

		LayoutV v = new LayoutV(Align.CENTER);
		v.add(new Text(text, "small_text"));

		LayoutH h = new LayoutH(Align.CENTER);
		int i = 0;
		for (String s : buttons) {
			Button btn = new Button(i++, s, "small_menu");

			switch (color) {
				case GREEN:
					btn.setTheme(ETheme.GREEN);
					break;
				case BLUE:
					btn.setTheme(ETheme.BLUE);
					break;
			}

			btn.setMinWidth(80);
			btn.setMarginsH(10, 10);
			h.add(btn);
		}
		v.add(h);

		frame.add(v);

		gui.setRootWidget(frame);

		Sounds.beep_popup.playEffect(1, 0.2f, false);
	}

	@Override
	public boolean hasBackgroundLayer() {
		return true;
	}
	
	@Override
	public RGB getBackgroundColor() {
		return new RGB(0, 0.4);
	}


	@Override
	public void actionPerformed(Widget widget) {
		if (widget instanceof Button) {
			closePanel();
			if (listener != null) listener.onDialogButton(id, widget.getId());
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);

		if (cancellable && down && key == Keyboard.KEY_ESCAPE) {
			closePanel();
			if (listener != null) listener.onDialogButton(id, -1);
			return;
		}

		if ((enterIndex != -1 || buttons.length == 1) && down && (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER)) {
			closePanel();

			if (listener != null) {
				if (enterIndex != -1) {
					listener.onDialogButton(id, enterIndex);
				} else if (buttons.length == 1) {
					listener.onDialogButton(id, 0);
				}
			}
			return;
		}
	}
}
