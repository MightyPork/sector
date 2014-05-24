package net.sector.level.drivers;


import java.util.ArrayList;


/**
 * List of AI tasks
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class TaskList extends ArrayList<AiTask> {

	/**
	 * Make a copy
	 * 
	 * @return copy
	 */
	public TaskList copy() {
		TaskList newList = new TaskList();
		for (AiTask task : this) {
			newList.add(task.copy());
		}

		return newList;
	}

}
