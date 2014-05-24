package net.sector.gui.widgets.composite;


import net.sector.gui.panels.HsColors;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.display.TextDouble;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.util.Align;

import com.porcupine.color.RGB;
import com.porcupine.util.StringUtils;


public class ListItemHighscore extends LayoutH {


	public TextDouble dt;

	public ListItemHighscore(int place, String name, int score, boolean user, boolean active) {
		super(Align.CENTER);

		Text t;
		add(t = new Text(place + ".", "small_text"));
		t.setColorText(new RGB(0.6, 0.6, 1));
		t.setTextAlign(Align.RIGHT);
		t.setMinWidth(40);
		t.setMarginsH(2, 5);

		add(dt = new TextDouble(390));
		dt.setFonts("small_text", "small_text");
		dt.setTexts(name, StringUtils.formatInt(score));

		RGB left = RGB.WHITE;
		if (user && !active) left = HsColors.USER;
		if (user && active) left = HsColors.USER_ACTIVE;
		if (!user && !active) left = HsColors.GUEST;
		if (!user && active) left = HsColors.GUEST_ACTIVE;

		dt.setColors(left, HsColors.SCORE);

		calcChildSizes();
	}

}
