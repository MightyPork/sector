package net.sector.level.sequence.nodes;


import java.util.ArrayList;

import net.sector.collision.Scene;
import net.sector.level.drivers.DriverStore;
import net.sector.level.sequence.LevelController;

import org.jdom2.Element;


/**
 * Level algorithm node - base class.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class LevelNodeBase {
	private LevelNodeBase parent;
	private LevelController level;
	private int lastExecutedChild = -1;
	private ArrayList<LevelNodeBase> children = new ArrayList<LevelNodeBase>(5);

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * Create sequence node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public LevelNodeBase(LevelNodeBase parent, LevelController level) {
		this.parent = parent;
		this.level = level;
	}

	/**
	 * Load this node from a XML tag
	 * 
	 * @param tag node xml tag
	 */
	public abstract void loadFromXml(Element tag);

	/**
	 * Get level controller (to access spawners etc)
	 * 
	 * @return level controller
	 */
	public final LevelController getLevel() {
		return level;
	}

	/**
	 * Get driver store
	 * 
	 * @return driver store for level
	 */
	public final DriverStore getDriverStore() {
		return level.getDriverStore();
	}

	/**
	 * Get level scene
	 * 
	 * @return scene
	 */
	public final Scene getScene() {
		return getLevel().getScene();
	}

	/**
	 * Get parent node
	 * 
	 * @return parent node
	 */
	public final LevelNodeBase getParent() {
		return parent;
	}

	/**
	 * Add a child node
	 * 
	 * @param node child node to add
	 */
	public final void addChild(LevelNodeBase node) {
		//System.out.println("Add child to " + this + " → " + node);
		children.add(node);
	}

	/**
	 * Get if this list has more children
	 * 
	 * @return has more children
	 */
	public final boolean hasMoreChildren() {
		return lastExecutedChild < children.size() - 1;
	}

	/**
	 * Execute next child in list, if any
	 * 
	 * @return true on success, false on end reached (repeat or continue to next
	 *         in parent)
	 */
	public final boolean executeNextChild() {
		if (!hasMoreChildren()) {
			return false;
		}
		lastExecutedChild++;
		boolean move = children.get(lastExecutedChild).execute();
		if (!move) lastExecutedChild--;
		return true;
	}

	/**
	 * Move cursor to first child.
	 */
	public final void gotoFirstChild() {
		lastExecutedChild = -1;
	}

	/**
	 * Execute this node
	 * 
	 * @return true if finished (false to repeat)
	 */
	public abstract boolean execute();

	public abstract void reset();

	public final void resetChildren() {
		gotoFirstChild();
		for (LevelNodeBase c : children)
			c.reset();
	}
}
