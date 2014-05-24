package net.sector.level.sequence.nodes;


import net.sector.level.sequence.LevelController;
import net.sector.level.sequence.LevelNodeRegistry;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;


/**
 * Node loop
 * 
 * @author MightyPork
 */
public class NodeCycle extends LevelNodeBase {

	private int repeatCount = 1;
	private int i = -1;

	/**
	 * Create repeat node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 * @param xmlTag xml tag this node is loading from
	 */
	public NodeCycle(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {
		i = -1;
		resetChildren();
	}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = tag.getAttribute("count");
		if (a == null) a = tag.getAttribute("cnt");
		if (a == null) a = tag.getAttribute("i");
		if (a == null) a = tag.getAttribute("times");
		if (a == null) a = tag.getAttribute("n");

		if (a == null || a.getValue().equals("-1") || a.getValue().equals("inf") || a.getValue().equals("infinite")) {
			repeatCount = -1;
		} else {
			try {
				repeatCount = a.getIntValue();
			} catch (DataConversionException e) {
				e.printStackTrace();
			}
		}

		for (Element e : tag.getChildren()) {
			LevelNodeBase node = LevelNodeRegistry.loadNode(this, getLevel(), e);
			if (node != null) addChild(node);
		}
	}

	@Override
	public boolean execute() {
		if (i == -1) {
			i = 0; // begin cycle
			gotoFirstChild(); // rewind
		}

		// if more children, call one and end.
		if (executeNextChild()) {
			return false; // repeat
		}

		gotoFirstChild(); // rewind		
		i++; // next loop

		// infinite!
		if (repeatCount == -1) return false;

		if (i >= repeatCount) {
			i = -1;
			return true; // break
		}
		return false; // repeat
	}


}
