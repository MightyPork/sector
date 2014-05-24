package net.sector.gui.panels;


import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.GuiRoot.EventListener;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.layout.LayoutV;

import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


public abstract class PanelGui extends Panel implements EventListener {

	protected GuiRoot gui;
	private LayoutV v;

	public PanelGui(Screen screen) {
		super(screen);
		gui = new GuiRoot(this, null);
	}


	@Override
	public final void onCreate() {
		initGui(gui);
		gui.setParentPanel(this);
		gui.updatePositions();
		onPostInit();
	}

	/**
	 * Called after panel is fully created
	 */
	public void onPostInit() {}

	@Override
	public void onClose() {}

	public final void updateWidgetPositions() {
		gui.updatePositions();
	}

	public final GuiRoot getRootWidget() {
		return gui;
	}

	public abstract void initGui(GuiRoot gui);

	@Override
	public void onWindowChanged() {
		gui.updatePositions();
	}

	@Override
	public boolean hasBackgroundLayer() {
		return true;
	}

	@Override
	public void update() {}

	@Override
	protected void renderPanel() {
		gui.render();
	}

	@Override
	public abstract void actionPerformed(Widget widget);

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		if (button != -1) gui.onMouseButton(button, down);
		if (wheelDelta != 0) gui.onScroll(Calc.clampi(wheelDelta, -1, 1));
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		gui.onKeyDown(key, c, down);
	}

	@Override
	public void handleStaticInputs() {
		gui.handleStaticInputs();
	}

}
