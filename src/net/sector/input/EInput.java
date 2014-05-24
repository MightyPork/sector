package net.sector.input;


import org.newdawn.slick.util.Log;


/**
 * Enum of event types for trigger.
 * 
 * @author MightyPork
 */
public enum EInput {
	/** Keyboard key pressed */
	KEY_PRESS,
	/** Keyboard key held down */
	KEY_DOWN,
	/** Keyboard key not pressed */
	KEY_UP,
	/** Keyboard key released */
	KEY_RELEASE,
	/** Wheel scrolled */
	SCROLL,
	/** Mouse button pressed */
	BTN_PRESS,
	/** Mouse button held down */
	BTN_DOWN,
	/** Mouse button not pressed */
	BTN_UP,
	/** Mouse button released */
	BTN_RELEASE;

	/**
	 * Get state / change variant
	 * 
	 * @param beStatic is long-lasting state (key held down)
	 * @return the modified variant.
	 */
	public EInput variant(boolean beStatic) {
		if (beStatic) {
			switch (this) {
				case BTN_PRESS:
				case BTN_DOWN:
					return BTN_DOWN;
				case BTN_UP:
				case BTN_RELEASE:
					return BTN_UP;
				case SCROLL:
					return SCROLL;
				case KEY_PRESS:
				case KEY_DOWN:
					return KEY_DOWN;
				case KEY_UP:
				case KEY_RELEASE:
					return KEY_UP;
			}
		} else {
			switch (this) {
				case BTN_PRESS:
				case BTN_DOWN:
					return BTN_PRESS;
				case BTN_UP:
				case BTN_RELEASE:
					return BTN_RELEASE;
				case SCROLL:
					return SCROLL;
				case KEY_PRESS:
				case KEY_DOWN:
					return KEY_PRESS;
				case KEY_UP:
				case KEY_RELEASE:
					return KEY_RELEASE;
			}
		}

		Log.warn("Invalid EInput constant in EInput.variant: " + this);

		return this;

	}
}
