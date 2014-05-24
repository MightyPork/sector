package net.sector.level.drivers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sector.Constants;
import net.sector.level.loading.DirectoryLoader;
import net.sector.level.loading.XmlUtil;
import net.sector.util.Log;

import org.jdom2.Element;


/**
 * Driver loader and store
 * 
 * @author MightyPork
 */
public class DriverStore {
	/** store of drivers loaded earlier */
	private List<DriverStore> superStores = new ArrayList<DriverStore>();

	private HashMap<String, TaskList> loadedDrivers = new HashMap<String, TaskList>();

	public static final boolean DEBUG = Constants.LOG_DRIVERS;

	/**
	 * Driver store
	 */
	public DriverStore() {}

	/**
	 * Driver store
	 * 
	 * @param superStores (var-args) already loaded stores to be extended
	 */
	public DriverStore(List<DriverStore> superStores) {
		this.superStores = superStores;
	}

	/**
	 * Driver store
	 * 
	 * @param superStores (var-args) already loaded stores to be extended
	 */
	public DriverStore(DriverStore... superStores) {
		this.superStores = new ArrayList<DriverStore>();
		for (DriverStore st : superStores)
			this.superStores.add(st);
	}

	/**
	 * Add super store (store loaded earlier, with drivers that can be extended)
	 * 
	 * @param store the added store
	 */
	public void addSuperStore(DriverStore store) {
		superStores.add(store);
	}

	/**
	 * Get driver (task list) that has already been loaded from a XML file.
	 * 
	 * @param name driver name
	 * @return task list
	 */
	public TaskList getDriver(String name) {
		if (name == null) {
			Log.w("DriverStore: get ( name = null )");
			return null;
		}
		TaskList tl = loadedDrivers.get(name.toLowerCase());

		if (tl == null) {
			for (DriverStore store : superStores) {
				TaskList tl2 = store.getDriver(name.toLowerCase());
				if (tl2 != null) {
					tl = tl2;
					break;
				}
			}
		}

		if (tl == null) {
			Log.w("Could not find driver " + name + ".");
		}
		return tl;
	}

	/**
	 * Get if this store contains driver of given name
	 * 
	 * @param name driver name
	 * @return is here
	 */
	public boolean hasDriver(String name) {
		return loadedDrivers.containsKey(name);
	}


	/**
	 * Add driver to this store
	 * 
	 * @param name driver name
	 * @param driver task list
	 */
	public void addDriver(String name, TaskList driver) {
		if (loadedDrivers.containsKey(name)) {
			Log.w("DriverStore: adding driver named \"" + name + "\" overwrites another loaded driver.");
		}
		loadedDrivers.put(name, driver);
	}

	/**
	 * Load drivers from a directory, as declared in manifext.xml - which MUST
	 * be present.
	 * 
	 * @param access directory loader
	 */
	public void loadDriversFromDirectory(DirectoryLoader access) {

		Map<String, Element> roots = XmlUtil.loadFromDirectoryWithManifest(access, "drivers");

		if (roots == null) return;

		for (Element elem : roots.values()) {
			loadDriversFromNodeList(elem);
		}

	}

	/**
	 * Load drivers from node list (eg. &lt;drivers&gt;Here are
	 * drivers&lt;/drivers&gt;)
	 * 
	 * @param nodeList node list element
	 */
	public void loadDriversFromNodeList(Element nodeList) {
		List<Element> drivers = nodeList.getChildren("driver");

		for (Element driver : drivers) {
			loadDriverFromNode(driver);
		}
	}

