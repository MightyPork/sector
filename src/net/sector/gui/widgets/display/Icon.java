package net.sector.gui.widgets.display;


import static org.lwjgl.opengl.GL11.*;
import net.sector.gui.widgets.Widget;
import net.sector.textures.TextureManager;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;


/**
 * Icon 32x32 from texture sheet
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Icon extends Widget {

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
	public Icon setColor(RGB tint) {
		colorTint.setTo(tint);
		return this;
	}


	public String tooltipText = "";
	public RGB tooltipColor = RGB.WHITE;
	private long mouseEnterTime = 0;


	/**
	 * new icon
	 * 
	 * @param texture texture name
	 * @param tileX tile x coord
	 * @param tileY tile y coord
	 */
	public Icon(String texture, int tileX, int tileY) {
		this.texture = texture;
		this.tile = new CoordI(tileX, tileY);
		setMargins(2, 2, 2, 2);
	}


	/**
	 * Set tooltip
	 * 
	 * @param text text
	 * @param color render color
	 * @return this
	 */
	public Icon setTooltip(String text, RGB color) {
		tooltipColor = color;
		tooltipText = text;
		return this;
	}


	public Icon setTooltip(String text) {
		tooltipColor = RGB.WHITE.copy();
		tooltipText = text;
		return this;
	}


	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

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

		int w = 32;
		int h = 32;

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


		boolean onTop = isPanelOnTop();
		boolean hover = isMouseOver(mouse);

		if (enabled && hover && onTop && tooltipText.length() > 0) {
			if (System.currentTimeMillis() - mouseEnterTime > 1000) {
				renderTooltip(tooltipText, tooltipColor);
			}
		} else {
			mouseEnterTime = System.currentTimeMillis();
		}
	}

	@Override
	public void calcChildSizes() {
		setMinSize(new Coord(32, 32));
		rect.setTo(0, 0, minSize.x, minSize.y);
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

}
