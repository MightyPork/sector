package net.sector.gui.widgets.display;


import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


public class TextWithBackground extends Text {

	private RGB bgColor = new RGB(0x479EF5);

	public TextWithBackground setBackgroundColor(RGB color) {
		this.bgColor = color;
		return this;
	}

	public TextWithBackground(String text) {
		super(text);
	}

	public TextWithBackground(String text, String font) {
		super(text, font);
	}

	@Override
	public void render(Coord mouse) {

		boolean over = isMouseOver(mouse);

		RGB color = new RGB(bgColor, 0.3);

		if (over) {
			color = new RGB(bgColor, 0.9);
		}

		RenderUtils.setColor(color);
		RenderUtils.quadSize(rect.getMin().x, rect.getMin().y, rect.getSize().x, rect.getSize().y);

		RenderUtils.setColor(RGB.WHITE);

		super.render(mouse);
	}

}
