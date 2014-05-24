package net.sector.gui.widgets.layout;


import static net.sector.util.Align.*;
import net.sector.gui.widgets.Widget;

import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Vertical layout widget
 * 
 * @author MightyPork
 */
public class LayoutV extends LayoutBase {

	/** horizontal align */
	private int alignH = CENTER;

	/**
	 * new Vertical layout widget
	 * 
	 * @param alignH horizontal align: -1 left, 0 center, 1 right
	 */
	public LayoutV(int alignH) {
		this.alignH = alignH;
	}

	@Override
	public void calcChildSizes() {

		double lastMargin = 0;
		double totalSize = 0;
		double maxHorizontalSize = 0;

		// measure max width for alignment.
		for (Widget child : children) {
			child.calcChildSizes();
			maxHorizontalSize = Math.max(maxHorizontalSize, child.getSize().x + child.getMargins().left + child.getMargins().right);
		}

		maxHorizontalSize = Math.max(maxHorizontalSize, minSize.x);

		// generate rects
		boolean first = true;
		for (int i = children.size() - 1; i >= 0; i--) {
			Widget child = children.get(i);
			// add whats required by margins.
			if (!first) {
				totalSize += Calc.max(lastMargin, child.getMargins().bottom);
			}
			first = false;
			switch (alignH) {
				case LEFT:
					child.rect.add_ip(new Vec(0, totalSize));
					break;
				case CENTER:
					child.rect.add_ip(new Vec((maxHorizontalSize - child.getSize().x) / 2, totalSize));
					break;
				case RIGHT:
					child.rect.add_ip(new Vec((maxHorizontalSize - child.getSize().x - child.getMargins().right), totalSize));
			}

			totalSize += child.getSize().y;
			lastMargin = child.getMargins().top;
		}

		if (Math.round(totalSize) % 2 == 1) {
			totalSize += 1;
		}

		if (Math.round(maxHorizontalSize) % 2 == 1) {
			maxHorizontalSize += 1;
		}

		this.rect.setTo(0, 0, (int) Math.round(Math.max(minSize.x, maxHorizontalSize)), (int) Math.round(Math.max(minSize.y, totalSize)));
		if (minSize.y > totalSize) {
			for (Widget child : children) {
				child.getRect().add_ip(0, minSize.y - totalSize);
			}
		}
	}

}
