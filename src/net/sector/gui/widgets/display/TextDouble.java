package net.sector.gui.widgets.display;


import net.sector.fonts.FontManager;
import net.sector.gui.widgets.Widget;

import org.lwjgl.opengl.GL11;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Passive text label
 * 
 * @author MightyPork
 */
public class TextDouble extends Widget {

	private String fontLeft = "default";
	private String fontRight = "default";

	private String textLeft = "";
	private String textRight = "";

	/** Text color left */
	public RGB colorLeft = new RGB(1, 1, 1);
	/** Text color right */
	public RGB colorRight = new RGB(0, 1, 0);

	/**
	 * new text
	 * 
	 * @param width min width
	 */
	public TextDouble(int width) {
		setMargins(2, 0, 2, 0);
		setMinWidth(width);
	}

	/**
	 * Set text fonts
	 * 
	 * @param fleft left font
	 * @param fright right font
	 * @return this
	 */
	public TextDouble setFonts(String fleft, String fright) {
		fontLeft = fleft;
		fontRight = fright;
		return this;
	}

	public TextDouble setTextLeft(String text, RGB color) {
		textLeft = text;
		colorLeft = color;
		return this;
	}

	public TextDouble setTextRight(String text, RGB color) {
		textRight = text;
		colorRight = color;
		return this;
	}

	@Override
	public void calcChildSizes() {
		Coord oldms = getMinSize().copy();
		setMinSize(new Coord(oldms.x, Math.max(FontManager.height(fontLeft), FontManager.height(fontRight))));
		if (minSize.x < oldms.x) minSize.x = oldms.x;
		if (minSize.y < oldms.y) minSize.y = oldms.y;
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

	@Override
	public void onBlur() {}

	@Override
	public Widget onKey(int key, char chr, boolean down) {
		return null;
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
	public void render(Coord mouse) {
		if (!isVisible()) return;

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Coord leftP;
		leftP = rect.getCenter();
		leftP.x = rect.getMin().x;

		Coord rightP;

		rightP = rect.getCenter();
		rightP.x = rect.getMax().x;

		leftP.sub_ip(2, FontManager.height(fontLeft) / 2);
		FontManager.draw(leftP, textLeft, fontLeft, colorLeft, -1);

		rightP.sub_ip(2, FontManager.height(fontRight) / 2);
		FontManager.draw(rightP, textRight, fontRight, colorRight, 1);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public void eraseTexts() {
		textLeft = "";
		textRight = "";
	}

	public TextDouble setTexts(String left, String right) {
		this.textLeft = left;
		this.textRight = right;
		return this;
	}

	public TextDouble setColors(RGB left, RGB right) {
		this.colorLeft = left;
		this.colorRight = right;
		return this;
	}
}
