package net.sector.level.sequence.nodes;


import net.sector.level.sequence.LevelController;

import org.jdom2.Attribute;
import org.jdom2.Element;


/**
 * "Enable spawner" node
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeSpawnerEnable extends LevelNodeBase {

	private String spawnerName = "";

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 * @param xmlTag xml tag this node is loading from
	 */
	public NodeSpawnerEnable(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = tag.getAttribute("id");
		if (a == null) a = tag.getAttribute("name");
		if (a == null) a = tag.getAttribute("spawner");
		if (a == null) a = tag.getAttribute("gen");
		if (a == null) a = tag.getAttribute("generator");

		spawnerName = a.getValue();
	}

	@Override
	public boolean execute() {

		getLevel().enableSpawner(spawnerName, true);

		return true;
	}

}
