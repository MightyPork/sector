package net.sector.gui.widgets;


import static net.sector.gui.widgets.EColorRole.*;

import java.util.HashMap;
import java.util.Map;

import com.porcupine.color.RGB;


public class ColorScheme {


	public static ColorScheme getForTheme(ETheme theme) {
		switch (theme) {
			case BLUE:
				return blue;

			case GREEN:
				return green;

			case BLUE_FRAME:
				return blueFrame;

			case GREEN_FRAME:
				return greenFrame;

			case DES_PANEL:
				return desSidePanel;

			case DES_TAB_BUTTON:
				return desShopButton;

			case DES_TABLE:
				return desTable;

			case MENU_BUTTON:
				return menuButton;

			case MENU_TITLE:
				return menuTitle;

			case BLUE_SCROLLBAR:
				return blueScrollbar;
		}

		return blue;
	}

	private static ColorScheme blue;
	private static ColorScheme green;
	private static ColorScheme blueFrame;
	private static ColorScheme greenFrame;
	private static ColorScheme menuButton;
	private static ColorScheme menuTitle;

	private static ColorScheme desSidePanel;
	private static ColorScheme desTable;

	private static ColorScheme desShopButton;

	private static ColorScheme blueScrollbar;

	public static void init() {

		ColorScheme cs;

		//@formatter:off
		
		menuButton = cs = new ColorScheme();
		cs.base.put(FG, new RGB(0x0B74E3));
		cs.base.put(SHADOW, new RGB(0x003185, 0.2));	
		
		cs.hover.put(FG, new RGB(0x02E9FA));
		cs.hover.put(SHADOW, new RGB(0x00A8FC, 0.05));
		
		
		
		menuTitle = cs = new ColorScheme();
		cs.base.put(FG, new RGB(0xffffff));
		cs.base.put(SHADOW, new RGB(0xffffff, 0.2));
		
		
		
		blueScrollbar = cs = new ColorScheme();
		cs.base.put(BDR, new RGB(0x999999, 0.8)); // ridge border
		cs.base.put(BG, new RGB(0x444444, 0.7)); // ridge background
		cs.base.put(BDR_INNER, new RGB(0x9999ff, 1.0)); // handle border
		cs.base.put(FG, new RGB(0x5555ff)); // handle inside
		cs.hover.put(FG, new RGB(0x7777ff)); // handle inside
		
		
		
		
		green = cs = new ColorScheme();			
		cs.setBase(
			new RGB(0x00CC00, 1), // bdr
			new RGB(0x00AA00, 0.5), // bg
			new RGB(0xCCDDD0, 1)  // fg
		);		
		cs.setHover(
			new RGB(0x00EE00, 1), // bdr
			new RGB(0x33CC33, 0.7), // bg
			new RGB(0xFFFFFF, 1)  // fg
		);		
		cs.setClicked(
			new RGB(0x00EE00, 1), // bdr
			new RGB(0x22AA22, 0.8), // bg
			new RGB(0xFFFFFF, 1)  // fg
		);
		cs.setSelected(
			new RGB(0x00FF00, 1), // bdr
			new RGB(0x22EE22, 0.8), // bg
			new RGB(0xFFFFFF, 1)  // fg
		);
		
		
		
		blue = cs = new ColorScheme();			
		cs.setBase(
			new RGB(0x0000CC, 1), // bdr
			new RGB(0x7777FF, 0.5), // bg
			new RGB(0xDDDDDD, 1)  // fg
		);		
		cs.setHover(
			new RGB(0x0000DD, 1), // bdr
			new RGB(0x7777FF, 0.7), // bg
			new RGB(0xFFFFFF, 1)  // fg
		);	
		cs.setClicked(
			new RGB(0x0000FF, 1), // bdr
			new RGB(0x5555EE, 0.8), // bg
			new RGB(0xFFFFFF, 1)  // fg
		);
		cs.setSelected(
			new RGB(0x0000FF, 1), // bdr
			new RGB(0x2222EE, 0.8), // bg
			new RGB(0xFFFFFF, 1)  // fg
		);
		
		
		
		desShopButton = cs = new ColorScheme();			
		cs.setBase(
			new RGB(0x99cc99, 1), // bdr
			new RGB(0x004400, 0.8), // bg
			new RGB(0xffffff, 0.8)  // fg
		);		
		cs.setHover(
			new RGB(0x99cc99, 1), // bdr
			new RGB(0x004400, 0.8), // bg
			new RGB(0xffffff, 1)  // fg
		);		
		cs.setClicked(
			new RGB(0x99cc99, 1), // bdr
			new RGB(0x338833, 0.8), // bg
			new RGB(0xffffff, 1)  // fg
		);
		cs.setSelected(
			new RGB(0x99cc99, 1), // bdr
			new RGB(0x33aa33, 0.8), // bg
			new RGB(0xffffff, 1)  // fg
		);

		
		
		blueFrame = new ColorScheme(
			new RGB(0x0000FF, 0.95), // bdr
			new RGB(0x353555, 0.99), // bg
			new RGB(0, 0)  // fg
		);
		blueFrame.base.put(SHADOW, new RGB(0, 0.3));
		
		
		
		greenFrame = cs = new ColorScheme(
			new RGB(0x00EE00, 0.9), // bdr
			new RGB(0x003300, 0.9), // bg
			new RGB(0, 0)  // fg
		);
		cs.base.put(SHADOW, new RGB(0, 0.3));
		
		
		
		desSidePanel = new ColorScheme(
			new RGB(0x99cc99, 1), // bdr
			new RGB(0x002200, 0.6), // bg
			new RGB(0, 0)  // fg
		);
		
		
		
		desTable = new ColorScheme(
			new RGB(0x9999cc, 1), // bdr
			new RGB(0x000019, 0.5), // bg
			new RGB(0x0000FF, 0.5)  // fg
		);
				
		//@formatter:on

	}


