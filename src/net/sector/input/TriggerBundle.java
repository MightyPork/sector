package net.sector.input;


import net.sector.level.dataobj.AiObjParser;

import org.lwjgl.input.Keyboard;


/**
 * Bundle representing input configuration for triggers.
 * 
 * @author MightyPork
 */
public class TriggerBundle {
	/** Detected event */
	public EInput event;
	/** Event attribute */
	public int attrib;

	/**
	 * Create trigger bundle
	 * 
	 * @param event event
	 * @param attrib event attribute
	 */
	public TriggerBundle(EInput event, int attrib) {
		this.event = event;
		this.attrib = attrib;
	}

	/**
	 * Create trigger bundle
	 * 
	 * @param other other bundle
	 */
	public TriggerBundle(TriggerBundle other) {
		this.event = other.event;
		this.attrib = other.attrib;
	}

	public TriggerBundle() {}

	/**
	 * Get copy
	 * 
	 * @return copy
	 */
	public TriggerBundle copy() {
		return new TriggerBundle(this);
	}

	/**
	 * Make the event static / dynamic
	 * 
	 * @param beStatic is static [TRUE] (hold) or dynamic (press)
	 */
	public void setStatic(boolean beStatic) {
		event = event.variant(beStatic);
	}

	/**
	 * Get human readable variant of trigger bundle.
	 * 
	 * @param longFmt long style (short: X, Left mouse, long: Press X, Left
	 *            mouse down)
	 * @return human readable format
	 */
	public String getLabel(boolean longFmt) {
		String evt = null, name = null;
		switch (event) {
			case BTN_PRESS:
				evt = "down";
				//$FALL-THROUGH$
			case BTN_DOWN:
				if (evt == null) evt = "hold";
				//$FALL-THROUGH$
			case BTN_UP:
				if (evt == null) evt = "unhold";
				//$FALL-THROUGH$
			case BTN_RELEASE:
				if (evt == null) evt = "up";

				// mouse.
				if (attrib == 0) name = "Left mouse";
				if (attrib == 1) name = "Right mouse";
				if (attrib == 2) name = "Middle mouse";
				if (attrib > 2) name = "Mouse " + attrib;

				if (longFmt) name += " " + evt;
				return name;

			case KEY_PRESS:
				evt = "Press";
				//$FALL-THROUGH$
			case KEY_DOWN:
				if (evt == null) evt = "Hold";
				//$FALL-THROUGH$
			case KEY_UP:
				if (evt == null) evt = "Unhold";
				//$FALL-THROUGH$
			case KEY_RELEASE:
				if (evt == null) evt = "Release";

				name = Keyboard.getKeyName(attrib);
				if (longFmt) name = evt + " " + name;
				return name;

			case SCROLL:
				return "Wheel " + (attrib < 0 ? "in" : "out");
		}

		return "???";
	}

	/**
	 * Convert to actual trigger
	 * 
	 * @return trigger
	 */
	public InputTrigger toTrigger() {
		return new InputTrigger(this);
	}

	@Override
	public String toString() {
		return event + ":" + attrib;
	}

	/**
	 * Load from string representation
	 * 
	 * @param string string variant EVENT:ATTRIB
	 * @return this
	 */
	public TriggerBundle fromString(String string) {
		if (string == null) return null;
		String[] pts = string.split("[:]");
		event = EInput.valueOf(pts[0]);
		attrib = AiObjParser.getInteger(pts[1]);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TriggerBundle)) return false;

		return ((TriggerBundle) obj).event == event && ((TriggerBundle) obj).attrib == attrib;
	}

	@Override
	public int hashCode() {
		return event.hashCode() ^ attrib;
	}
}
