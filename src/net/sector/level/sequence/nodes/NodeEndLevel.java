package net.sector.level.sequence.nodes;


import net.sector.level.sequence.LevelController;

import org.jdom2.Element;


/**
 * "Pause" node
 * 
 * @author MightyPork
 */
public class NodeEndLevel extends LevelNodeBase {

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeEndLevel(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {}

	@Override
	public boolean execute() {
		getLevel().setEnded();
		return true;
	}

}
