package net.sector.level.sequence.nodes;


import java.util.Map;

import net.sector.level.loading.XmlUtil;
import net.sector.level.sequence.LevelController;
import net.sector.level.spawners.EnemySpawner;
import net.sector.level.spawners.SpawnerBase;
import net.sector.util.Utils;

import org.jdom2.Attribute;
import org.jdom2.Element;


/**
 * "Setup entity spawner" node - (ships)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeSetupEntitySpawner extends LevelNodeBase {

	private String name;
	private Map<String, Object> args;

	/**
	 * "Setup entity spawner" node
	 * 
	 * @param parent parent tag
	 * @param level level controller
	 * @param xmlTag xml tag to load from
	 */
	public NodeSetupEntitySpawner(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a = (Attribute) Utils.fallback(tag.getAttribute("name"), tag.getAttribute("id"));

		if (a != null) name = a.getValue();
		args = XmlUtil.loadArgs(tag);
	}

	@Override
	public boolean execute() {
		SpawnerBase sp = null;
		if ((sp = getLevel().getSpawner(name)) != null) {
			sp.loadFromXmlArgs(args);
			return true;
		}

		SpawnerBase spawner = new EnemySpawner(getScene());
		spawner.loadFromXmlArgs(args);
		getLevel().addSpawner(name, spawner);
		return true;
	}


}
