package net.sector.gui.widgets.composite;


import java.util.ArrayList;

import net.sector.gui.widgets.IRefreshable;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.display.ColorRectange;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.input.ButtonIcon;
import net.sector.gui.widgets.layout.Gap;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.input.Function;
import net.sector.level.SuperContext;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


public class ListItemProfileGuest extends LayoutH implements IRefreshable {

	protected RGB indicatorColor;
	public ArrayList<Widget> scrollList = null;
	protected ButtonIcon bnSelect;

	/**
	 * Set scroll list (list of children in CompositeScrollBox
	 * 
	 * @param scrollList arraylist of children
	 * @return this
	 */
	public ListItemProfileGuest setScrollList(ArrayList<Widget> scrollList) {
		this.scrollList = scrollList;
		return this;
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		RGB topC = new RGB(0x5D3DD1, 0.4);
		RGB downC = new RGB(0x3D71D1, 0.4);

		if (SuperContext.selectedUser == null) {
			topC = new RGB(0x5D3DD1, 0.9);
			downC = new RGB(0x3D71D1, 0.9);
		}

		RenderUtils.quadRectGradV(rect, topC, downC);

		super.render(mouse);
	}

	public ListItemProfileGuest() {
		super(Align.CENTER);

		indicatorColor = new RGB(0x77ccff);

		setMinWidth(500);
		setMinHeight(50);

		add(new Gap(5, 0));
		add(new ColorRectange(20, 50, 5, 3, indicatorColor).setTooltip("Play without profile", new RGB(0xffffff)));

		String texture = "designer_icons";

		add(bnSelect = new ButtonIcon(-1, texture, 0, 3).setColor(RGB.GREEN));
		add(new Gap(5, 0));
		add(new Text("Guest", "small_menu").setTextAlign(Align.LEFT).setMinWidth(315));
		calcChildSizes();


		bnSelect.setTooltip("Select", new RGB(0x66ff66));


		bnSelect.addOnClickHandler(new Function<Boolean>() {
			@Override
			public Boolean run(Object... args) {
				SuperContext.selectedUser = null;
				refreshAll();
				return true;
			}
		});

		refresh();

	}

	public void refreshAll() {
		for (Widget w : scrollList) {
			IRefreshable ref = (IRefreshable) w;
			ref.refresh();
		}
	}


	@Override
	public void refresh() {
		if (SuperContext.selectedUser != null) {
			bnSelect.setSelected(false);
		} else {
			bnSelect.setSelected(true);
		}
	}

}
