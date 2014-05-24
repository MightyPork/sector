package net.sector.level.sequence.nodes;


import net.sector.level.dataobj.AiObjParser;
import net.sector.level.sequence.LevelController;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.porcupine.coord.Vec;


/**
 * "Pause" node
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeSetGlobalMovement extends LevelNodeBase {

	private Vec movement = new Vec(0, 0, -1);

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeSetGlobalMovement(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = tag.getAttribute("move");
		if(a==null) a = tag.getAttribute("dir");
		if(a==null) a = tag.getAttribute("vec");
		if(a==null) a = tag.getAttribute("coord");

		movement = new Vec(AiObjParser.getCoord(a.getValue()).toCoord(null));
	}

	@Override
	public boolean execute() {

		getLevel().setGlobalMovement(movement);

		return true;
	}

}
