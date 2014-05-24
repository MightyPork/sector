package net.sector.gui.widgets.menu;


import static net.sector.gui.widgets.EColorRole.*;
import net.sector.fonts.FontManager;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.sounds.Sounds;
import net.sector.util.Align;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Clcikable button.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class MenuButton extends Widget {

	private boolean clicked = false;

	public String font = "menu";

	/**
	 * new button
	 * 
	 * @param id widget id
	 * @param text widget text
	 * @param font render font
	 */
	public MenuButton(int id, String text) {
		setId(id);
		setText(text);
		setTheme(ETheme.MENU_BUTTON);
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		boolean hover = (isMouseOver(mouse) && isPanelOnTop());

		RGB color = getColor(FG, mouse);
		RGB blur = getColor(SHADOW, mouse);

		FontManager.drawFuzzy(rect.getCenterDown(), text, font, blur, color, hover ? 3 : 1, Align.CENTER);

	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (button != 0) return null;
		if (!isMouseOver(pos)) return null;
		if (down == true) {
			clicked = true;
		} else {
			if (clicked) {

				Sounds.beep_soft.playAsSoundEffect(1, 0.05f, false);

				// mouse down and up
				// click consumed, send event to listener
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
		//FontManager.setFullscreenDoubleSize(true);
		setMinSize(new Coord(FontManager.width(font, text), FontManager.height(font)));
		rect.setTo(0, 0, minSize.x, minSize.y);
		//FontManager.setFullscreenDoubleSize(false);
	}

}
