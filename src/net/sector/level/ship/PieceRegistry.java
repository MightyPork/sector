package net.sector.level.ship;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sector.level.ship.modules.pieces.Piece;
import net.sector.level.ship.modules.pieces.body.*;
import net.sector.level.ship.modules.pieces.weapons.*;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.Log;

import com.porcupine.color.RGB;
import com.porcupine.math.Calc;


/**
 * Table of pieces.
 * 
 * @author MightyPork
 */
public class PieceRegistry {

	public static class PieceEntry {
		public String id;
		public String discovery;
		public String group;
		public String label;
		public RenderModel model;
		public int baseCost;
		public Class<? extends Piece> pieceClass;

		public PieceEntry(String id, String discovery, String pieceGroup, String label, Class<? extends Piece> pieceClass) {
			this.discovery = discovery;
			this.group = pieceGroup;
			this.label = label;
			this.pieceClass = pieceClass;
			this.id = id;

			try {
				Piece inst = pieceClass.newInstance();
				model = inst.getModel();
				baseCost = inst.getBaseCost();
			} catch (InstantiationException e) {
				Log.e("Error registering piece.", e);
			} catch (IllegalAccessException e) {
				Log.e("Error registering piece.", e);
			}
		}
	}

	/** Human readable labels for piece groups */
	public static LinkedHashMap<String, String> groups = new LinkedHashMap<String, String>();

	public static Map<String, PieceEntry> pieces = new LinkedHashMap<String, PieceEntry>();

	public static String designerDefaultGroup = null;



	/**
	 * initialize static fields
	 */
	public static void init() {
		Log.f1("Initializing PieceRegistry...");

		//@formatter:off

		
		String gBODY, gWEAPONS, gENGINES;
		// piece groups
		registerPieceGroup(gBODY = "body", 					"Body");
		registerPieceGroup(gWEAPONS = "weapons", 			"Guns");
		registerPieceGroup(gENGINES = "propulsion", 		"Engines");
		

		String dBODY, dENGINE_R, dENGINE_I, dCANNON, dLASER, dPLASMA, dEMP, dFIRE, dROCKET, dROCKET_G;
		
		// piece discoveries
		dBODY = "body";
				
		dENGINE_R = "engineRocket";
		dENGINE_I = "engineIon";	
		
		dCANNON = "cannon";
		dLASER = "laser";
		dPLASMA = "plasma";
		dROCKET = "rocket";
		dROCKET_G = "rocket_guided";
		dEMP = "emp";
		dFIRE = "fireball";


		// pieces
		
		// body box
		registerPiece("bb_cube", 		dBODY, 		gBODY, 		"Cube", 			PieceBbCube.class);
		registerPiece("bb_triangle",	dBODY, 		gBODY, 		"Triangle", 		PieceBbTriangle.class);
		registerPiece("bb_arrow", 		dBODY, 		gBODY, 		"Arrow", 			PieceBbArrow.class);
		registerPiece("bb_point", 		dBODY, 		gBODY, 		"Point", 			PieceBbPoint.class);
		
		// body smooth
		// one side
		registerPiece("bs_side1", 			dBODY, 		gBODY, 		"Border 1 side", 				PieceBsSide1.class);
		registerPiece("bs_side2_next", 		dBODY, 		gBODY, 		"Border 2 sides next",			PieceBsSide2Next.class);
		registerPiece("bs_side2_opp", 		dBODY, 		gBODY, 		"Border 2 sides opposite",		PieceBsSide2Opp.class);
		registerPiece("bs_side3", 			dBODY, 		gBODY, 		"Border 3 sides",				PieceBsSide3.class);
		registerPiece("bs_corner1", 		dBODY, 		gBODY, 		"Border 1 corner", 				PieceBsCorner1.class);
		registerPiece("bs_corner2_next", 	dBODY, 		gBODY, 		"Border 2 corners next",		PieceBsCorner2Next.class);
		registerPiece("bs_corner2_opp", 	dBODY, 		gBODY, 		"Border 2 corners opposite",	PieceBsCorner2Opp.class);
		registerPiece("bs_corner3", 		dBODY, 		gBODY, 		"Border 3 corners", 			PieceBsCorner3.class);
		registerPiece("bs_corner4", 		dBODY, 		gBODY, 		"Border 4 corners", 			PieceBsCorner4.class);
		registerPiece("bs_corner1_side1", 	dBODY, 		gBODY, 		"Border 1 corner 1 side",		PieceBsCorner1Side1.class);
		registerPiece("bs_corner1_side1_m",	dBODY, 		gBODY, 		"Border 1 corner 1 side",		PieceBsCorner1Side1M.class);
		registerPiece("bs_corner2_side1", 	dBODY, 		gBODY, 		"Border 2 corners 1 side",		PieceBsCorner2Side1.class);
		registerPiece("bs_corner1_side2", 	dBODY, 		gBODY, 		"Border 1 corner 2 side",		PieceBsCorner1Side2.class);
		registerPiece("bs_point", 			dBODY, 		gBODY, 		"Border Point", 				PieceBsPoint.class);		
		registerPiece("bs_triangle", 		dBODY, 		gBODY, 		"Border Triangle", 				PieceBsTriangle.class);


		//body wing
		registerPiece("bw_cube", 		dBODY, 		gBODY, 		"Square Wing", 		PieceBwCube.class);
		registerPiece("bw_triangle", 	dBODY, 		gBODY, 		"Triangle Wing", 	PieceBwCorner.class);
		registerPiece("bw_arrow", 		dBODY, 		gBODY, 		"Arrow Wing", 		PieceBwPoint.class);

		// engine
		registerPiece("engine_rocket", 		dENGINE_R, 	gENGINES,	"Rocket Engine", 	PieceEngineRocket.class);
		registerPiece("engine_ion", 		dENGINE_I, 	gENGINES,	"Ion Engine",		PieceEngineIon.class);

		// weapons
		registerPiece("w_cannon", 		dCANNON, 	gWEAPONS, 	"Cannon", 			PieceCannon.class);
		registerPiece("w_laser", 		dLASER, 	gWEAPONS, 	"Laser Gun", 		PieceLaser.class);
		registerPiece("w_plasma", 		dPLASMA, 	gWEAPONS, 	"Plasma Gun", 		PiecePlasmaGun.class);
		registerPiece("w_emp", 			dEMP, 		gWEAPONS, 	"EMP Launcher", 	PieceEMPGun.class);
		registerPiece("w_rocket", 		dROCKET, 	gWEAPONS, 	"Rocket Launcher", 	PieceRocketLauncher.class);
		registerPiece("w_rocket_g", 	dROCKET_G, 	gWEAPONS, 	"Guided Rocket Launcher", 	PieceRocketLauncherGuided.class);
		registerPiece("w_fireball", 	dFIRE, 		gWEAPONS, 	"Flamethrower", 	PieceFlamethrower.class);

		//@formatter:on
	}

