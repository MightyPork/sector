package net.sector.gui.panels.profiles;


import java.util.ArrayList;

import net.sector.gui.panels.PanelGui;
import net.sector.gui.screens.Screen;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.IWidgetFactory;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.CompositeScrollBox;
import net.sector.gui.widgets.display.Text;
import net.sector.gui.widgets.display.TextWithBackground;
import net.sector.gui.widgets.input.Button;
import net.sector.gui.widgets.input.Checkbox;
import net.sector.gui.widgets.input.TextInputCountry;
import net.sector.gui.widgets.layout.LayoutH;
import net.sector.gui.widgets.layout.LayoutV;
import net.sector.gui.widgets.layout.WindowFrame;
import net.sector.input.Function;
import net.sector.network.CountryList;
import net.sector.network.CountryList.Country;
import net.sector.util.Align;

import org.lwjgl.input.Keyboard;

import com.porcupine.color.RGB;


/**
 * Overlay panel for paused game.
 * 
 * @author MightyPork
 */
public class PanelSelectCountry extends PanelGui {

	private static final int CANCEL = 0;
	private static final int OK = 1;
	private static final int CHECK_ALL = 2;


	private CompositeScrollBox scrollBox;
	private TextInputCountry countryEdit;


	/**
	 * Game over panel
	 * 
	 * @param screen the screen
	 * @param score player score
	 */
	public PanelSelectCountry(Screen screen, TextInputCountry countryEdit) {
		super(screen);
		this.countryEdit = countryEdit;
	}

	private Function<Boolean> onClickHandler = new Function<Boolean>() {

		@Override
		public Boolean run(Object... args) {
			Widget w = (Widget) args[0];
			countryEdit.setCountry(w.getTag());
			closePanel();
			return true;
		}

	};


	private class EntryFactory implements IWidgetFactory {

		@Override
		public Widget getWidget() {
			return getItem(new Country("", ""));
		}

		public Text getItem(Country c) {
			Text t = new TextWithBackground(c.name, "small_text").setBackgroundColor(new RGB(0x479EF5));
			t.addOnClickHandler(onClickHandler);
			t.setPadding(6, 2);
			t.setMargins(2, 1, 2, 1);
			t.setTextAlign(Align.LEFT);
			t.setMinWidth(400);
			t.setTag(c.code);

			if (c.isSmall) t.setColorText(new RGB(0x999999));
			if (c.isFictional) t.setColorText(new RGB(0x55ffff));

			return t;

		}
	}

	private EntryFactory scrollBoxItemFactory = new EntryFactory();
	private ArrayList<Widget> itemsBig;
	private ArrayList<Widget> itemsAll;
	private Button bnCancel;

	@Override
	public void initGui(GuiRoot root) {

		itemsBig = new ArrayList<Widget>();
		itemsAll = new ArrayList<Widget>();

		for (Country c : CountryList.big) {
			itemsBig.add(scrollBoxItemFactory.getItem(c));
		}

		for (Country c : CountryList.all) {
			itemsAll.add(scrollBoxItemFactory.getItem(c));
		}

		//@formatter:off
		WindowFrame frame = new WindowFrame();
		//frame.setTheme(ETheme.GREEN_FRAME);
		frame.setPadding(5, 5, 5, 5);
		frame.enableShadow(true);

			LayoutV v = new LayoutV(Align.CENTER);
			
				v.add(new Text("Country list", "dialog_heading").setMarginsV(10, 15));
				
				v.add(new Checkbox(CHECK_ALL, "Show all countries", "small_text").setChecked(false));				
								
				scrollBox = new CompositeScrollBox(10, scrollBoxItemFactory);
				
				v.add(scrollBox);
		
				LayoutH h = new LayoutH(Align.CENTER);
					h.add(bnCancel = new Button(CANCEL, "Cancel", "small_text"));
				v.add(h);
			
			frame.add(v);
		
		root.setRootWidget(frame);
		
		//bnCancel.setTheme(ETheme.GREEN);

		//@formatter:on

		loadCountriesToList(true);
	}

	private void loadCountriesToList(boolean bigOnly) {
		scrollBox.removeAll();

		scrollBox.addItem(scrollBoxItemFactory.getItem(new Country("", "(no country)")));

		if (bigOnly) {
			for (Widget w : itemsBig)
				scrollBox.addItem(w);
		} else {
			for (Widget w : itemsAll)
				scrollBox.addItem(w);
		}

		scrollBox.refresh();
	}


	@Override
	public void actionPerformed(Widget widget) {
		if (!widget.isEnabled()) return;

		if (widget.id == CHECK_ALL) {
			loadCountriesToList(!((Checkbox) widget).isChecked());
			return;
		}
		if (widget.id == CANCEL) {
			closePanel();
			return;
		}
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		super.onKey(key, c, down);

		if (key == Keyboard.KEY_ESCAPE && down) {
			actionPerformed(bnCancel);
		}
	}

	@Override
	public void onFocus() {
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onBlur() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean hasBackgroundLayer() {
		return true;
	}
	
	@Override
	public RGB getBackgroundColor() {
		return new RGB(0, 0.4);
	}

}