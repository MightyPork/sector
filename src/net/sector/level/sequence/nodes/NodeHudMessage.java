package net.sector.level.sequence.nodes;


import net.sector.level.dataobj.AiObjParser;
import net.sector.level.sequence.LevelController;
import net.sector.util.Log;

import org.jdom2.Attribute;
import org.jdom2.Element;


/**
 * "Pause" node
 * 
 * @author MightyPork
 */
public class NodeHudMessage extends LevelNodeBase {

	private String message = "Undefined HUD Message";
	private boolean once = false;
	private boolean done = false;

	private double time = 3;

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeHudMessage(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {
		done = false;
	}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = tag.getAttribute("str");
		if (a == null) a = tag.getAttribute("text");
		if (a == null) a = tag.getAttribute("txt");
		//if (a == null) a = tag.getAttribute("t");
		if (a == null) a = tag.getAttribute("msg");
		if (a == null) a = tag.getAttribute("message");

		message = a.getValue();

		a = tag.getAttribute("once");
		if (a != null) once = AiObjParser.getBoolean(a.getValue());

		a = tag.getAttribute("time");
		if (a == null) a = tag.getAttribute("t");
		if (a == null) a = tag.getAttribute("secs");

		if (a != null) time = AiObjParser.getDouble(a.getValue());

	}

	@Override
	public boolean execute() {

		if (once && done) return true;

		done = true;

		Log.f3("New message (" + time + "s): " + message);
		getLevel().addHudMessage(message, time);

		return true;
	}

}