	/**
	 * Get piece damage color.
	 * 
	 * @param health piece health
	 * @param healthMax max health
	 * @return color
	 */
	public static RGB getDamageColor(double health, double healthMax) {
		RGB clr = new RGB(0, 0, 0);
		double yellow = healthMax * 0.7;
		if (health <= yellow) {
			clr.r = 1;
			clr.g = health / yellow;
		} else if (health < healthMax) {
			clr.r = Calc.clampd(1 - (health - yellow) / yellow, 0, 1);
			clr.g = 1;
		} else {
			clr.r = 0;
			clr.g = 1;
		}
		return clr;
	}

//
//	/**
//	 * Register discoverable key
//	 * 
//	 * @param id discovery key
//	 * @param label human readable discovery name
//	 * @param defaultLevel initial discovery level (0 = unavailable)
//	 * @param maxLevel max discovery level
//	 * @param dependencies dependencies name, level, name, level...
//	 */
//	public static void registerDiscovery(String id, String label, int defaultLevel, int maxLevel, Object... dependencies) {
//
//		DiscoveryRegistry.registerDiscovery(id, label, defaultLevel, maxLevel, dependencies);
//	}

	/**
	 * Register piece group
	 * 
	 * @param id group name
	 * @param label human readable label
	 */
	public static void registerPieceGroup(String id, String label) {
		groups.put(id, label);
		if (designerDefaultGroup == null) designerDefaultGroup = id;
	}

	/**
	 * Register new piece
	 * 
	 * @param id unique identifier
	 * @param discovery key for discovery table
	 * @param group piece group
	 * @param label label for ship designer and similar
	 * @param pieceClass class of the piece
	 */
	public static void registerPiece(String id, String discovery, String group, String label, Class<? extends Piece> pieceClass) {

		pieces.put(id, new PieceEntry(id, discovery, group, label, pieceClass));

		if (!DiscoveryRegistry.discoveryExists(discovery)) {
			Log.w("Ship piece " + id + " registered with unknown discovery key " + discovery + "!");
		}

	}

