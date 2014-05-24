package net.sector.gui.widgets.menu;


import static net.sector.gui.widgets.EColorRole.*;
import net.sector.fonts.FontManager;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.util.Align;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Passive label
 * 
 * @author MightyPork
 */
public class MenuTitle extends Widget {

	/**
	 * Gui label
	 * 
	 * @param text widget text
	 * @param font render font
	 */
	public MenuTitle(String text, String font) {
		setText(text);
		setFont(font);
		setTheme(ETheme.MENU_TITLE);
	}

	/**
	 * Gui label menu_title
	 * 
	 * @param text widget text
	 */
	public MenuTitle(String text) {
		this(text, "menu_title");
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;


		RGB color = getColor(FG, mouse);
		RGB blur = getColor(SHADOW, mouse);

		FontManager.drawFuzzy(rect.getCenterDown(), text, font, blur, color, 1, Align.CENTER);

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
		//FontManager.setFullscreenDoubleSize(true);
		setMinSize((int) FontManager.width(font, text), (int) FontManager.height(font));
		rect.setTo(0, 0, minSize.x, minSize.y);
		//FontManager.setFullscreenDoubleSize(false);
	}

}
