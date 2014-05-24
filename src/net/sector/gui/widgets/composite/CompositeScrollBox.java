package net.sector.gui.widgets.composite;


import java.util.ArrayList;
import java.util.List;

import net.sector.annotations.Internal;
import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.input.Scrollbar;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.ScrollingLayoutV;
import net.sector.util.Align;


public class CompositeScrollBox extends LayoutH {

	private ScrollingLayoutV layoutV;
	private Scrollbar scrollbar;

	public CompositeScrollBox(int size, int scrollbarWidth, IWidgetFactory fakeWidgetFactory, List<Widget> elements) {
		super(Align.TOP);

		layoutV = new ScrollingLayoutV(size, fakeWidgetFactory);

		for (Widget w : elements) {
			layoutV.addChild(w);
		}

		add(layoutV);

		add(scrollbar = new Scrollbar(scrollbarWidth, (int) layoutV.getSize().y).setScrollable(layoutV));
	}

	public CompositeScrollBox(int size, IWidgetFactory fakeWidgetFactory, List<Widget> elements) {
		this(size, 31, fakeWidgetFactory, elements);
	}

	public CompositeScrollBox(int size, int scrollbarWidth, IWidgetFactory fakeWidgetFactory) {
		this(size, scrollbarWidth, fakeWidgetFactory, new ArrayList<Widget>());
	}

	public CompositeScrollBox(int size, IWidgetFactory fakeWidgetFactory) {
		this(size, 31, fakeWidgetFactory, new ArrayList<Widget>());
	}

	public CompositeScrollBox addItem(Widget item) {
		item.setGuiRoot(getGuiRoot());
		layoutV.addChild(item);
		return this;
	}

	public ArrayList<Widget> getItems() {
		return layoutV.children;
	}

	/**
	 * Refresh positions and contents after some children were added or removed.
	 */
	public void refresh() {
		setGuiRoot(getGuiRoot());
		layoutV.onScrollbarChange(scrollbar.getValue());
		setGuiRoot(getGuiRoot());
		//scb.setValue(0);
	}

	@Override
	@Internal
	public void add(Widget child) {
		super.add(child);
		child.setGuiRoot(getGuiRoot());
	}

	@Override
	public void removeAll() {
		layoutV.removeAll();
	}

}
