package net.sector.level.sequence.nodes;


import java.util.Map;

import net.sector.level.loading.XmlUtil;
import net.sector.level.sequence.LevelController;
import net.sector.level.spawners.ScoreSpawner;
import net.sector.level.spawners.SpawnerBase;
import net.sector.util.Utils;

import org.jdom2.Attribute;
import org.jdom2.Element;


/**
 * "Setup score spawner" node - (score)
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeSetupScoreSpawner extends LevelNodeBase {

	private String name;
	private Map<String, Object> args;

	/**
	 * "Setup score spawner" node
	 * 
	 * @param parent parent tag
	 * @param level level controller
	 */
	public NodeSetupScoreSpawner(LevelNodeBase parent, LevelController level) {
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

		SpawnerBase spawner = new ScoreSpawner(getScene());
		spawner.loadFromXmlArgs(args);
		getLevel().addSpawner(name, spawner);
		return true;
	}


}
