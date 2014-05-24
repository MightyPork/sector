package net.sector.level.sequence;


/**
 * HUD message object
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class HudMessage {
	/** Message text */
	public String text = "";
	/** Displayed secs */
	public double secs = 3;

	/**
	 * NMew HUD message
	 * 
	 * @param text message text
	 * @param time time of dysplay (secs)
	 */
	public HudMessage(String text, double time) {
		this.text = text;
		secs = time;
	}
}
