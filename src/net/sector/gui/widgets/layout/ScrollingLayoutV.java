package net.sector.gui.widgets.layout;


import java.util.ArrayList;

import net.sector.gui.widgets.IScrollable;
import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.WidgetMargins;
import net.sector.gui.widgets.input.Scrollbar;
import net.sector.util.Align;
import net.sector.util.Log;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Rect;


public class ScrollingLayoutV extends LayoutV implements IScrollable {


	private Scrollbar scrollbar;

	private ArrayList<ChildPos> positions;

	/**
	 * Create scrolling layout V.<br>
	 * After creating, this layout must be filled with at least the minimal
	 * amount of children to stretch it accordingly.<br>
	 * It does not work well with children of different sizes.
	 * 
	 * @param childrenShown
	 */
	public ScrollingLayoutV(int childrenShown, IWidgetFactory fakeWidgetFactory) {
		super(Align.LEFT);
		this.childrenShown = childrenShown;

		for (int i = 1; i <= childrenShown; i++)
			addChild(fakeWidgetFactory.getWidget());
		rememberPositions();
		removeAll();
	}

	private class ChildPos {

		public ChildPos(Widget w) {
			this.rect = w.rect.copy();
			this.margins = w.margins.copy();
			this.minSize = w.minSize.copy();
		}

		public void modify(Widget w) {
			w.rect.setTo(this.rect);
			w.margins.setTo(this.margins);
			w.minSize.setTo(this.minSize);
		}

		public Rect rect;
		public WidgetMargins margins;
		public Coord minSize;
	}

	private void rememberPositions() {
		calcChildSizes();
		positions = new ArrayList<ChildPos>(childrenShown);
		for (Widget ch : children) {
			if (ch == null) {
				Log.w("Null child in ScrollingLayoutV!");
			} else {
				positions.add(new ChildPos(ch));
			}
		}
		//Collections.reverse(positions);
		setMinSize(getSize());
	}

	private int childrenShown = 1;

	private ArrayList<Widget> allChildren = new ArrayList<Widget>();

	public void addChild(Widget widget) {
		allChildren.add(widget);
		if (children.size() < childrenShown) children.add(widget);
	}

	@Override
	@Deprecated
	public void add(Widget child) {}

	@Override
	public double getContentHeight() {
		return allChildren.size();
	}

	@Override
	public double getViewHeight() {
		return childrenShown;
	}

	@Override
	public void onScrollbarChange(double value) {
		int startEntry = (int) ((getContentHeight() - getViewHeight()) * value);

		children.clear();
		if (allChildren.size() < childrenShown) {
			for (int i = 0, c = 0; i < allChildren.size(); i++, c++) {
				try {
					Widget w;
					children.add(w = allChildren.get(i));
					w.setGuiRoot(getGuiRoot());
					positions.get(c).modify(w);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			for (int i = startEntry, c = 0; i < startEntry + childrenShown; i++, c++) {
				try {
					Widget w;
					children.add(w = allChildren.get(i));
					w.setGuiRoot(getGuiRoot());
					positions.get(c).modify(w);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		//calcChildSizes();
	}

	@Override
	public void removeAll() {
		children.clear();
		allChildren.clear();
	}

	@Override
	public Widget onScroll(Coord pos, int scroll) {
		return scrollbar.onScrollDelegate(scroll);
	}

	@Override
	public void onScrollbarConnected(Scrollbar scrollbar) {
		this.scrollbar = scrollbar;
	}

}
