package net.sector.fonts;


import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;

import net.sector.App;
import net.sector.Constants;
import net.sector.util.Log;

import org.newdawn.slick.util.ResourceLoader;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Remade universal font manager for Sector.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class FontManager {

	private static final boolean DEBUG = Constants.LOG_FONTS;

	/**
	 * Glyph tables.
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 */
	public static class Glyphs {
		//@formatter:off
		/** all glyphs */
		public static final String all = 
				" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]" +
				"^_`abcdefghijklmnopqrstuvwxyz{|}~€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ¡¢£¤" +
				"¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäå" +
				"æçèéêëìíîïðñòóôõö÷øùúûüýþÿ";
		
		/** letters and numbers, sufficient for basic messages etc. NO SPACE */
		public static final String alnum_nospace = 
				"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		/** letters and numbers, sufficient for basic messages etc. */
		public static final String alnum = 
				" "+alnum_nospace;
		
		/** letters and numbers with the most basic punctuation signs */
		public static final String basic_text = 
				" .-,.?!:;_"+alnum_nospace;

		/** letters */
		public static final String alpha = 
				" ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		
		/** numbers */
		public static final String numbers = 
				" 0123456789.-,:";
		
		/** non-standard variants of alnum */
		public static final String alnum_extra = 
				" ŒÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜŸÝßàáâãäåæçèéêëìíîïðñòóôõöøùúû" +
				"üýþÿĚŠČŘŽŤŇĎŮěščřžťňďůŁłđ";
		
		/** signs and punctuation */
		public static final String signs = 
				" !\"#$%&§'()*+,-./:;<=>?@[\\]^_{|}~";
		
		/** extra signs and punctuation */
		public static final String signs_extra = 
				" ¥€£¢`ƒ„…†‡ˆ‰‹‘’“”•›¡¤¦¨ª«¬­¯°±²³´µ¶·¸¹º»¼½¾¿÷™©­®→↓←↑";
		
		
		/** basic character set. */
		public static final String basic = alnum + signs;
		//@formatter:on
	}

	/**
	 * Font style
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 */
	public static enum Style {
		/** Normal */
		NORMAL,
		/** Italic */
		ITALIC,
		/** Stronger italic to left. */
		LEFT,
		/** Stronger italic to right */
		RIGHT,
		/** Monospace type */
		MONO,
		/** Bold */
		BOLD,
		/** Bold & Italic */
		BOLD_I,
		/** Bold & Left */
		BOLD_L,
		/** Bold & Right */
		BOLD_R,
		/** Heavy style, stronger than bold. */
		HEAVY,
		/** Light (lighter than normal) */
		LIGHT,
		/** narrow style, similar to Light */
		NARROW,
		/** Wide style, like Bold but with thinner lines */
		WIDE,
		/** Outline variant of normal */
		OUTLINE;
	}

	/**
	 * Preloaded font identifier [name, size, style]
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 */
	public static class FontId {
		/** font size (pt) */
		public float size = 24;
		/** font name, registered with registerFile */
		public String name = "";
		/** font style. The given style must be in a file. */
		public Style style;

		/** Set of glyphs in this ID */
		public String glyphs = "";

		/** Index for faster comparision of glyph ids. */
		public int glyphset_id = 0;

		/**
		 * Preloaded font identifier
		 * 
		 * @param name font name (registerFile)
		 * @param size font size (pt)
		 * @param style font style
		 * @param glyphs glyphs to load
		 */
		public FontId(String name, double size, Style style, String glyphs) {
			this.name = name;
			this.size = (float) size;
			this.style = style;

			if (glyphs.equals(Glyphs.basic)) {
				glyphset_id = 1;
			} else if (glyphs.equals(Glyphs.alnum)) {
				glyphset_id = 2;
			} else if (glyphs.equals(Glyphs.basic_text)) {
				glyphset_id = 3;
			} else if (glyphs.equals(Glyphs.numbers)) {
				glyphset_id = 4;
			} else if (glyphs.equals(Glyphs.alpha)) {
				glyphset_id = 5;
			} else if (glyphs.equals(Glyphs.all)) {
				glyphset_id = 6;
			} else if (glyphs.equals(Glyphs.alnum_extra)) {
				glyphset_id = 7;
			} else if (glyphs.equals(Glyphs.signs)) {
				glyphset_id = 8;
			} else if (glyphs.equals(Glyphs.signs_extra)) {
				glyphset_id = 9;
			} else {
				this.glyphs = glyphs;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj.getClass().isAssignableFrom(getClass()))) return false;
			if (obj instanceof FontId) {
				if (obj == this) return true;
				FontId id2 = ((FontId) obj);
				boolean flag = true;
				flag &= id2.size == size;
				flag &= id2.name.equals(name);
				flag &= id2.style == style;
				flag &= ((id2.glyphset_id != -1 && id2.glyphset_id == glyphset_id) || id2.glyphs.equals(glyphs));
				return flag;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return (new Float(size).hashCode()) ^ name.hashCode() ^ style.hashCode() ^ glyphset_id;
		}

		@Override
		public String toString() {
			return "[" + name + ", " + size + ", " + style + (glyphset_id > 0 ? ", g=" + glyphset_id : ", g=custom") + "]";
		}
	}

	/**
	 * Group of styles of one font.
	 * 
	 * @author Ondřej Hruška (MightyPork)
	 */
	public static class FontFamily extends HashMap<Style, String> {}


	/**
	 * Table of font files. name → {style:file,style:file,style:file...}
	 */
	private static HashMap<String, FontFamily> fontFiles = new HashMap<String, FontFamily>();

	private static HashMap<FontId, LoadedFont> loadedFonts = new HashMap<FontId, LoadedFont>();

	private static HashMap<String, FontId> aliases = new HashMap<String, FontId>();

	private static boolean FULLSCREEN_DOUBLE_SIZE = false;

	/**
	 * Decide whether fonts in fullscreen should be twice larger or not.
	 * 
	 * @param state switch state
	 */
	public static void setFullscreenDoubleSize(boolean state) {
		FULLSCREEN_DOUBLE_SIZE = state;
	}


	/**
	 * Register font file.
	 * 
	 * @param path resource path (res/fonts/...)
	 * @param name font name (for binding)
	 * @param style font style in this file
	 */
	public static void registerFile(String path, String name, Style style) {
		if (fontFiles.containsKey(name)) {
			if (fontFiles.get(name) != null) {
				fontFiles.get(name).put(style, path);
				//Log.finest("Registered font file \""+path+"\" as ["+name+", "+style+"].");
				return;
			}
		}

		// insert new table of styles to font name.
		FontFamily family = new FontFamily();
		family.put(style, path);
		fontFiles.put(name, family);
		//Log.finest("Registered font file \""+path+"\" as ["+name+", "+style+"].");		
	}

	public static int loadedFontCounter = 0;

	/**
	 * Preload font if needed, get preloaded font.<br>
	 * If needed file is not available, throws runtime exception.
	 * 
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @param glyphs glyphs needed
	 * @return the loaded font.
	 */
	public static LoadedFont preloadFont(String name, double size, Style style, String glyphs) {
		FontId id = new FontId(name, size, style, glyphs);
		if (loadedFonts.containsKey(id)) {
			return loadedFonts.get(id);
		}

		String resourcePath;
		try {
			resourcePath = fontFiles.get(name).get(style);
			if (resourcePath == null) {
				Log.w("Font [" + name + "] does not have variant " + style + ".\nUsing NORMAL instead.");
				resourcePath = fontFiles.get(name).get(Style.NORMAL);
				if (resourcePath == null) {
					throw new NullPointerException();
				}
			}
		} catch (NullPointerException npe) {
			throw new RuntimeException("Font loading failed: no font file registered for name \"" + name + "\".");
		}

		InputStream in = ResourceLoader.getResourceAsStream(resourcePath);

		Font awtFont;
		try {
			awtFont = Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (Exception e) {
			Log.e("Loading of font " + resourcePath + " failed.", e);
			throw new RuntimeException(e);
		}
		awtFont = awtFont.deriveFont((float) size); // set font size
		LoadedFont font = new LoadedFont(awtFont, true, glyphs);

		font.setCorrection(correctionLeft, correctionRight);

		loadedFonts.put(id, font);
		loadedFontCounter++;

		if (DEBUG) Log.f3("Font from file \"" + resourcePath + "\" preloaded as " + id + ".");

		return font;
	}

	/**
	 * Get font from parameters, preload if needed
	 * 
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @return the font
	 */
	public static LoadedFont getFont(String name, double size, Style style) {
		return getFont(name, size, style, Glyphs.basic);
	}


	/**
	 * Get font from parameters, preload if needed
	 * 
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @param glyphs glyphs requested
	 * @return the font
	 */
	public static LoadedFont getFont(String name, double size, Style style, String glyphs) {
		FontId id = new FontId(name, size, style, glyphs);
		if (loadedFonts.containsKey(id)) {
			return loadedFonts.get(id);
		}
		return preloadFont(name, size, style, glyphs);
	}

	/**
	 * Get font by alias
	 * 
	 * @param alias font alias
	 * @return the font
	 */
	public static LoadedFont getFont(String alias) {
		alias = fs(alias);
		FontId id = aliases.get(alias);
		if (id == null) throw new RuntimeException("Font alias \"" + alias + "\" undefined.");
		return loadedFonts.get(id);
	}

	private static double clipVt = 0;
	private static double clipVb = 0;
	private static Coord scale = new Coord(1, 1);
	private static int correctionLeft = 9;
	private static int correctionRight = 8;

	/**
	 * Set vertical clip for next loaded font
	 * 
	 * @param clipT clip top (0-1)
	 * @param clipB vlip bottom (0-1)
	 */
	public static void nextLoadedClipV(double clipT, double clipB) {
		FontManager.clipVt = clipT;
		FontManager.clipVb = clipB;
	}

	/**
	 * Set relative scale for next loaded font
	 * 
	 * @param scale scale
	 */
	public static void nextLoadedScale(Coord scale) {
		FontManager.scale = scale;
	}

	/**
	 * Set spacing correction for next loaded font
	 * 
	 * @param left left
	 * @param right right
	 */
	public static void nextLoadedCorrection(int left, int right) {
		correctionLeft = left;
		correctionRight = right;
	}

	/**
	 * Reset settings for "next loaded font"
	 */
	public static void resetNextLoadedSettings() {
		clipVt = 0;
		clipVb = 0;
		scale = new Coord(1, 1);
		correctionLeft = 9;
		correctionRight = 8;
	}

	/**
	 * Register font alias and preload.
	 * 
	 * @param alias alias to add
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 */
	public static void addAlias(String alias, String name, double size, Style style) {
		addAlias(alias, name, size, style, Glyphs.basic, scale, clipVt, clipVb);
	}


	/**
	 * Register font alias and preload. Preloads also fulscreen variant with
	 * twice larger size.
	 * 
	 * @param alias alias to add
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @param glyphs glyphs needed
	 */
	public static void addAlias(String alias, String name, double size, Style style, String glyphs) {
		addAlias(alias, name, size, style, glyphs, scale, clipVt, clipVb);
	}

	/**
	 * Register font alias and preload. Preloads also fulscreen variant with
	 * twice larger size.
	 * 
	 * @param alias alias to add
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @param glyphs glyphs needed
	 * @param scale font scale
	 */
	public static void addAlias(String alias, String name, double size, Style style, String glyphs, Coord scale) {
		addAlias(alias, name, size, style, glyphs, scale, clipVt, clipVb);
	}



	/**
	 * Register font alias and preload. Preloads also fulscreen variant with
	 * twice larger size.
	 * 
	 * @param alias alias to add
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @param glyphs glyphs needed
	 * @param scale font scale
	 * @param clipT vertical clip 0-1 (up)
	 * @param clipB vertical clip 0-1 (bottom)
	 */
	public static void addAlias(String alias, String name, double size, Style style, String glyphs, Coord scale, double clipT, double clipB) {

		FontId id;

		preloadFont(name, size, style, glyphs).setScale(scale.x, scale.y).setClip(clipT, clipB);
		id = new FontId(name, size, style, glyphs);
		aliases.put(alias, id);

		preloadFont(name, size * 2, style, glyphs).setScale(scale.x, scale.y).setClip(clipT, clipB);
		id = new FontId(name, size * 2, style, glyphs);
		aliases.put(alias + "_fs", id);

		if (DEBUG) Log.f3("Font " + id + " connected to alias " + alias + "(_fs).");

	}

	/**
	 * Register font alias and preload. Preloads also fulscreen variant with
	 * twice larger size.
	 * 
	 * @param alias alias to add
	 * @param name font name (registerFile)
	 * @param size font size (pt)
	 * @param style font style
	 * @param glyphs glyphs needed
	 * @param clipT clip top
	 * @param clipB clip bottom
	 */
	public static void addAlias(String alias, String name, double size, Style style, String glyphs, double clipT, double clipB) {

		FontId id;

		preloadFont(name, size, style, glyphs).setClip(clipT, clipB);
		id = new FontId(name, size, style, glyphs);
		aliases.put(alias, id);

		preloadFont(name, size * 2, style, glyphs).setClip(clipT, clipB);
		id = new FontId(name, size * 2, style, glyphs);
		aliases.put(alias + "_fs", id);

		if (DEBUG) Log.f3("Font " + id + " connected to alias " + alias + "(_fs).");

	}

	/**
	 * Destroy font data
	 * 
	 * @param alias font alias
	 */
	public static void destroyFont(String alias) {
		if (aliases.containsKey(alias)) {
			FontId fontId = aliases.get(alias);
			aliases.remove(alias);
			LoadedFont fontData = loadedFonts.get(fontId);
			if (fontData != null) {
				fontData.destroy();
				loadedFonts.remove(fontId);
			}
		}
	}

	/**
	 * Get string width
	 * 
	 * @param fontAlias font alias
	 * @param text text
	 * @return size (px)
	 */
	public static float width(String fontAlias, String text) {
		return getFont(fontAlias).getWidth(text);
	}

	/**
	 * Get string width
	 * 
	 * @param name font name
	 * @param size font size (pt)
	 * @param style font style
	 * @param text text
	 * @return size (px)
	 */
	public static float width(String name, double size, Style style, String text) {
		return getFont(name, size, style).getWidth(text);
	}

	private static LoadedFont getCurrentFont() {
		return getFont(fontName, fontSize, fontStyle);
	}

	/**
	 * Get string width (currently binded with default set calls)
	 * 
	 * @param text text
	 * @return size (px)
	 */
	public static float width(String text) {
		return getCurrentFont().getWidth(text);
	}

	private static String fs(String alias) {
		if (!FULLSCREEN_DOUBLE_SIZE) return alias;
		return alias += (App.isFullscreen() ? "_fs" : "");
	}

	/**
	 * Get font height
	 * 
	 * @param alias font alias
	 * @return height (px)
	 */
	public static float height(String alias) {
		return getFont(alias).getLineHeight();
	}

	/**
	 * Get font height (currently binded with default set calls)
	 * 
	 * @return height (px)
	 */
	public static float height() {
		return getCurrentFont().getLineHeight();
	}

	/**
	 * Get font height
	 * 
	 * @param name font name
	 * @param size font size (pt)
	 * @param style font style
	 * @return height (px)
	 */
	public static float height(String name, double size, Style style) {
		return getFont(name, size, style).getLineHeight();
	}


	// Draw by alias.

	private static RGB fontColor = new RGB(1, 1, 1);
	private static int fontAlign = -1;
	private static String fontName = "default";
	private static double fontSize = 24;
	private static Style fontStyle = Style.NORMAL;

	/**
	 * Set default render color
	 * 
	 * @param color new color
	 */
	public static void setColor(RGB color) {
		fontColor.setTo(color);
	}

	/**
	 * Set default render align
	 * 
	 * @param align align (-1,0,1)
	 */
	public static void setAlign(int align) {
		fontAlign = align;
	}

	/**
	 * Set default font alias
	 * 
	 * @param fontAlias font alias
	 */
	public static void setFont(String fontAlias) {
		FontId id = aliases.get(fs(fontAlias));
		fontName = id.name;
		fontSize = id.size;
		fontStyle = id.style;
	}

	/**
	 * Set default font alias and color
	 * 
	 * @param fontAlias font alias
	 * @param color color
	 */
	public static void setFont(String fontAlias, RGB color) {
		setFont(fontAlias);
		setColor(color);
	}

	/**
	 * Set default font alias and color
	 * 
	 * @param fontAlias font alias
	 * @param align font align (-1,0,1)
	 */
	public static void setFont(String fontAlias, int align) {
		setFont(fontAlias);
		fontAlign = align;
	}

	/**
	 * Set default font alias, color, align
	 * 
	 * @param fontAlias alias
	 * @param color color
	 * @param align font align -1,0,1
	 */
	public static void setFont(String fontAlias, RGB color, int align) {
		setFont(fontAlias, color);
		fontAlign = align;
	}

//
//	/**
//	 * Set default font name,size,style and color
//	 * 
//	 * @param name font name
//	 * @param size font size (pt)
//	 * @param style font style
//	 * @param color color
//	 */
//	public static void setFont(String name, double size, Style style, RGB color) {
//		setFont(name, size * App.fs12(), style);
//		setColor(color);
//	}
//
//	/**
//	 * Set default font name,size,style and color
//	 * 
//	 * @param name font name
//	 * @param size font size (pt)
//	 * @param style font style
//	 */
//	public static void setFont(String name, double size, Style style) {
//		fontName = name;
//		fontSize = size * App.fs12();
//		fontStyle = style;
//	}
//
//	/**
//	 * Set default font name,size,style and color
//	 * 
//	 * @param name font name
//	 * @param size font size (pt)
//	 * @param style font style
//	 * @param color color
//	 * @param align font align -1,0,1
//	 */
//	public static void setFont(String name, double size, Style style, RGB color, int align) {
//		setFont(name, size, style);
//		setColor(color);
//		fontAlign = align;
//	}

//	/**
//	 * Set default font name,size,style and color
//	 * 
//	 * @param name font name
//	 * @param size font size (pt)
//	 * @param style font style
//	 * @param align font align -1,0,1
//	 */
//	public static void setFont(String name, double size, Style style, int align) {
//		fontName = name;
//		fontSize = size * App.fs12();
//		fontStyle = style;
//		fontAlign = align;
//	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 */
	public static void draw(Coord pos, String text) {
		draw(pos.x, pos.y, text, fontName, fontSize, fontStyle, fontColor, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param color render color
	 */
	public static void draw(Coord pos, String text, RGB color) {
		draw(pos.x, pos.y, text, fontName, fontSize, fontStyle, color, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param fontAlias font alias
	 * @param color render color
	 */
	public static void draw(Coord pos, String text, String fontAlias, RGB color) {
		draw(pos.x, pos.y, text, fontAlias, color, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param fontAlias font alias
	 */
	public static void draw(Coord pos, String text, String fontAlias) {
		draw(pos.x, pos.y, text, fontAlias, fontColor, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param align (-1,0,1)
	 */
	public static void draw(Coord pos, String text, int align) {
		draw(pos.x, pos.y, text, fontName, fontSize, fontStyle, fontColor, align);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param color render color
	 * @param align (-1,0,1)
	 */
	public static void draw(Coord pos, String text, RGB color, int align) {
		draw(pos.x, pos.y, text, fontName, fontSize, fontStyle, color, align);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param fontAlias font alias
	 * @param color render color
	 * @param align (-1,0,1)
	 */
	public static void draw(Coord pos, String text, String fontAlias, RGB color, int align) {
		draw(pos.x, pos.y, text, fontAlias, color, align);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param pos position
	 * @param text text to draw
	 * @param fontAlias font alias
	 * @param align (-1,0,1)
	 */
	public static void draw(Coord pos, String text, String fontAlias, int align) {
		draw(pos.x, pos.y, text, fontAlias, fontColor, align);
	}


	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 */
	public static void draw(double x, double y, String text) {
		draw(x, y, text, fontName, fontSize, fontStyle, fontColor, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param color render color
	 */
	public static void draw(double x, double y, String text, RGB color) {
		draw(x, y, text, fontName, fontSize, fontStyle, color, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param fontAlias font alias
	 * @param color render color
	 */
	public static void draw(double x, double y, String text, String fontAlias, RGB color) {
		draw(x, y, text, fontAlias, color, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param fontAlias font alias
	 */
	public static void draw(double x, double y, String text, String fontAlias) {
		draw(x, y, text, fontAlias, fontColor, fontAlign);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param align (-1,0,1)
	 */
	public static void draw(double x, double y, String text, int align) {
		draw(x, y, text, fontName, fontSize, fontStyle, fontColor, align);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param color render color
	 * @param align (-1,0,1)
	 */
	public static void draw(double x, double y, String text, RGB color, int align) {
		draw(x, y, text, fontName, fontSize, fontStyle, color, align);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param fontAlias font alias
	 * @param align (-1,0,1)
	 */
	public static void draw(double x, double y, String text, String fontAlias, int align) {
		draw(x, y, text, fontAlias, fontColor, align);
	}

	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param fontAlias font alias
	 * @param color render color
	 * @param align (-1,0,1)
	 */
	public static void draw(double x, double y, String text, String fontAlias, RGB color, int align) {
		getFont(fontAlias).drawString(x, y, text, 1, 1, color, align);
	}


	/**
	 * Draw string with font.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @param text text to draw
	 * @param name font name
	 * @param size font size
	 * @param style font style
	 * @param color font color
	 * @param align align (-1,0,1)
	 */
	public static void draw(double x, double y, String text, String name, double size, Style style, RGB color, int align) {
		getFont(name, size, style).drawString(x, y, text, 1, 1, color, align);
	}


	public static void drawFuzzy(Coord pos, String text, String font, RGB blurColor, RGB textColor, int blurSize, int align) {
		FontManager.drawFuzzy(pos, text, font, blurColor, textColor, blurSize, align, true);
	}


	public static void drawFuzzy(Coord pos, String text, String font, RGB blurColor, RGB textColor, int blurSize, int align, boolean smooth) {

		glPushMatrix();

		glTranslated(pos.x, pos.y, pos.z);

		glEnable(GL_TEXTURE_2D);

		//shadow
		int sh = blurSize;

		int l = glGenLists(1);

		setColor(blurColor);
		glNewList(l, GL_COMPILE);
		draw(0, 0, text, font, align);
		glEndList();


		for (int xx = -sh; xx <= sh; xx += (smooth ? 1 : sh)) {
			for (int yy = -sh; yy <= sh; yy += (smooth ? 1 : sh)) {
				if (xx == 0 && yy == 0) continue;
				glPushMatrix();
				glTranslated(xx, yy, 0);
				glCallList(l);
				glPopMatrix();
			}
		}

		glDeleteLists(l, 1);

		draw(0, 0, text, font, textColor, align);

		glDisable(GL_TEXTURE_2D);

		glPopMatrix();
	}



}
