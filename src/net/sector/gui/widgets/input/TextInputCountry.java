package net.sector.gui.widgets.input;


import net.sector.gui.panels.profiles.PanelSelectCountry;
import net.sector.gui.widgets.Widget;
import net.sector.network.CountryList;

import com.porcupine.coord.Coord;


/**
 * Text input for countries
 * 
 * @author MightyPork
 */
public class TextInputCountry extends TextInput {

	/**
	 * Country text input
	 * 
	 * @param id ID
	 * @param text text
	 * @param font font
	 */
	public TextInputCountry(int id, String text, String font) {
		super(id, text, font);
		this.text = "Click to select...";
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (isMouseOver(pos) && down) clicked = true;
		if (!down) {
			if (clicked && isMouseOver(pos)) {
				clicked = false;
				getPanel().openPanel(new PanelSelectCountry(getPanel().screen, this));
				return this;
			}
			clicked = false;
		}
		return null;
	}

	/**
	 * Set country code
	 * 
	 * @param code country code
	 * @return this
	 */
	public TextInputCountry setCountry(String code) {

		if (code.equals("")) {
			setText("Click to select...");
		} else {
			String name = CountryList.getName(code);
			setText(name);
		}

		setTag(code);
		return this;
	}

	@Override
	public Widget onKey(int key, char chr, boolean down) {
		return null;
	}

}
