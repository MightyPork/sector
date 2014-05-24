package net.sector.gui.widgets.input;


import static net.sector.gui.widgets.EColorRole.*;
import static org.lwjgl.opengl.GL11.*;
import net.sector.fonts.FontManager;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Rect;


/**
 * Clcikable button.
 * 
 * @author MightyPork
 */
public class Checkbox extends Widget {

	public int frameSize = 25;
	public int borderWidth = 1;
	public int markDist = borderWidth + 6;
	public int txtDist = 5;

	public boolean checked = false;

	public Checkbox setChecked(boolean checked) {
		setChecked_do(checked);
		return this;
	}

	public void setChecked_do(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public Checkbox setFrameSize(int size) {
		frameSize = size;
		return this;
	}

	/**
	 * new ckbox
	 * 
	 * @param id widget id
	 * @param text widget text
	 * @param font render font
	 */
	public Checkbox(int id, String text, String font) {
		setId(id);
		setText(text);
		setFont(font);
		setTheme(ETheme.BLUE);
	}

	/**
	 * new ckbox
	 * 
	 * @param id widget id
	 * @param text widget text
	 */
	public Checkbox(int id, String text) {
		this(id, text, "small_menu");
	}

	protected Rect getBoxRect() {
		return new Rect(rect.getMin().x, rect.getMin().y, rect.getMin().x + frameSize, rect.getMin().y + frameSize).add_ip(0,
				(getSize().y - frameSize) / 2);
	}

	public void renderBase(Coord mouse) {
		if (!isVisible()) return;

		Rect box = getBoxRect();

		RGB bdc = getColor(BDR, mouse);
		RGB bgc = getColor(BG, mouse);

		RenderUtils.quadRectBorder(box, borderWidth, bdc, bgc);


		glEnable(GL_TEXTURE_2D);
		Coord txtCenterPos = rect.getMin().add(frameSize + txtDist, 0).add_ip(0, rect.getSize().y / 2 - FontManager.height(font) / 2 - 2);
		FontManager.draw(txtCenterPos, text, font, getColor(FG, mouse), Align.LEFT);
		glDisable(GL_TEXTURE_2D);
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		renderBase(mouse);

		if (checked) {
			Rect box = getBoxRect();

			RenderUtils.setColor(getColor(FG, mouse));
			glLineWidth(2);
			glBegin(GL_LINES);
			glVertex2d(box.getMin().x + markDist, box.getMin().y + markDist);
			glVertex2d(box.getMin().x + frameSize - markDist, box.getMin().y + frameSize - markDist);
			glVertex2d(box.getMin().x + markDist, box.getMin().y + frameSize - markDist);
			glVertex2d(box.getMin().x + frameSize - markDist, box.getMin().y + markDist);
			glEnd();
		}
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (button != 0) return null;
		if (!isMouseOver(pos)) {
			clicked = false;
			return null;
		}
		if (down == true) {
			clicked = true;
		} else {
			if (clicked) {
				Sounds.click2.playEffect(1, 0.5f, false);
				clicked = false;
				setChecked(!isChecked());
				return this;
			}
		}
		return null;
	}

	@Override
	public void onBlur() {
		clicked = false;
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
		Coord oldms = getMinSize().copy();
		setMinSize(new Coord(FontManager.width(font, text) + frameSize + this.txtDist, FontManager.height(font)));
		if (minSize.x < oldms.x) minSize.x = oldms.x;
		if (minSize.y < oldms.y) minSize.y = oldms.y;
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
