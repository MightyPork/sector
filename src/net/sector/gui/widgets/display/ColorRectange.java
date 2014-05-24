package net.sector.gui.widgets.display;


import net.sector.gui.widgets.Widget;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Space
 * 
 * @author MightyPork
 */
public class ColorRectange extends Widget {

	/**
	 * Colour rectange
	 * 
	 * @param width min width
	 * @param height min height
	 * @param margin widget margin
	 * @param border widget border (bevel in rectangle)
	 * @param color color (referenced!)
	 */
	public ColorRectange(int width, int height, int margin, int border, RGB color) {
		setMargins(margin, margin, margin, margin);
		setMinSize(width, height);
		this.border = border;
		this.rectColor = color;
	}


	public String tooltipText = "";
	public RGB tooltipColor = RGB.WHITE;

	/**
	 * Set tooltip
	 * 
	 * @param text text
	 * @param color render color
	 * @return this
	 */
	public ColorRectange setTooltip(String text, RGB color) {
		tooltipColor = color;
		tooltipText = text;
		return this;
	}

	public int border = 3;
	public RGB rectColor = RGB.GREEN;

	public ColorRectange setColorRect(RGB newColor) {
		rectColor = newColor;
		return this;
	}

	public ColorRectange setBorderWidth(int w) {
		border = w;
		return this;
	}

	private long mouseEnterTime = 0;

	@Override
	public void render(Coord mouse) {
		RenderUtils.quadRectOutset(rect, border, rectColor, false);

		boolean hover = isMouseOver(mouse);

		if (enabled && hover && tooltipText.length() > 0) {
			if (System.currentTimeMillis() - mouseEnterTime > 1000) {
				renderTooltip(tooltipText, tooltipColor);
			}
		} else {
			mouseEnterTime = System.currentTimeMillis();
		}
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		return null;
	}


	@Override
	public Widget onScroll(Coord pos, int scroll) {
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

}
