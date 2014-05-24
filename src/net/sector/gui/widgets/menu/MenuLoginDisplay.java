package net.sector.gui.widgets.menu;


import net.sector.App;
import net.sector.fonts.FontManager;
import net.sector.gui.widgets.Widget;
import net.sector.level.SuperContext;
import net.sector.sounds.Sounds;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


public class MenuLoginDisplay extends Widget {

	public MenuLoginDisplay(int id) {
		setMinSize(330, 100);
		setId(id);
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		boolean hover = (isMouseOver(mouse) && isPanelOnTop());

		// line
		int bdr = 2;

		RGB side = new RGB(0x029BFA, 0);
		RGB center = new RGB(0x02D9FA, 1);

		RenderUtils.quadCoordGradHBilinear(rect.getMin().x, rect.getMax().y - bdr, rect.getMax().x, rect.getMax().y, side, center);

		RenderUtils.quadCoordGradHBilinear(rect.getMin().x, rect.getMin().y, rect.getMax().x, rect.getMin().y + bdr, side, center);

		String font1 = "login_display1";
		String font2 = "login_display2";



		Coord pos = rect.getCenterTop();

		RGB color = hover ? new RGB(0x02E9FA) : new RGB(0x0B94E3);
		RGB blur = hover ? new RGB(0x00A8FC, 0.05) : new RGB(0x003185, 0.05);

		if (App.offlineMode) {

			pos.y -= rect.getSize().y / 2;
			pos.y -= FontManager.height(font2) / 2;

			FontManager.drawFuzzy(pos, "No Connection!", font2, new RGB(0xff0000, 0.04), new RGB(0xff0000), 2, Align.CENTER);

		} else if (SuperContext.selectedUser == null) {

			pos.y -= rect.getSize().y / 2;
			pos.y -= FontManager.height(font2) / 2;

			FontManager.drawFuzzy(pos, "Click to log in.", font2, blur, color, hover ? 3 : 2, Align.CENTER);

		} else {
			pos.y -= rect.getSize().y / 3;

			String s1 = "Logged in as ";
			String s2 = SuperContext.selectedUser.uname.trim();

			double totalWidth = FontManager.width(font1, s1 + s2);
			double w1 = FontManager.width(font1, s1);

			RGB c1main = new RGB(0x0B74E3);
			RGB c1blur = new RGB(0x003185, 0.2);

			RGB c2main = new RGB(0x0CADED);
			RGB c2blur = new RGB(0x0C62A8, 0.1);

			FontManager.drawFuzzy(pos.sub(totalWidth / 2, FontManager.height(font1) / 2), s1, font1, c1blur, c1main, 1, Align.LEFT);
			FontManager.drawFuzzy(pos.sub(totalWidth / 2 - w1, FontManager.height(font1) / 2), s2, font2, c2blur, c2main, 2, Align.LEFT);

			pos.y -= rect.getSize().y / 3;
			FontManager.drawFuzzy(pos.sub(0, FontManager.height(font2) / 2), "Click to change.", font2, blur, color, hover ? 3 : 2, Align.CENTER);
		}



	}

	private boolean clicked = false;

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (App.offlineMode) return null;
		if (button != 0) return null;
		if (!isMouseOver(pos)) return null;
		if (down == true) {
			clicked = true;
		} else {
			if (clicked) {
				Sounds.beep_soft.playAsSoundEffect(1, 0.05f, false);
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
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
