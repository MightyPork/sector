package net.sector.gui.panels.dialogs;


import net.sector.gui.panels.Panel;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.input.EInput;
import net.sector.input.TriggerBundle;
import net.sector.sounds.Sounds;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Capture input dialog - set controls
 * 
 * @author MightyPork
 */
public class PanelDialogCaptureInput extends PanelGui {
	/**
	 * Dialog click handler
	 * 
	 * @author MightyPork
	 */
	public interface ICaptureInputDialogListener {
		/**
		 * on dialog closed by button press
		 * 
		 * @param dialogId id of the dialog used
		 * @param newTrigger new trigger, or null if ESC was pressed.
		 */
		public void onCaptureInputDialogClosed(int dialogId, TriggerBundle newTrigger);
	}

	private ICaptureInputDialogListener listener;
	private int id = -1;
	private String curTriggerDescr;
	private String txtSetFor;
	private boolean isStatic = false;
	private Text txWaiting;

	private boolean doneWaiting = false;
	private long waitingStartTime = 0;
	private TriggerBundle newTrigger;

	private EDialogColor color = EDialogColor.GREEN;

	/**
	 * Set color theme
	 * 
	 * @param color color
	 * @return this
	 */
	public Panel setColorTheme(EDialogColor color) {
		this.color = color;
		return this;
	}

	/**
	 * Create capture input dialog
	 * 
	 * @param screen parent screen
	 * @param listener onButton listener (handles user input)
	 * @param id id
	 * @param isStatic trigger is static (not event, but analog)
	 * @param setFor name of what the trigger is set for
	 * @param curTriggerDescr current trigger description (getLabel)
	 */
	public PanelDialogCaptureInput(Screen screen, ICaptureInputDialogListener listener, int id, boolean isStatic, String setFor,
			String curTriggerDescr) {
		super(screen);
		this.curTriggerDescr = curTriggerDescr;
		this.listener = listener;
		this.id = id;
		this.txtSetFor = setFor;
		this.isStatic = isStatic;
	}

	@Override
	public void initGui(GuiRoot gui) {
		WindowFrame frame = (WindowFrame) new WindowFrame().setTheme(ETheme.BLUE_FRAME);
		frame.setPadding(10, 10, 10, 10);
		frame.enableShadow(true);

		switch (color) {
			case GREEN:
				frame.setTheme(ETheme.GREEN_FRAME);
				break;
			case BLUE:
				frame.setTheme(ETheme.BLUE_FRAME);
				break;
		}

		LayoutV v = new LayoutV(Align.CENTER);
		v.setMinWidth(350);
		v.add(new Text("Set control trigger for:", "smaller_text").setColorText(RGB.WHITE));
		v.add(new Text(txtSetFor, "small_text").setColorText(RGB.GREEN));
		v.add(new Gap(0, 8));
		v.add(new Text("Current trigger:", "smaller_text").setColorText(RGB.WHITE));
		v.add(new Text(curTriggerDescr, "small_text").setColorText(RGB.YELLOW));
		v.add(new Gap(0, 12));
		v.add(new Text("Press button or key to set trigger.\nPress ESC to cancel.", "smaller_text").setColorText(RGB.WHITE));
		v.add(new Gap(0, 6));
		v.add(txWaiting = new Text("[waiting for input]", "small_text").setColorText(RGB.ORANGE));
		frame.add(v);

		gui.setRootWidget(frame);

		Sounds.beep_popup.playEffect(1.5f, 0.2f, false);
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
	public void actionPerformed(Widget widget) {}


	@Override
	public void update() {
		super.update();

		if (doneWaiting && System.currentTimeMillis() > waitingStartTime + 700) {
			closePanel();
			listener.onCaptureInputDialogClosed(id, newTrigger);
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		if (!down) return;
		if (doneWaiting) return;

		if (key == Keyboard.KEY_ESCAPE) {
			closePanel();
			listener.onCaptureInputDialogClosed(id, null);
			return;
		}

		if (isStatic) {
			newTrigger = new TriggerBundle(EInput.KEY_DOWN, key);
		} else {
			newTrigger = new TriggerBundle(EInput.KEY_PRESS, key);
		}

		doneWaiting = true;
		waitingStartTime = System.currentTimeMillis();
		txWaiting.setText(newTrigger.getLabel(false));
	}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		if (!down) return;
		if (button > 1) return;
		if (doneWaiting) return;

		if (isStatic) {
			newTrigger = new TriggerBundle(EInput.BTN_DOWN, button);
		} else {
			newTrigger = new TriggerBundle(EInput.BTN_PRESS, button);
		}

		doneWaiting = true;
		waitingStartTime = System.currentTimeMillis();
		txWaiting.setText(newTrigger.getLabel(false));
	}
}
