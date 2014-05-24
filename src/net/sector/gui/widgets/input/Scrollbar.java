package net.sector.gui.widgets.input;


import static net.sector.gui.widgets.EColorRole.*;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.IScrollable;
import net.sector.gui.widgets.Widget;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


/**
 * Clcikable button.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Scrollbar extends Widget {

	private int bdr = 2;
	private int ingap = 1;

	private double value = 0;

	private IScrollable scrollable;

	/**
	 * new scrollbar
	 * 
	 * @param width scrollbar width
	 * @param height scrollbar height
	 */
	public Scrollbar(int width, int height) {
		setMinSize(width, height);
		setTheme(ETheme.BLUE_SCROLLBAR);
	}

	/**
	 * Set scrollable element
	 * 
	 * @param scrollable scrollable element
	 * @return this
	 */
	public Scrollbar setScrollable(IScrollable scrollable) {
		this.scrollable = scrollable;
		scrollable.onScrollbarConnected(this);
		return this;
	}

	private double getContentHeight() {
		return scrollable.getContentHeight();
	}

	private double getViewHeight() {
		return scrollable.getViewHeight();
	}

	private double getGrooveHeight() {
		return getSize().y - bdr * 2 - ingap * 2;
	}

	private double getHandleHeight() {
		double h = (getViewHeight() / getContentHeight()) * getGrooveHeight();
		if (h < 20) h = 20;
		if (h > getGrooveHeight()) h = getGrooveHeight();
		return h;
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		RGB borderC = getColor(BDR, mouse);
		RGB fillC = getColor(BG, mouse);

		RenderUtils.quadRectBorder(rect, bdr, borderC, fillC);


		borderC = getColor(BDR_INNER, mouse);
		fillC = getColor(FG, mouse);

		double GH = getGrooveHeight();
		double HH = getHandleHeight();

		Coord min = new Coord(rect.getMin().x + bdr + ingap, rect.getMin().y + bdr + ingap + GH - (GH - HH) * value - HH);
		Coord max = min.add(rect.getSize().x - bdr * 2 - ingap * 2, getHandleHeight());


		RenderUtils.quadCoordBorder(min, max, 2, borderC, fillC);
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
		if (getContentHeight() <= getViewHeight()) return null;
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
			value = valueDragStart + (1 / (getGrooveHeight() - getHandleHeight())) * (posDragStart.y - pos.y);
			value = Calc.clampd(value, 0, 1);
			if (value != vlast) {
				scrollable.onScrollbarChange(value);
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
		if (getContentHeight() <= getViewHeight()) return null;
		double vlast = value;
		value = Calc.clampd(value - scroll * (getViewHeight() / getContentHeight()) * 0.2, 0, 1);
		if (value != vlast) scrollable.onScrollbarChange(value);
		return this;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = Calc.clampd(value, 0, 1);
	}

}
