package net.sector.gui.widgets.input;


import static net.sector.gui.widgets.EColorRole.*;
import net.sector.fonts.FontManager;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.input.Function;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import org.lwjgl.opengl.GL11;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Clickable button.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Button extends Widget {

	private Function<Boolean> onClickHandler;

	public boolean sndClick = false;

	public boolean lastRenderHover = false;

	public double borderWidth = 2;
	public boolean showBorder = true;
	public double paddingX = 10;
	public double paddingY = 4;

	public String tooltipText = "";
	public RGB tooltipColor = RGB.WHITE;


	public void addOnClickHandler(Function<Boolean> handler) {
		onClickHandler = handler;
	}

	public Button setPadding(double x, double y) {
		paddingX = x;
		paddingY = y;
		return this;
	}

	public Button setBorderSize(double width) {
		borderWidth = width;
		showBorder = true;
		return this;
	}

	public Button enableBorder(boolean flag) {
		showBorder = flag;
		if (!flag) borderWidth = 0;
		return this;
	}

	/**
	 * new button
	 * 
	 * @param id widget id
	 * @param text widget text
	 * @param font render font
	 */
	public Button(int id, String text, String font) {
		setId(id);
		setText(text);
		setFont(font);
		setTheme(ETheme.BLUE);
	}

	/**
	 * new button
	 * 
	 * @param id widget id
	 * @param text widget text
	 */
	public Button(int id, String text) {
		this(id, text, "small_menu");
	}


	/**
	 * new button
	 * 
	 * @param id widget id
	 */
	public Button(int id) {
		setId(id);
	}

	/**
	 * Set tooltip
	 * 
	 * @param text text
	 * @param color render color
	 * @return this
	 */
	public Button setTooltip(String text, RGB color) {
		tooltipColor = color;
		tooltipText = text;
		return this;
	}


	public Button setTooltip(String text) {
		tooltipColor = RGB.WHITE.copy();
		tooltipText = text;
		return this;
	}


	/** Borders - L,R,T,B */
	public boolean bdrs[] = { true, true, true, true };

	private long mouseEnterTime = 0;


	protected void renderBase(Coord mouse) {
		if (!isVisible()) return;
		boolean onTop = isPanelOnTop();

		boolean hover = isMouseOver(mouse);

		if (showBorder) {
			RenderUtils.setColor(getColor(BDR, mouse));

			//LTTTTR
			//L    R
			//L    R
			//LBBBBR
			// left
			if (bdrs[0]) RenderUtils.quadCoord(rect.getMin().x, rect.getMin().y, rect.getMin().x + borderWidth, rect.getMax().y);
			// right
			if (bdrs[1]) RenderUtils.quadCoord(rect.getMax().x - borderWidth, rect.getMin().y, rect.getMax().x, rect.getMax().y);
			// top
			if (bdrs[2])
				RenderUtils.quadCoord(rect.getMin().x + borderWidth * (bdrs[0] ? 1 : 0), rect.getMax().y - borderWidth, rect.getMax().x - borderWidth
						* (bdrs[1] ? 1 : 0), rect.getMax().y);
			// bottom
			if (bdrs[3])
				RenderUtils.quadCoord(rect.getMin().x + borderWidth * (bdrs[0] ? 1 : 0), rect.getMin().y, rect.getMax().x - borderWidth
						* (bdrs[1] ? 1 : 0), rect.getMin().y + borderWidth);

			RenderUtils.setColor(getColor(BG, mouse));
			double left = rect.getMin().x + borderWidth * (bdrs[0] ? 1 : 0);
			double bottom = rect.getMin().y + borderWidth * (bdrs[3] ? 1 : 0);
			double right = rect.getMax().x - borderWidth * (bdrs[1] ? 1 : 0);
			double top = rect.getMax().y - borderWidth * (bdrs[2] ? 1 : 0);
			RenderUtils.quadCoord(left, bottom, right, top);
		} else {
			RenderUtils.setColor(getColor(BG, mouse));
			RenderUtils.quadCoord(rect.getMin().x, rect.getMin().y, rect.getMax().x, rect.getMax().y);
		}

		if (enabled && hover && onTop && tooltipText.length() > 0) {
			if (System.currentTimeMillis() - mouseEnterTime > 1000) {
				renderTooltip(tooltipText, tooltipColor);
			}
		} else {
			mouseEnterTime = System.currentTimeMillis();
		}

		lastRenderHover = hover;
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;
		renderBase(mouse);

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		Coord txtCenterPos = rect.getCenter().sub(3, FontManager.height(font) / 2 + 2);
		FontManager.draw(txtCenterPos, text, font, getColor(FG, mouse), Align.CENTER);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
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
				if (sndClick) {
					Sounds.click1.playEffect(1.6f, 0.3f, false);
				} else {
					Sounds.beep_soft_short.playEffect(1, 0.6f, false);
				}
				clicked = false;

				if (onClickHandler != null) return onClickHandler.run() ? this : null;

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
		setMinSize(new Coord(FontManager.width(font, text) + borderWidth * 2 + paddingX * 2, FontManager.height(font) + borderWidth * 2 + paddingY
				* 2));
		if (minSize.x < oldms.x) minSize.x = oldms.x;
		if (minSize.y < oldms.y) minSize.y = oldms.y;
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
