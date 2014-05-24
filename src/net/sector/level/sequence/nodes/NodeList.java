package net.sector.level.sequence.nodes;


import net.sector.level.sequence.LevelController;
import net.sector.level.sequence.LevelNodeRegistry;

import org.jdom2.Element;


/**
 * Node loop
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeList extends LevelNodeBase {

	/**
	 * Create repeat node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 * @param xmlTag xml tag this node is loading from
	 */
	public NodeList(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {
		resetChildren();
	}

	@Override
	public void loadFromXml(Element tag) {
		for (Element e : tag.getChildren()) {
			LevelNodeBase child = LevelNodeRegistry.loadNode(this, getLevel(), e);
			if (child != null) addChild(child);
		}
	}

	@Override
	public boolean execute() {
		return !executeNextChild();
	}


}
