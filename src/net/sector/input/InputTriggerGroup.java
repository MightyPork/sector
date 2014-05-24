package net.sector.input;


import java.util.HashSet;
import java.util.Set;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Group of input triggers, able to detect multiple events separately.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class InputTriggerGroup implements IInputHandler {


	/** triggered actions */
	public Set<InputTriggeredAction> triggers = new HashSet<InputTriggeredAction>();

	/**
	 * Add new triggered action
	 * 
	 * @param action the action
	 * @return the action trigger added
	 */
	public InputTriggeredAction addTrigger(InputTriggeredAction action) {
		InputTriggeredAction a;
		triggers.add(a = action);
		return a;
	}

	/**
	 * Add new triggered action
	 * 
	 * @param triggers action trigger(s)
	 * @param func action
	 * @return the action trigger added
	 */
	public InputTriggeredAction addTrigger(Routine func, InputTrigger... triggers) {
		InputTriggeredAction a;
		this.triggers.add(a = new InputTriggeredAction(func, triggers));
		return a;
	}

	/**
	 * Add new triggered action
	 * 
	 * @param event event detected
	 * @param attrib event attribute
	 * @param func action
	 * @return the action trigger added
	 */
	public InputTriggeredAction addTrigger(Routine func, EInput event, int attrib) {
		InputTriggeredAction a;
		triggers.add(a = new InputTriggeredAction(func, event, attrib));
		return a;
	}

	/**
	 * Add new triggered action
	 * 
	 * @param func action
	 * @param event1 1st event type
	 * @param attrib1 1st event attriv
	 * @param event2 2nd event type
	 * @param attrib2 2nd type attrib
	 * @return the action trigger added
	 */
	public InputTriggeredAction addTrigger(Routine func, EInput event1, int attrib1, EInput event2, int attrib2) {
		InputTriggeredAction a;
		triggers.add(a = new InputTriggeredAction(func, new InputTrigger(event1, attrib1), new InputTrigger(event2, attrib2)));
		return a;
	}

	/**
	 * Remove trigger action (eg. to rebuild it)
	 * 
	 * @param action the action.
	 */
	public void removeTrigger(InputTriggeredAction action) {
		if (action == null) return;
		triggers.remove(action);
	}

	/**
	 * Remove all action triggers
	 */
	public void clearTriggers() {
		triggers.clear();
	}

	@Override
	public void onKey(int key, char c, boolean down) {
		for (InputTriggeredAction action : triggers) {
			action.onKey(key, c, down);
		}
	}

	@Override
	public void onMouseButton(int button, boolean down, int wheelDelta, Coord pos, Coord deltaPos) {
		for (InputTriggeredAction action : triggers) {
			action.onMouseButton(button, down, wheelDelta, pos, deltaPos);
		}
	}

	@Override
	public void onMouseMove(Coord pos, Vec move, int wheelDelta) {}

	@Override
	public void handleStaticInputs() {
		for (InputTriggeredAction action : triggers) {
			action.handleStaticInputs();
		}
	}
}
