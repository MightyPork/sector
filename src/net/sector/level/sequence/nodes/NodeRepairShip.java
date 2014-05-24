package net.sector.level.sequence.nodes;


import net.sector.level.dataobj.AiObjParser;
import net.sector.level.sequence.LevelController;
import net.sector.level.ship.modules.pieces.Piece;
import net.sector.sounds.Sounds;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.jdom2.Attribute;
import org.jdom2.Element;

import com.porcupine.math.Range;


/**
 * "Add score" node
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class NodeRepairShip extends LevelNodeBase {

	private Range repairRate = new Range(1);

	/**
	 * Create "spawner enable" node
	 * 
	 * @param parent parent node
	 * @param level level controller
	 */
	public NodeRepairShip(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {
		Attribute a;
		a = (Attribute) Utils.fallback(tag.getAttribute("rate"), tag.getAttribute("strength"), tag.getAttribute("i"), tag.getAttribute("n"));

		repairRate = AiObjParser.getRange(a.getValue(), repairRate);

	}

	@Override
	public boolean execute() {
		double rate = repairRate.randDouble();
		Log.f3("Repairing ship with rate " + rate);

		getScene().getPlayerShip().body.energySystem.fill();
		getScene().getPlayerShip().body.shieldSystem.fill();

		for (Piece p : getScene().getPlayerShip().body.allPieces) {
			if (!p.isDead) {
				p.addHealth(p.getHealthMax() * rate);
			}
		}

		Sounds.des_repair.playEffect(1f, 0.5f, false);

		return true;
	}

}