	/**
	 * Load driver from XML node
	 * 
	 * @param driverNode the node with driver
	 */
	public void loadDriverFromNode(Element driverNode) {
		String driverName = empty2null(driverNode.getAttributeValue("name"));
		String extend = empty2null(driverNode.getAttributeValue("extends"));

		// in case user uses "extend" and not "extends"
		if (extend == null) extend = driverNode.getAttributeValue("extend");
		if (driverName == null) driverName = driverNode.getAttributeValue("id");


		if (DEBUG) Log.f2("Starting loading a driver '" + driverName + "' extending '" + extend + "'.");

		if (driverName == null) {
			Log.w("DriveStore: Loaded driver has no name. Loading aborted.");
			return;
		}

		TaskList driverTasks = new TaskList();

		// extend if requested.
		if (extend != null) {
			boolean found = false;
			for (DriverStore store : superStores) {
				if (store.hasDriver(extend)) {
					driverTasks = store.getDriver(extend).copy();
					found = true;
					break;
				}
			}
			if (hasDriver(extend)) {
				driverTasks = getDriver(extend).copy();
				found = true;
			}
			if (!found) {
				Log.w("DriveStore: Driver '" + driverName + "' extends nonexistent driver '" + extend + "'. Loading aborted.");
				return;
			}
		}

		List<Element> taskNodes = driverNode.getChildren();

		/*
		The Task: 
		<check replace="can_fire">
			<fn str="CAN_SHOOT" />	
			<gun num="0" />
			<spare str="ENEMY" />		
		</check>
		
		 */
		if (DEBUG) Log.f3("Loading tasks...");
		for (Element taskNode : taskNodes) {
			AiTask newTask = new AiTask();

			String id = empty2null(taskNode.getAttributeValue("id"));
			String ifA = taskNode.getAttributeValue("if");
			String forA = taskNode.getAttributeValue("for");

			if (ifA != null) ifA = ifA.trim();
			if (forA != null) forA = forA.trim();


			String replace = empty2null(taskNode.getAttributeValue("replace"));
			String before = empty2null(taskNode.getAttributeValue("before"));
			String after = empty2null(taskNode.getAttributeValue("after"));

			String taskTagName = taskNode.getName();
			if (taskTagName.equals("task") || taskTagName.equals("do")) {
				newTask.type = EAiTaskType.TASK;
				newTask.typeArg = ifA;

			} else if (taskTagName.equals("check") || taskTagName.equals("test")) {
				newTask.type = EAiTaskType.CHECK;
				newTask.typeArg = forA;
			}

			newTask.id = id;

			newTask.args = XmlUtil.loadArgs(taskNode);
			newTask.fn = (String) newTask.args.get("fn");
			newTask.args.remove("fn");

			// here the task should be ready.

			if (replace != null || before != null || after != null) {
				String searched = (replace != null ? replace : before != null ? before : after);
				// do replace
				int i = 0;
				boolean foundTask = false;
				for (AiTask t : driverTasks) {
					if (t.id != null && t.id.equalsIgnoreCase(searched)) {
						foundTask = true;
						break;
					}
					i++;
				}

				if (!foundTask) {
					Log.w("DriveStore: Task " + (replace != null ? " replacing '" + replace + "'" : " '" + newTask.id + "'") + " in driver '"
							+ driverName + "' contains invalid before|after|replace value.  Loading aborted.");
					return;
				}

				if (replace != null) {
					AiTask replaced = driverTasks.get(i);
					replaced.extendWith(newTask);
				} else if (before != null) {
					driverTasks.add(i, newTask);
				} else if (after != null) {
					driverTasks.add(i + 1, newTask);
				}
			} else {
				// if no position specified, append
				driverTasks.add(newTask);
			}


			//if (DEBUG) Log.f3("Task" + (replace != null ? " replacing '" + replace + "'" : " '" + newTask.id + "'") + " added to new driver.");
		}


		// the driver should be entirely loaded now
		if (DEBUG) {
			Log.f3("\nAll task in driver " + driverName + ":");

			for (AiTask task : driverTasks) {
				Log.f3(" -> " + task);
			}
			Log.f2("Driver " + driverName + " loaded.\n");
		}

		loadedDrivers.put(driverName, driverTasks);

	}

	private String empty2null(String source) {
		if (source == null) return null;
		if (source.trim().length() == 0) return null;
		return source.trim();
	}

}
