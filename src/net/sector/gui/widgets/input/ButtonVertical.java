package net.sector.gui.widgets.input;


import static net.sector.gui.widgets.EColorRole.*;
import net.sector.fonts.FontManager;
import net.sector.util.Align;

import org.lwjgl.opengl.GL11;

import com.porcupine.coord.Coord;


/**
 * Vertical (left tab) button
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ButtonVertical extends Button {

	/**
	 * new button v
	 * 
	 * @param id widget id
	 * @param text widget text
	 * @param font render font
	 */
	public ButtonVertical(int id, String text, String font) {
		super(id, text, font);
	}

	/**
	 * new button v
	 * 
	 * @param id widget id
	 * @param text widget text
	 */
	public ButtonVertical(int id, String text) {
		super(id, text);
	}


	/**
	 * new button v
	 * 
	 * @param id widget id
	 */
	public ButtonVertical(int id) {
		super(id);
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;
		renderBase(mouse);

		GL11.glPushMatrix();

		Coord txtCenterPos = rect.getCenter().add(FontManager.height(font) / 2 + 2, -3);

		GL11.glTranslated(txtCenterPos.x, txtCenterPos.y, 0);

		GL11.glRotated(90, 0, 0, 1);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		FontManager.draw(Coord.ZERO, text, font, getColor(FG, mouse), Align.CENTER);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glPopMatrix();
	}

	@Override
	public void calcChildSizes() {
		Coord oldms = getMinSize().copy();
		double w = FontManager.height(font) + borderWidth * 2 + paddingX * 2;
		double h = FontManager.width(font, text) + borderWidth * 2 + paddingY * 2;
		setMinSize(new Coord(w, h));
		if (minSize.x < oldms.x) minSize.x = oldms.x;
		if (minSize.y < oldms.y) minSize.y = oldms.y;
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
