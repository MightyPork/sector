package net.sector.gui.widgets.input;


import static org.lwjgl.opengl.GL11.*;
import net.sector.textures.TextureManager;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;


/**
 * Button with icon
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ButtonIcon extends Button {

	private CoordI tile = null;
	private String texture = null;

	/** Tint color */
	public RGB colorTint = RGB.WHITE.copy();

	/**
	 * Set color tint
	 * 
	 * @param tint color tint
	 * @return this
	 */
	public ButtonIcon setColor(RGB tint) {
		colorTint.setTo(tint);
		return this;
	}

	/**
	 * new button
	 * 
	 * @param id widget id
	 * @param texture texture identifier
	 * @param tileX tile x coord
	 * @param tileY tile y coord
	 */
	public ButtonIcon(int id, String texture, int tileX, int tileY) {
		super(id);
		setId(id);
		this.texture = texture;
		this.tile = new CoordI(tileX, tileY);
		setPadding(2, 2);
		setMargins(2, 2, 2, 2);
		sndClick = true;
	}


	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;
		renderBase(mouse);

		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		TextureManager.bind(texture);

		RenderUtils.setColor(new RGB(colorTint, enabled ? 1 : 0.3));


		glBegin(GL_QUADS);

		double left = (tile.x) * 0.125;
		double top = (tile.y) * 0.125;
		double right = (tile.x + 1) * 0.125;
		double bottom = (tile.y + 1) * 0.125;

		double w = 32;
		double h = 32;

		Coord c = getRect().getCenter();

		double x1 = c.x - w / 2;
		double y1 = c.y + h / 2;
		double x2 = c.x + w / 2;
		double y2 = c.y - h / 2;


		glTexCoord2d(left, top);
		glVertex3d(x1, y1, 0);

		glTexCoord2d(right, top);
		glVertex3d(x2, y1, 0);

		glTexCoord2d(right, bottom);
		glVertex3d(x2, y2, 0);

		glTexCoord2d(left, bottom);
		glVertex3d(x1, y2, 0);

		glEnd();

		RenderUtils.setColor(RGB.WHITE);
		TextureManager.unbind();

		glPopAttrib();
		glPopMatrix();
	}

	@Override
	public void calcChildSizes() {
		Coord oldms = getMinSize().copy();
		setMinSize(new Coord(32 + borderWidth * 2 + paddingX * 2, 32 + borderWidth * 2 + paddingY * 2));
		if (minSize.x < oldms.x) minSize.x = oldms.x;
		if (minSize.y < oldms.y) minSize.y = oldms.y;
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
