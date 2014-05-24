package net.sector.level.sequence.nodes;


import net.sector.level.dataobj.AiObjParser;
import net.sector.level.sequence.LevelController;
import net.sector.level.sequence.LevelTimer;

import org.jdom2.Attribute;
import org.jdom2.Element;


/**
 * "Setup Timer" node
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeTimerSetup extends LevelNodeBase {

	private int secs = 0;
	private boolean done = false;

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeTimerSetup(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {
		done = false;
	}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = tag.getAttribute("secs");
		if (a == null) a = tag.getAttribute("seconds");
		if (a == null) a = tag.getAttribute("time");
		if (a == null) a = tag.getAttribute("t");
		if (a == null) a = tag.getAttribute("i");
		if (a == null) a = tag.getAttribute("n");
		if (a == null) a = tag.getAttribute("sec");

		secs = AiObjParser.getInteger(a.getValue(), 1);
	}

	@Override
	public boolean execute() {

		if (!done) {
			LevelTimer t = new LevelTimer(secs);
			t.start();

			getLevel().setTimer(t);

			done = true;
		}

		return true;
	}

}
