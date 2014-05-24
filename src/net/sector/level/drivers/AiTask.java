package net.sector.level.drivers;


import java.util.HashMap;
import java.util.Map;

import net.sector.util.Log;


/**
 * AI task entry
 * 
 * @author MightyPork
 */
public class AiTask {
	/** Task functor name */
	public String fn = "none";
	/** Task functor arguments */
	public Map<String, Object> args = new HashMap<String, Object>(3);
	/** memory for task functor */
	public Map<String, Object> memory = new HashMap<String, Object>(1);

	/** Task type (TASK or TEST) */
	public EAiTaskType type = EAiTaskType.TASK;

	/** Type argument value (argument name is "for" for CHECK, "if" for TASK) */
	public String typeArg = "";

	/** Task ID in driver */
	public String id = "";

	/**
	 * AiTask implicit constructor
	 */
	public AiTask() {}

	/**
	 * Extend this task with other task - replace what's to replace, and change
	 * 
	 * @param extension
	 */
	public void extendWith(AiTask extension) {

		if (extension.type != type) {
			Log.w("Error in AiTask: Trying to extend '" + type + "' with '" + extension.type + "'. Aborting extension.");
			return;
		}

		fn = extension.fn; 				// the function name
		args = extension.args; 			// arguments for task
		if (extension.typeArg != null) typeArg = extension.typeArg; 	// if -or- for

		if (DriverStore.DEBUG) Log.f3("Task '" + id + "' extended -> " + this);
	}

	/**
	 * AiTask as copy of another
	 * 
	 * @param other other task to copy
	 */
	public AiTask(AiTask other) {
		fn = other.fn;
		args = other.args;
		type = other.type;
		typeArg = other.typeArg;
		id = other.id;
		// only memory is new
	}

	/**
	 * Create AI Task
	 * 
	 * @param func functor name
	 * @param taskType type of this task entry
	 * @param typeArg argument for task (condition or output variable)
	 * @param id id of this task
	 * @param args arguments given
	 */
	public AiTask(String func, EAiTaskType taskType, String typeArg, String id, HashMap<String, Object> args) {
		this.fn = func;
		this.args = args;
		this.type = taskType;
		this.typeArg = typeArg;
		this.id = id;
	}

	/**
	 * Reset memory
	 */
	public void reset() {
		memory.clear();
	}

	/**
	 * Get a copy
	 * 
	 * @return copy
	 */
	public AiTask copy() {
		return new AiTask(this);
	}

	@Override
	public String toString() {

		String s = "";
		s += type + "(id: '" + id + "', fn: ";
		s += fn;
		if (type == EAiTaskType.TASK && typeArg != null) s += " ? '" + typeArg + "'";
		if (type == EAiTaskType.CHECK && typeArg != null) s += " → '" + typeArg + "'";
		s += ", ";
		s += "args: " + args;

		s += ")";

		return s;
	}
}
