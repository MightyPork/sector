package net.sector.input;


import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Function executor triggered by input event.
 * 
 * @author MightyPork
 */
public class InputTriggeredAction implements IInputHandler {

	/** Trigger used to detect the event */
	private InputTrigger[] triggers = null;
	/** Function called when triggered */
	private Routine function = null;

	/**
	 * Function controller triggered by input event.
	 * 
	 * @param event event detected
	 * @param attrib event attribute
	 * @param func function to run
	 */
	public InputTriggeredAction(Routine func, EInput event, int attrib) {
		triggers = new InputTrigger[] { new InputTrigger(event, attrib) };
		function = func;
	}

	/**
	 * Function controller triggered by input event.
	 * 
	 * @param func function to run
	 * @param triggers triggers (AND)
	 */
	public InputTriggeredAction(Routine func, InputTrigger... triggers) {
		this.triggers = triggers;
		function = func;
	}

	/**
	 * Replace function.
	 * 
	 * @param func new function
	 */
	public void replaceFunction(Routine func) {
		this.function = null;
		this.function = func;
	}

	/**
	 * Replace trigger(s)
	 * 
	 * @param triggers the triggers
	 */
	public void setTriggers(InputTrigger... triggers) {
		this.triggers = null;
		this.triggers = triggers;
	}

	/**
	 * Replace trigger
	 * 
	 * @param event event detected
	 * @param attrib event attribute
	 */
	public void setTrigger(EInput event, int attrib) {
		this.triggers = null;
		triggers = new InputTrigger[] { new InputTrigger(event, attrib) };
	}

	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {
		// NO-OP
	}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		for (InputTrigger t : triggers) {
			if (t.isStatic) {
				if (!t.handleStaticInputs()) return;
			} else {
				if (!t.onMouseButton(button, down, wheelDelta, pos, deltaPos)) return;

			}
		}

		function.run();
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		for (InputTrigger t : triggers) {
			if (t.isStatic) {
				if (!t.handleStaticInputs()) return;
			} else {
				if (!t.onKey(key, c, down)) return;
			}
		}

		function.run();
	}

	@Override
	public void handleStaticInputs() {
		for (InputTrigger t : triggers) {
			if (!t.handleStaticInputs()) return;
		}

		function.run();
	}

	/**
	 * Get all triggers
	 * 
	 * @return triggers
	 */
	public InputTrigger[] getTriggers() {
		return triggers;
	}
}
