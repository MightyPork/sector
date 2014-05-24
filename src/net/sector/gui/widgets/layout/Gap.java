package net.sector.gui.widgets.layout;


import net.sector.gui.widgets.Widget;

import com.porcupine.coord.Coord;


/**
 * Space
 * 
 * @author MightyPork
 */
public class Gap extends Widget {

	/**
	 * Gui space (gap)
	 * 
	 * @param width min width
	 * @param height min height
	 */
	public Gap(int width, int height) {
		setMargins(0, 0, 0, 0);
		setMinSize(width, height);
	}

	@Override
	public void render(Coord mouse) {}

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
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
