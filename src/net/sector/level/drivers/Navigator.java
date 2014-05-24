package net.sector.level.drivers;


import java.util.HashMap;
import java.util.Map.Entry;

import net.sector.util.Log;


/**
 * AI drone controller with tasks
 * 
 * @author MightyPork
 */
public class Navigator {
	private INavigated drone;

	private TaskList tasks = new TaskList();

	private HashMap<String, Boolean> taskFlags = new HashMap<String, Boolean>();

	/**
	 * Implicit navigator constructor
	 */
	public Navigator() {}

	/**
	 * Implicit navigator constructor
	 * 
	 * @param tasks AI tasks to be used in this AI
	 */
	public Navigator(TaskList tasks) {
		this.tasks = tasks.copy();
	}

	/**
	 * Navigator constructor for ship
	 * 
	 * @param ship controlled ship
	 */
	public Navigator(INavigated ship) {
		this.drone = ship;
	}

	/**
	 * Set ship to be controlled by this AI
	 * 
	 * @param drone the ship
	 */
	public void setShip(INavigated drone) {
		this.drone = drone;
	}

	/**
	 * Set ship desired speed
	 * 
	 * @param desiredSpeed desired speed
	 */
	public void setShipDesiredSpeed(double desiredSpeed) {
		this.drone.setDesiredSpeed(desiredSpeed);
	}

	/**
	 * Set ship level
	 * 
	 * @param level level to set
	 */
	public void setShipLevel(int level) {
		this.drone.setShipLevel(level);
	}

	/**
	 * Add AI task
	 * 
	 * @param task AI task to add
	 */
	public void addTask(AiTask task) {
		tasks.add(task);
	}

	/**
	 * Clear AI tasks
	 */
	public void clearTasks() {
		tasks.clear();
	}

	/**
	 * Reset AI tasks (erase memories)
	 */
	public void resetTasks() {
		for (AiTask task : tasks) {
			task.reset();
		}
	}

	/**
	 * On entity update hook
	 */
	public void onUpdate() {
		taskFlags.clear();
		for (AiTask task : tasks) {


			if (task.fn == null || task.fn.equals("") || task.fn.equals("none")) continue;
			boolean fnNeg = task.fn.startsWith("!");
			FunctorBase functor = FunctorRegistry.get(task.fn.replace("!", ""));

			if (functor == null) {
				Log.e("AI error: requested functor '" + task.fn + "' does not exist. Task id=" + task.id + ", drone=" + drone);
				continue;
			}

			if (task.type == EAiTaskType.TASK) {

				// check if "if" condition is met.

				String condition = task.typeArg;
				String cond1 = condition;

				if (condition != null && condition.length() != 0) {
					condition = cond1 = condition.toLowerCase();

					for (Entry<String, Boolean> flag : taskFlags.entrySet()) {
						condition = condition.replace(flag.getKey().toLowerCase(), flag.getValue() ? "1" : "0");
					}

					if (!condition.matches("[\\(\\)\\|\\&01\\!\\+\\*]+")) {
						Log.w("AI error: invalid condition pattern in task \"" + cond1 + "\" - possibly flag that hasn't been set yet.\nRemains = "
								+ condition);
						continue;
					}

					int iter = 0;
					condition = condition.replaceAll("\\|\\|", "|");
					condition = condition.replaceAll("\\&\\&", "&");
					condition = condition.replaceAll("\\+", "|");
					condition = condition.replaceAll("\\*", "&");
					while (condition.length() > 1) {
						// remove useless brackets
						condition = condition.replaceAll("\\(([01])\\)", "$1");

						// not
						condition = condition.replaceAll("!0", "1");
						condition = condition.replaceAll("!1", "0");

						// and
						condition = condition.replaceAll("0\\&0", "0");
						condition = condition.replaceAll("0\\&1", "0");
						condition = condition.replaceAll("1\\&0", "0");
						condition = condition.replaceAll("1\\&1", "1");

						// or
						condition = condition.replaceAll("0\\|0", "0");
						condition = condition.replaceAll("0\\|1", "1");
						condition = condition.replaceAll("1\\|0", "1");
						condition = condition.replaceAll("1\\|1", "1");


						iter++;
						if (iter >= 15) break;
					}
				}

				if (condition == null || condition.equals("1")) functor.execute(drone, task.memory, task.args);
			} else if (task.type == EAiTaskType.CHECK) {
				if (task.typeArg == null) continue;
				// perform check.
				taskFlags.put(task.typeArg.toLowerCase(), (fnNeg ? true : false) ^ functor.execute(drone, task.memory, task.args));
			}

		}
	}

	/**
	 * Set driver to this navigator.
	 * 
	 * @param driver the task list.
	 */
	public void setDriver(TaskList driver) {
		if (driver == null) {
			Log.e(new RuntimeException("Null driver assigned to entity " + this + "."));
			return;
		}
		this.tasks = driver.copy();
		//resetTasks();
	}


}