	private Map<EColorRole, RGB> base = new HashMap<EColorRole, RGB>();
	private Map<EColorRole, RGB> hover = new HashMap<EColorRole, RGB>();
	private Map<EColorRole, RGB> clicked = new HashMap<EColorRole, RGB>();
	private Map<EColorRole, RGB> selected = new HashMap<EColorRole, RGB>();

	public ColorScheme() {}

	public ColorScheme(RGB border, RGB background, RGB foreground) {
		setBase(border, background, foreground);
		setHover(border, background, foreground);
		setClicked(border, background, foreground);
		setSelected(border, background, foreground);
	}

	public ColorScheme(RGB border, RGB background) {
		setBase(border, background, RGB.BLACK);
		setHover(border, background, RGB.BLACK);
		setClicked(border, background, RGB.BLACK);
		setSelected(border, background, RGB.BLACK);
	}

	public ColorScheme setBase(RGB border, RGB background, RGB foreground) {
		base.put(BDR, border);
		base.put(BG, background);
		base.put(FG, foreground);
		return this;
	}

	public ColorScheme setHover(RGB border, RGB background, RGB foreground) {
		hover.put(BDR, border);
		hover.put(BG, background);
		hover.put(FG, foreground);
		return this;
	}

	public ColorScheme setClicked(RGB border, RGB background, RGB foreground) {
		clicked.put(BDR, border);
		clicked.put(BG, background);
		clicked.put(FG, foreground);
		return this;
	}

	public ColorScheme setSelected(RGB border, RGB background, RGB foreground) {
		selected.put(BDR, border);
		selected.put(BG, background);
		selected.put(FG, foreground);
		return this;
	}

	private RGB fallback(RGB... options) {
		for (RGB color : options) {
			if (color != null) return color;
		}
		return RGB.RED; // error color
	}


	public RGB getColor(EColorRole role, boolean panelActive, boolean enabled, boolean mouseOver, boolean mouseDown, boolean widgetSelected) {

		if (!enabled) {
			if (widgetSelected) return fallback(selected.get(role), base.get(role)).mulAlpha(0.3);
			return base.get(role).mulAlpha(0.3);
		}

		if (mouseDown && panelActive) {
			if (widgetSelected) return fallback(selected.get(role), clicked.get(role), hover.get(role), base.get(role));
			return fallback(clicked.get(role), hover.get(role), base.get(role));
		}

		if (mouseOver && panelActive) {
			if (widgetSelected) return fallback(selected.get(role), hover.get(role), base.get(role));
			return fallback(hover.get(role), clicked.get(role), base.get(role));
		}

		if (widgetSelected) return fallback(selected.get(role), base.get(role));
		return fallback(base.get(role));
	}

}
