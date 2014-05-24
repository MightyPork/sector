package net.sector.gui.panels;


import com.porcupine.color.RGB;


/**
 * Colors used in all highscore tables
 * 
 * @author MightyPork
 */
public class HsColors {
	/** Score number */
	public static final RGB SCORE = RGB.GREEN;
	/** Guest name */
	public static final RGB GUEST = RGB.WHITE;
	/** User name (not active) */
	public static final RGB USER = new RGB(0x2EA8FF);
	/** Active guest (just submitted) */
	public static final RGB GUEST_ACTIVE = new RGB(0xFFE100);
	/** Active user (Just submitted, or in an overview table) */
	public static final RGB USER_ACTIVE = RGB.CYAN;
}
