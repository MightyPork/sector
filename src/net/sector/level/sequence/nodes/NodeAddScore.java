package net.sector.level.sequence.nodes;


import net.sector.level.SuperContext;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.sequence.LevelController;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.porcupine.math.Range;


/**
 * "Add score" node
 * 
 * @author MightyPork
 */
public class NodeAddScore extends LevelNodeBase {

	private Range score = new Range(0);

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeAddScore(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = (Attribute) Utils.fallback(tag.getAttribute("points"), tag.getAttribute("money"), tag.getAttribute("score"), tag.getAttribute("i"),
				tag.getAttribute("n"));

		score = AiObjParser.getRange(a.getValue(), score);

	}

	@Override
	public boolean execute() {

		int added = score.randInt();
		Log.f3("Adding score points: " + added);
		SuperContext.getGameContext().getCursor().addScore(added);

		return true;
	}

}
