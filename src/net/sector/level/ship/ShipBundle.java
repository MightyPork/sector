package net.sector.level.ship;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.sector.input.TriggerBundle;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.loading.XmlUtil;
import net.sector.level.ship.modules.EnergySystem;
import net.sector.level.ship.modules.Shield;
import net.sector.util.Log;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.porcupine.math.Calc;


/**
 * Player ship bundle
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ShipBundle {

	/** Ship design [Z][X] */
	public PieceBundle[][] ship = new PieceBundle[1][1];

	/** Shield level */
	public int shieldLevel = 1;

	/** Energy level */
	public int energyLevel = 1;

	/**
	 * Create new ship bundle.
	 * 
	 * @param shipBody ship design
	 * @param shield shields
	 * @param energy energy
	 */
	public ShipBundle(PieceBundle[][] shipBody, int shield, int energy) {
		ship = shipBody;
		if (ship == null) {
			ship = new PieceBundle[][] { { null } };
		}
		shieldLevel = shield;
		energyLevel = energy;
	}

	/**
	 * Create new ship bundle.
	 */
	public ShipBundle() {}


	/**
	 * Create new context.
	 * 
	 * @param other bundle to copy
	 */
	public ShipBundle(ShipBundle other) {
		// copy the ship.
		PieceBundle[][] newShip = new PieceBundle[other.ship.length][];
		for (int i = 0; i < other.ship.length; i++)
			newShip[i] = other.ship[i].clone();
		ship = newShip;

		shieldLevel = other.shieldLevel;
		energyLevel = other.energyLevel;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return copy();
	}

	/**
	 * Get power system level
	 * 
	 * @return level
	 */
	public int getEnergyLevel() {
		return energyLevel;
	}

	/**
	 * Get shield level
	 * 
	 * @return level
	 */
	public int getShieldLevel() {
		return shieldLevel;
	}

	/**
	 * Get ship design table
	 * 
	 * @return table
	 */
	public PieceBundle[][] getShipDesign() {
		return ship;
	}

	/**
	 * Get copy
	 * 
	 * @return copy
	 */
	public ShipBundle copy() {
		return new ShipBundle(this);
	}

	/**
	 * Write as XML to a stream
	 * 
	 * @param out output stream
	 * @throws IOException on error
	 */
	public void xmlToStream(OutputStream out) throws IOException {
		Document doc = new Document();
		Element root = new Element("ship");
		doc.setRootElement(root);


		// write dimensions
		Element dim = new Element("dim");
		dim.setAttribute("x", "" + ship[0].length);
		dim.setAttribute("y", "" + ship.length);
		root.addContent(dim);

		Element sys = new Element("sys");
		sys.setAttribute("energy", "" + energyLevel);
		sys.setAttribute("shield", "" + shieldLevel);
		root.addContent(sys);

		// write ship content
		Element struct = new Element("struct");
		for (PieceBundle[] pbr : ship) {

			int notNull = 0;
			for (PieceBundle pb : pbr) {
				if (pb != null) notNull++;
			}

			if (notNull == 0) {
				struct.addContent(new Element("null"));
			} else {
				Element row = new Element("row");
				for (PieceBundle pb : pbr) {
					if (pb == null) {
						row.addContent(new Element("null"));
					} else {
						Element piece = new Element("piece");

						piece.setAttribute("id", pb.id);
						piece.setAttribute("level", pb.level + "");
						piece.setAttribute("rotate", pb.rotate + "");
						piece.setAttribute("health", pb.health + "");
						if (pb.hasTrigger) piece.setAttribute("trigger", pb.trigger.toString());
						row.addContent(piece);
					}
				}
				struct.addContent(row);
			}
		}
		root.addContent(struct);

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(doc, out);
	}

	/**
	 * Get total ship & systems cost.
	 * 
	 * @return total cost.
	 */
	public int getTotalCost() {
		int cost = 0;

		cost += getShieldCost();
		cost += getEnergyCost();

		// add cost of systems

		for (PieceBundle[] pbr : ship) {
			for (PieceBundle pb : pbr) {
				if (pb == null) {
					continue;
				} else {
					cost += pb.getTotalValue();
				}
			}
		}

		return cost;
	}

	/**
	 * Get shield price (level 1 to current level)
	 * 
	 * @return shield price
	 */
	public int getShieldCost() {
		if (shieldLevel > 0) return PieceRegistry.getLevelChangeCost(Shield.getBaseCost(), 1, shieldLevel);
		return 0;
	}

	/**
	 * Get energy system price (level 1 to current level)
	 * 
	 * @return energy price
	 */
	public int getEnergyCost() {
		if (energyLevel > 0) return PieceRegistry.getLevelChangeCost(EnergySystem.getBaseCost(), 1, energyLevel);
		return 0;
	}

	/**
	 * Reduce shield and energy and piece levels to match discovery table (set
	 * pieces to nuill if not discovered).
	 * 
	 * @param dt discovery table.
	 */
	public void reduceForDiscoveryTable(DiscoveryTable dt) {
		int yy = 0;
		for (PieceBundle[] pbr : ship) {
			int xx = 0;
			for (PieceBundle pb : pbr) {
				if (pb == null) {
					xx++;
					continue;
				} else {
					if (dt.getDiscoveryLevelForPiece(pb.id) >= 1) {
						pb.level = Calc.clampi(pb.level, 1, dt.getDiscoveryLevelForPiece(pb.id));
					} else {
						ship[yy][xx] = null;
					}
				}
				xx++;

			}
			yy++;
		}

		if (dt.getDiscoveryLevel("shield") == 0) {
			shieldLevel = 0;
		} else {
			shieldLevel = Calc.clampi(shieldLevel, 1, dt.getDiscoveryLevel("shield"));
		}
		energyLevel = Calc.clampi(energyLevel, 1, dt.getDiscoveryLevel("energy"));

	}

	/**
	 * Try to decrease piece levels / system levels so that the total cost fits
	 * into a given limit.<br>
	 * If it isn't possible, erase the ship bundle.
	 * 
	 * @param cost max total cost
	 */
	public void reduceForTotalCost(int cost) {

		ArrayList<PieceBundle> pieces = new ArrayList<PieceBundle>();
		for (PieceBundle[] pbr : ship) {
			for (PieceBundle pb : pbr) {
				if (pb == null) {
					continue;
				} else {
					pieces.add(pb);
				}
			}
		}

		int iterations = 0;
		while (true) {
			iterations++;

			if (getTotalCost() <= cost) return;

			Collections.sort(pieces, new Comparator<PieceBundle>() {
				@Override
				public int compare(PieceBundle o1, PieceBundle o2) {

					Integer a = o1.getTotalValue();
					Integer b = o1.getTotalValue();

					return -(a.compareTo(b));
				}
			});



			int max = pieces.get(0).getTotalValue();
			max = Math.max(max, getShieldCost());
			max = Math.max(max, getEnergyCost());

			if (shieldLevel > 1 && getShieldCost() >= max) shieldLevel--;

			if (getTotalCost() <= cost) return;

			if (energyLevel > 1 && getEnergyCost() >= max) energyLevel--;

			if (getTotalCost() <= cost) return;

			for (PieceBundle pb : pieces) {
				if (pb.getTotalValue() >= max && pb.level > 1) {
					pb.level--;

					if (getTotalCost() <= cost) return;
				}
			}

			if (getTotalCost() <= cost) return;

			if (iterations > 500) {
				Log.f3("Failed to reduce ship for cost " + cost + ", turning to nothing.");

				shieldLevel = 1;
				energyLevel = 1;

				ship = new PieceBundle[1][1];

				break;
			}
		}
	}

	/**
	 * Create from XML node
	 * 
	 * @param root ship design root node (&lt;ship&gt;)
	 * @return this
	 */
	public ShipBundle xmlFromElement(Element root) {
		// get dimensions
		Element dim = root.getChild("dim");
		int x = AiObjParser.getInteger(dim.getAttributeValue("x"), 1);
		int y = AiObjParser.getInteger(dim.getAttributeValue("y"), 1);


		// get system levels
		Element sys = root.getChild("sys");
		energyLevel = AiObjParser.getInteger(sys.getAttributeValue("energy"), 1);
		shieldLevel = AiObjParser.getInteger(sys.getAttributeValue("shield"), 1);


		// write ship content
		Element struct = root.getChild("struct");


		// load structure
		ship = new PieceBundle[y][x];

		int yy = 0;
		int xx = 0;
		for (Element row : struct.getChildren()) {
			xx = 0;
			if (row.getName().equals("row")) {

				for (Element piece : row.getChildren()) {

					// if "null", skip.
					if (piece.getName().equals("piece")) {
						String id = piece.getAttributeValue("id");
						int level = AiObjParser.getInteger(piece.getAttributeValue("level"), 1);
						int rotate = AiObjParser.getInteger(piece.getAttributeValue("rotate"), 0);
						int health = AiObjParser.getInteger(piece.getAttributeValue("health"), 1);

						PieceBundle pb;
						pb = ship[yy][xx] = new PieceBundle(id, level, rotate, health);
						if (pb.hasTrigger) {
							String triggerText = piece.getAttributeValue("trigger");
							if (triggerText != null) {
								TriggerBundle tb = new TriggerBundle().fromString(triggerText);
								pb.setTrigger(tb);
							}
						}
					}

					xx++;
				}
			}
			yy++;
		}

		return this;
	}


	/**
	 * Load from XML stream (file)
	 * 
	 * @param in input stream
	 * @throws IOException on error
	 */
	public void xmlFromStream(InputStream in) throws IOException {

		Element root = XmlUtil.getRootElement(in);

		xmlFromElement(root);
	}
}
