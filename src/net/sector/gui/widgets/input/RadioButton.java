package net.sector.gui.widgets.input;


import static net.sector.gui.widgets.EColorRole.*;

import java.util.HashSet;

import net.sector.util.RenderUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Rect;


/**
 * Radio button.
 * 
 * @author MightyPork
 */
public class RadioButton extends Checkbox {

	/**
	 * Radio group.
	 * 
	 * @author MightyPork
	 */
	public static class RadioGroup extends HashSet<RadioButton> {}

	/**
	 * Build new radiobutton group.
	 * 
	 * @return group list.
	 */
	public static RadioGroup newGroup() {
		return new RadioGroup();
	}

	private RadioGroup group = null;

	/**
	 * Assign radio button group
	 * 
	 * @param groupList group list
	 * @return this
	 */
	public RadioButton setGroup(RadioGroup groupList) {
		this.group = groupList;
		group.add(this);
		return this;
	}

	/**
	 * Radio button
	 * 
	 * @param id widget ID
	 * @param text
	 */
	public RadioButton(int id, String text) {
		super(id, text);
	}

	/**
	 * Radio button
	 * 
	 * @param id widget id
	 * @param text widget text
	 * @param font render font
	 */
	public RadioButton(int id, String text, String font) {
		super(id, text, font);
	}

	@Override
	public Checkbox setChecked(boolean checked) {
		if (checked == false) return this; // no unchecking here.
		for (RadioButton rb : group)
			rb.setChecked_do(false);
		setChecked_do(true);
		return this;
	}


	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		renderBase(mouse);

		if (checked) {
			Rect box = getBoxRect();

			RenderUtils.setColor(getColor(FG, mouse));
			RenderUtils.quadSize(box.getMin().x + markDist, box.getMin().y + markDist, frameSize - markDist * 2, frameSize - markDist * 2);
		}
	}
}
