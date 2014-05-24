package net.sector.level;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Map.Entry;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.drivers.DriverStore;
import net.sector.level.highscore.HighscoreTable;
import net.sector.level.sequence.LevelController;
import net.sector.level.ship.DiscoveryRegistry;
import net.sector.level.ship.DiscoveryTable;
import net.sector.level.ship.ShipBundle;
import net.sector.network.levels.NetLevelContainer;
import net.sector.util.Log;
import net.sector.util.Utils;

import org.jdom2.Element;

import com.porcupine.ion.Ion;
import com.porcupine.util.StringUtils;


/**
 * Level bundle
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class LevelBundle implements Comparable<LevelBundle> {

	private ShipBundle ship = null;
	private DiscoveryTable discoveries = DiscoveryRegistry.getDiscoveryTableMaximal();
	private LevelController level = null;
	private DriverStore drivers = new DriverStore(SuperContext.basicDrivers);

	private int money = 0;

	private int timer = -1;

	private EBuildingMode buildMode = EBuildingMode.NORMAL;

	/** Level title */
	public String title = "";
	/** Level subtitle */
	public String subtitle = "";
	/** Minimal game version to play this level */
	public int minVersion = 0;


	/** If this level had default ship when loaded */
	public boolean hadDefaultShip = false;

	/** Level author */
	public String author = "";

	/** [ELevel] LID (if type == NET) */
	public String lid = "";

	/** [ELevel] Highscore filename */
	public String rawFilename = "";

	/** Level type */
	public ELevel type = null;

	private GameCursor compiledCursor = null;
	private GameCursor lastCursor = null;


	/**
	 * Construct LevelBundle from a NetLevelContainer
	 * 
	 * @param nlc net level container
	 */
	public LevelBundle(NetLevelContainer nlc) {
		this(nlc.lid, nlc.levelRootNode, ELevel.NET);
		this.lid = nlc.lid;
	}

	/**
	 * Create from filename, xml root node and level type.
	 * 
	 * @param filename filename
	 * @param rootNode root node
	 * @param type levle type
	 */
	public LevelBundle(String filename, Element rootNode, ELevel type) {

		this.type = type;


		if (filename.contains("/")) {
			String[] parts = filename.split("[/]");
			filename = parts[parts.length - 1];
		}

		filename = filename.split("[.]")[0];

		rawFilename = filename;

		loadFromXml(rootNode);

	}

	private void loadFromXml(Element root) {

		Element info = root.getChild("info");
		if (info == null) {
			Log.w("Missing <info> tag in net level.");
		} else {
			String strTitle = info.getChildText("title");
			if (strTitle != null) this.title = strTitle;

			String strAuthor = info.getChildText("author");
			if (strAuthor != null) this.author = strAuthor;

			String strSubTitle = info.getChildText("subtitle");
			if (strSubTitle != null) this.subtitle = strSubTitle;

			String strVersion;

			strVersion = info.getChildText("minv");
			if (strVersion != null) this.minVersion = AiObjParser.getInteger(strVersion, 0);
		}

		// load ship from <ship>
		Element shipNode = root.getChild("ship");
		if (shipNode != null) {
			hadDefaultShip = true;
			this.ship = new ShipBundle();
			this.ship.xmlFromElement(shipNode);
		} else {
			this.ship = new ShipBundle();
			this.ship.shieldLevel = DiscoveryRegistry.getDiscoveryLevelMin("shield");
			this.ship.energyLevel = DiscoveryRegistry.getDiscoveryLevelMin("energy");
		}

		// load drivers from <drivers>
		Element driversNode = root.getChild("drivers");
		if (driversNode != null) {
			this.drivers.loadDriversFromNodeList(driversNode);
		}

		// parse <config> node
		Element configNode = root.getChild("config");
		if (configNode != null) {
			this.money = AiObjParser.getInteger(configNode.getChildText("money"), 0);
			this.timer = AiObjParser.getInteger(configNode.getChildText("timer"), -1);
			if (timer == -1) this.timer = AiObjParser.getInteger(configNode.getChildText("limit"), -1);

			String bmode = configNode.getChildText("building");
			if (bmode != null) {
				bmode = bmode.toUpperCase();
				try {
					this.buildMode = EBuildingMode.valueOf(bmode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			Log.w("Missing required <config> node in level " + title);
		}

		// parse <discoveries>
		Element discoveriesNode = root.getChild("discoveries");
		if (discoveriesNode != null) {
			for (Element node : discoveriesNode.getChildren()) {
				if (node.getName().equals("all")) {

					String l = node.getAttributeValue("level");

					if (StringUtils.isInArray(l, false, "min", "empty", "none", "zero", "0")) {

						discoveries = DiscoveryRegistry.getDiscoveryTableMinimal();

					} else if (StringUtils.isInArray(l, false, "max", "full")) {

						discoveries = DiscoveryRegistry.getDiscoveryTableMaximal();

					} else if (l.matches("[0-9]+")) {
						discoveries = DiscoveryRegistry.getDiscoveryTableMinimal();
						for (Entry<String, Integer> e : discoveries.entrySet()) {
							discoveries.setDiscoveryLevel(e.getKey(), Integer.valueOf(l));
						}
					}

				} else {

					String name = node.getAttributeValue("name");
					String l = node.getAttributeValue("level");

					if (StringUtils.isInArray(l, false, "min", "empty", "none", "zero", "0")) {

						discoveries.setDiscoveryLevel(name, DiscoveryRegistry.getDiscoveryLevelMin(name));

					} else if (StringUtils.isInArray(l, false, "max", "full")) {

						discoveries.setDiscoveryLevel(name, DiscoveryRegistry.getDiscoveryLevelMax(name));

					} else if (l.matches("[0-9]+")) {
						discoveries.setDiscoveryLevel(name, AiObjParser.getInteger(name, 0));

					} else {
						Log.w("Illegal discovery level: " + l);
					}
				}
			}
		}

		Element sequenceElement = root.getChild("sequence");
		if (sequenceElement != null) {
			this.level = new LevelController(sequenceElement);
		} else {
			Log.e("[!!!] Missing required <sequence> tag in level " + title);
		}

		// set timer in level controller
		if (this.timer > 0) this.level.setInitialTimer(this.timer);

		// assign driver store to the level controller
		this.level.setDriverStore(drivers);

//		System.out.println("Loaded level: "+title);
//		System.out.println("DT:\n"+discoveries+"\n");

//		if (buildMode != EBuildingMode.LOCKED && !hadDefaultShip) {
//			File file = getLastShipFile();
//			if(file.exists()) {
//				try {
//					ship.xmlFromStream(new FileInputStream(file));
//				} catch (Exception e) {
//					Log.exc("Error loading last ship.", e);
//				}
//			}
//		}

		// reduce ship for discoveries and discovery table.
		if (buildMode == EBuildingMode.NORMAL) {
			ship.reduceForDiscoveryTable(discoveries);
			ship.reduceForTotalCost(money);
			money -= ship.getTotalCost();
		}

		compileToCursor();
	}

	/**
	 * Compile to a cursor.
	 */
	public void compileToCursor() {
		Log.f2("Compiling level " + title + " to game cursor.");
		this.compiledCursor = new GameCursor();
		this.compiledCursor.discoveryTable = this.discoveries;
		this.compiledCursor.money = this.money;
		this.compiledCursor.buildMode = buildMode;
		this.compiledCursor.shipBundle = ship.copy();
	}

	/**
	 * Get game cursor for game context
	 * 
	 * @return the new copy of cursor
	 */
	public GameContext toGameContext() {
		GameContext gc = new GameContext();

		if (lastCursor == null) {
			gc.setCursor(compiledCursor.copy());
		} else {
			gc.setCursor(lastCursor.copy());
		}

		gc.setLevelController(this.level);
		gc.getLevelController().reset();
		gc.setLevelType(type);
		gc.setLevelBundle(this);
		gc.saveCursor();
		return gc;
	}

	/**
	 * Get if this level can be played on current game version.
	 * 
	 * @return if level is compatible.
	 */
	public boolean isCompatible() {
		return Constants.VERSION_NUMBER >= minVersion;
	}


	@Override
	public int compareTo(LevelBundle o) {
		return title.compareToIgnoreCase(o.title);
	}

	/**
	 * Save cursor (when leaving designer)
	 * 
	 * @param saved cursor to copy and save
	 */
	public void saveCursorToBundle(GameCursor saved) {
		this.lastCursor = saved.copy();
	}

	/**
	 * Save ship to file (shown as option in load ship dialog)
	 * 
	 * @param ship
	 */
	public void saveShipToFile(ShipBundle ship) {
		File file = getLastShipFile();

		try {
			ship.xmlToStream(new FileOutputStream(file));
		} catch (Exception e) {
			Log.e("Error saving ship file.", e);
		}
	}

	/**
	 * Get local highscore table.<br>
	 * For NET levels, use LeaderboardClient to obtain entries in Global
	 * Leaderboard.
	 * 
	 * @return highscore table.
	 */
	public HighscoreTable getHighscoreTable() {

		File file = getHighscoreFile();
		HighscoreTable table = null;

		table = (HighscoreTable) Ion.fromFile(file);
		if (table == null) table = new HighscoreTable();

		table.sort();
		return table;
	}

	/**
	 * Get highscore file if applicable.
	 * 
	 * @return hs file
	 */
	public File getLastShipFile() {

		String highscorePath = "";
		if (type == ELevel.INTERNAL) {
			highscorePath = Constants.DIR_LASTSHIP_INTERNAL;
		} else if (type == ELevel.LOCAL) {
			highscorePath = Constants.DIR_LASTSHIP_LOCAL;
		} else {
			highscorePath = Constants.DIR_LASTSHIP_SHARED;
		}

		File path = Utils.getGameSubfolder(highscorePath);
		try {
			path.mkdirs();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return new File(path, rawFilename + "." + Constants.SUFFIX_SHIP);

	}

	/**
	 * Get highscore file if applicable.
	 * 
	 * @return hs file
	 */
	public File getHighscoreFile() {

		String highscorePath = "";
		if (type == ELevel.INTERNAL) {
			highscorePath = Constants.DIR_HIGHSCORE_INTERNAL;
		} else if (type == ELevel.LOCAL) {
			highscorePath = Constants.DIR_HIGHSCORE_LOCAL;
		} else {
			highscorePath = Constants.DIR_HIGHSCORE_SHARED;
		}

		File path = Utils.getGameSubfolder(highscorePath);
		try {
			path.mkdirs();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return new File(path, rawFilename + ".ion");
	}
}