	/**
	 * Get piece name from piece instance.
	 * 
	 * @param p piece instance
	 * @return name
	 */
	public static String getPieceName(Piece p) {
		for (Entry<String, PieceEntry> entry : pieces.entrySet()) {
			if (p.getClass() == entry.getValue().pieceClass) return entry.getKey();
		}
		throw new RuntimeException("Class of piece " + p + " could not be resolved.");
	}

	/**
	 * Get maximal level for piece instance
	 * 
	 * @param piece piece instance
	 * @return piece max level
	 */
	public final static int getPieceLevelMax(Piece piece) {
		String pieceName = getPieceName(piece);
		return getPieceLevelMax(pieceName);
	}

	/**
	 * Get maximal level for piece name
	 * 
	 * @param id piece name
	 * @return max level
	 */
	public static int getPieceLevelMax(String id) {
		String discoveryName = getPieceDiscoveryKey(id);
		return DiscoveryRegistry.getDiscoveryLevelMax(discoveryName);
	}

	/**
	 * Build new ship piece for name
	 * 
	 * @param id piece name
	 * @return the piece
	 */
	public static Piece makePiece(String id) {
		return makePiece(id, 1, 0);
	}

	/**
	 * Build new ship piece for name
	 * 
	 * @param id piece name
	 * @param level piece level
	 * @return the piece
	 */
	public static Piece makePiece(String id, int level) {
		return makePiece(id, level, 0);
	}

	/**
	 * Build new ship piece for name
	 * 
	 * @param id piece name
	 * @param level piece level
	 * @param rotate piece rotate
	 * @return the piece
	 */
	public static Piece makePiece(String id, int level, int rotate) {
		try {
			Piece p = pieces.get(id).pieceClass.newInstance();
			p.setPieceRotate(rotate);
			p.setLevel(level);
			return p;
		} catch (Exception e) {
			Log.e("Cant make piece for: " + id);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get model for piece
	 * 
	 * @param id piece name
	 * @return the model
	 */
	public static RenderModel getModel(String id) {
		return pieces.get(id).model;
	}


	/**
	 * Calculate how much upgrade / downgrade will cost.
	 * 
	 * @param base base cost (level 1)
	 * @param from level from (before up/down-grade)
	 * @param to level to (after up/down-grade)
	 * @return cost (negative if money was returned)
	 */
	public static int getLevelChangeCost(int base, int from, int to) {
		return (getLevelCost(base, to) - getLevelCost(base, from));
	}

	/**
	 * Get level cost
	 * 
	 * @param base base cost (level 1)
	 * @param level current level
	 * @return cost
	 */
	public static int getLevelCost(int base, int level) {
		return base * (level * level);
	}

	/**
	 * Get how much repair will cost
	 * 
	 * @param base base cost (level 1)
	 * @param level current level
	 * @param health health of the piece
	 * @param healthMax max piece health
	 * @return cost
	 */
	public static int getRepairCost(int base, int level, double health, double healthMax) {
		return (int) Math.round(getLevelCost(base, level) * (1 - health / healthMax));
	}

	/**
	 * Get piece cost for selling
	 * 
	 * @param base base cost (level 1)
	 * @param level current level
	 * @param health health of the piece
	 * @param healthMax max piece health
	 * @return total cost
	 */
	public static int getPieceCost(int base, int level, double health, double healthMax) {
		return getLevelCost(base, level) - getRepairCost(base, level, health, healthMax);
	}

	/**
	 * Get piece base cost for other calculations
	 * 
	 * @param id piece name
	 * @return cost of level 1.
	 */
	public static int getBaseCost(String id) {
		if (id.equals("")) return -1;
		return pieces.get(id).baseCost;
	}

	/**
	 * Get piece name for tooltip
	 * 
	 * @param id name
	 * @return tooltip text
	 */
	public static String getPieceLabel(String id) {
		return pieces.get(id).label;
	}


	/**
	 * Get piece group key
	 * 
	 * @param id name
	 * @return group key
	 */
	public static String getPieceGroup(String id) {
		return pieces.get(id).group;
	}

	/**
	 * Get group label
	 * 
	 * @param id key
	 * @return group label
	 */
	public static String getGroupLabel(String id) {
		return groups.get(id);
	}

	/**
	 * Get discovery key, discovery needed to buy piece (eg. "body","cannon")
	 * 
	 * @param pieceName
	 * @return discovery key
	 */
	public static String getPieceDiscoveryKey(String pieceName) {
		return pieces.get(pieceName).discovery;
	}

}
