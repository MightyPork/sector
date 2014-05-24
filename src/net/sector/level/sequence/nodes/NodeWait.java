package net.sector.level.sequence.nodes;


import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.sequence.LevelController;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.porcupine.util.StringUtils;


/**
 * "Pause" node
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeWait extends LevelNodeBase {

	private String waveWait = null;
	private int ticks = 0;
	private int i = 0;
	private boolean infinite;

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeWait(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {
		i = -1;
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

		if (a == null) {
			a = tag.getAttribute("wave");
			if (a == null) a = tag.getAttribute("group");
			if (a == null) a = tag.getAttribute("for");

			if (a != null) {
				waveWait = a.getValue().toLowerCase().trim();
				return;
			}
		}

		if (a == null || StringUtils.isInArray(a.getValue(), false, "-1", "inf", "infinite", "forever", "4ever")) {
			infinite = true;
			return;
		}

		ticks = (int) (AiObjParser.getDouble(a.getValue(), 1) * Constants.FPS_UPDATE);
	}

	@Override
	public boolean execute() {

		if (infinite) return false;

		if (waveWait != null) {
			//if(waveWait.equals("boss")) System.out.println("Wait for wave: "+waveWait);
			if (getLevel().isWaveDead(waveWait)) {
				//System.out.println("Wave "+waveWait+" erradicated.");
				return true;
			}
			//if(waveWait.equals("boss")) System.out.println(getLevel().getWave(waveWait));
			return false;
		}

		i++;
		if (i >= ticks) {
			i = 0;
			return true;
		}

		return false;
	}

}
