package net.sector.gui.widgets.layout;


import static net.sector.util.Align.*;
import net.sector.gui.widgets.Widget;

import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Horizontal layout widget
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class LayoutH extends LayoutBase {

	/** vertical align */
	private int alignV = CENTER;

	/**
	 * New horizontal layout widget
	 * 
	 * @param alignV vertical align. -1 bottom, 0 center, 1 top.
	 */
	public LayoutH(int alignV) {
		this.alignV = alignV;
	}

	@Override
	public void calcChildSizes() {
		double lastMargin = 0;
		double totalSize = 0;
		double maxVerticalSize = 0;

		// measure max width for alignment.
		for (Widget child : children) {
			child.calcChildSizes();
			maxVerticalSize = Math.max(maxVerticalSize, child.getSize().y + child.getMargins().top + child.getMargins().bottom);
		}

		boolean first = true;
		// generate rects
		for (Widget child : children) {
			// add whats required by margins.
			if (!first && child.getSize().x > 0) {
				totalSize += Calc.max(lastMargin, child.getMargins().left);
			}

			first = false;
			switch (alignV) {
				case BOTTOM:
					child.rect.add_ip(new Vec(totalSize, child.getMargins().bottom));
					break;
				case CENTER:
					child.rect.add_ip(new Vec(totalSize, (maxVerticalSize - child.getSize().y) / 2));
					break;
				case TOP:
					child.rect.add_ip(new Vec(totalSize, (maxVerticalSize - child.getSize().y - child.getMargins().top)));
			}

			totalSize += child.getSize().x;
			lastMargin = child.getMargins().right;
		}

		if (Math.round(totalSize) % 2 == 1) {
			totalSize += 1;
		}

		if (Math.round(maxVerticalSize) % 2 == 1) {
			maxVerticalSize += 1;
		}

		this.rect.setTo(0, 0, Math.max(minSize.x, totalSize), Math.max(minSize.y, maxVerticalSize));

//		if(minSize.x>totalSize) {
//			for (Widget child : children) {
//				child.getRect().add_ip(minSize.x-totalSize, 0);
//			}
//		}
	}

}
