package net.sector.gui.widgets.input;


import static net.sector.gui.widgets.EColorRole.*;
import net.sector.gui.panels.PanelGui;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


/**
 * Clcikable button.
 * 
 * @author MightyPork
 */
public class Slider extends Widget {

	private int bdr = 2;

	private double value = 0;
	private int handleW = 22;
	private int handleH = 22;
	private int grooveH = 6;
	private int ingapH = 2 + handleH / 2;

	/**
	 * new scrollbar
	 * 
	 * @param width scrollbar width
	 * @param value slider value
	 */
	public Slider(int width, double value) {
		setMinSize(width, handleH + 4);
		setValue(value);
		setTheme(ETheme.BLUE_SCROLLBAR);
	}


	private double getGrooveWidth() {
		return getSize().x - bdr * 2 - ingapH * 2;
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		RGB borderC = getColor(BDR, mouse);
		RGB fillC = getColor(BG, mouse);

		Coord grooveMin = rect.getCenterLeft().sub(0, grooveH / 2);
		Coord grooveMax = rect.getCenterRight().add(0, grooveH / 2);

		RenderUtils.quadCoordBorder(grooveMin, grooveMax, bdr, borderC, fillC);

		borderC = getColor(BDR_INNER, mouse);
		fillC = getColor(FG, mouse);

		double GW = getGrooveWidth();
		double HW = handleW;
		double HH = handleH;

		Coord handleMin = new Coord(rect.getMin().x + bdr + ingapH + (GW) * value - (HW / 2), rect.getCenter().y - HH / 2);
		Coord handleMax = handleMin.add(HW, HH);

		RenderUtils.quadCoordBorder(handleMin, handleMax, 2, borderC, fillC);
	}

	private boolean isDragging = false;
	private Coord posDragStart;
	private double valueDragStart = 0;

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (button != 0) return null;
		if (!isDragging && !isMouseOver(pos)) {

			return null;
		}

		if (down == true) {
			isDragging = true;
			posDragStart = pos;
			valueDragStart = value;
		} else {
			// up.
			isDragging = false;
		}
		return null;
	}

	@Override
	public void handleStaticInputs(Coord pos) {
		if (isDragging) {
			double vlast = value;
			value = valueDragStart - (1 / getGrooveWidth()) * (posDragStart.x - pos.x);
			value = Calc.clampd(value, 0, 1);
			if (value != vlast) {
				((PanelGui) getPanel()).actionPerformed(this);
			}
		}
	}

	@Override
	public void onBlur() {

	}

	@Override
	public Widget onScroll(Coord pos, int scroll) {
		if (isMouseOver(pos)) {
			return onScrollDelegate(scroll);
		}
		return null;
	}

	@Override
	public Widget onKey(int key, char chr, boolean down) {
		return null;
	}

	@Override
	public void calcChildSizes() {
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

	public Widget onScrollDelegate(int scroll) {
		double vlast = value;
		value = Calc.clampd(value + scroll * 0.1, 0, 1);
		if (value != vlast) ((PanelGui) getPanel()).actionPerformed(this);
		return this;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = Calc.clampd(value, 0, 1);
	}

}
