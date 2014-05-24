package net.sector.gui.widgets.composite;


import java.util.ArrayList;
import java.util.Iterator;

import net.sector.gui.panels.designer.PanelDesigner;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.input.Keys;
import net.sector.level.EBuildingMode;
import net.sector.level.ship.DiscoveryTable;
import net.sector.level.ship.PieceBundle;
import net.sector.level.ship.PieceRegistry;
import net.sector.level.ship.modules.pieces.Piece;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;
import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Deg;


/**
 * ship designer table
 * 
 * @author MightyPork
 */
public class ShipDesignerTable extends ShipDesignerBase {

	/**
	 * Designer edit modes
	 * 
	 * @author MightyPork
	 */
	public enum EdMode {

		/**
		 * Default mode.<br>
		 * Scroll to rotate dragged piece, or piece under mouse.<br>
		 * Press LMB to grab a piece, release LMB to drop it (swaps pieces if
		 * dropped in non-empty slot).<br>
		 * Click while holding DELETE to sell piece.<br>
		 * Ctrl + LMB or RMB = level up/down of dragged piece or piece under
		 * mouse.<br>
		 * Level changes require some money (up - costs some, down - gives some)
		 */
		ARRANGE,

		/**
		 * Click = place piece dragged, buy it. Dragged piece is not consumed.
		 * Scroll to rotate dragged piece. Ctrl + LMB or RMB to level up/down
		 */
		BUILD,

		/**
		 * Click = place piece dragged, buy it. Dragged piece is not consumed.
		 * Scroll to rotate dragged piece. Ctrl + LMB or RMB to level up/down
		 */
		BUILD_ONE,

		DELETE,

		REPAIR,

		SELECT;
	}


	public static enum EShipStructureError {
		EMPTY_CENTER, INVALID_CENTER, NO_PROPULSION, NO_WEAPONS, INVALID, UNSTABLE, OK;

		/**
		 * Get long verbose description for error dialog.
		 * 
		 * @return text
		 */
		public String getDescription() {
			switch (this) {

				case INVALID_CENTER:
					return "Invalid central piece.\nMust be a BODY or WING.";

				case EMPTY_CENTER:
					return "Central slot must not be empty.\nThe core systems reside there.";

				case NO_PROPULSION:
					return "Your ship needs some propulsion.\nEngines let your ship maneuver.";

				case NO_WEAPONS:
					return "You need some weapons.\nHow would you shoot?";

				case INVALID:
					return "Some pieces are placed incorrectly.";

				case UNSTABLE:
					return "Some pieces are not connected to center.";

				case OK:
					return "Ship is OK.";


			}
			return "Unknown state.";
		}

		/**
		 * Get human readable error text.
		 * 
		 * @return text
		 */
		public String getString() {
			switch (this) {

				case INVALID_CENTER:
					return "Central piece is invalid!";

				case EMPTY_CENTER:
					return "Central slot is empty!";

				case NO_PROPULSION:
					return "Missing propulsion!";

				case NO_WEAPONS:
					return "Missing weapons!";

				case INVALID:
					return "Invalid piece placement.";

				case UNSTABLE:
					return "Ship design is not stable.";

				case OK:
					return "Ship design is valid.";
			}
			return "Unknown state.";
		}

		/**
		 * Get if is valid = can start game with this design
		 * 
		 * @return is valid
		 */
		public boolean isValid() {
			return this == OK;
		}
	}

	/**
	 * Function modifying given piece
	 * 
	 * @author MightyPork
	 */
	public static interface PieceModifyFn {
		/**
		 * Do what needs to be done before other stuff.
		 */
		public void prepare();

		/**
		 * modify piece
		 * 
		 * @param piece piece to modify / process / edit / etc..
		 * @param args arguments for the function
		 * @return replacement (return the piece if not discarded)
		 */
		public DraggablePiece process(DraggablePiece piece, Object... args);
	}

	private boolean cancelBuildingOnRightRelease;
	/** The edit mode. */
	protected EdMode editMode = EdMode.ARRANGE;

	/** Error found during the last invocation of checkDesign() */
	public EShipStructureError lastErrorFound = EShipStructureError.OK;

	private PanelDesigner designerGui;

	/**
	 * Assign designer panel
	 * 
	 * @param designer designer
	 */
	public void setDesignerGui(PanelDesigner designer) {
		this.designerGui = designer;
	}

	/** flag that during batch level change sound was requested to play. */
	private boolean fnPlayedSnd = false;

	/** Change level function, args: direction, ignoreMoney */
	private PieceModifyFn fnLvl = new PieceModifyFn() {
		@Override
		public void prepare() {
			fnPlayedSnd = false;
		}

		@Override
		public DraggablePiece process(DraggablePiece p, Object... args) {

			int levelChangeDir = (Integer) args[0];
			boolean ignoreMoney = (Boolean) args[1];

			if (p == null) return null;
			if (p.health < p.healthMax) return p;

			boolean doit = true;
			int money = 0;

			if (ignoreMoney) {
				doit = true;
			} else {
				money = p.getLevelChangeCost(levelChangeDir);
				doit = control.hasMoney(money);
			}

			if (doit) {
				if (p.level > 1 && levelChangeDir == -1) {
					fnPlayedSnd = true;
					p.level--;
					control.consumeMoney(money);
				} else if (p.level < discoveries.getDiscoveryLevelForPiece(p.id) && levelChangeDir == 1) {
					fnPlayedSnd = true;
					p.level++;
					control.consumeMoney(money);
				}
			}

			p.fixFieldValuesSetMaxHealth();

			return p;
		}
	};


	private int keyDel = Keyboard.KEY_DELETE;

	private int keyLevel = Keyboard.KEY_LCONTROL;

	private int keyRepair = Keyboard.KEY_R;

	private int keyRotRough = Keyboard.KEY_LSHIFT;

	private int keySelect = Keyboard.KEY_LCONTROL;

	/** Rotate piece, args: dir */
	private PieceModifyFn pieceRotFn = new PieceModifyFn() {
		@Override
		public void prepare() {}

		@Override
		public DraggablePiece process(DraggablePiece p, Object... args) {

			int rotDir = (Integer) args[0];

			int rough = 45;
			int smooth = p.rotStep;

			if (p.rotStep > rough) rough = p.rotStep;

			if (Keyboard.isKeyDown(keyRotRough)) {
				p.rotate = Deg.roundX(p.rotate, rough);
				p.rotate += rotDir * rough;
			} else {
				p.rotate += rotDir * smooth;
			}

			p.rotate = (int) Deg.norm(p.rotate);
			return p;
		}
	};

	/** List of selected slots */
	public ArrayList<CoordI> selectedSlots = new ArrayList<CoordI>();
	/** List of slots with problem */
	public ArrayList<CoordI> errorSlots = new ArrayList<CoordI>();

	private boolean upAtEmptyWillStopSelecting;
	private DiscoveryTable discoveries;

	/**
	 * Ship design table
	 * 
	 * @param pieces piece table
	 * @param discoveries
	 */
	public ShipDesignerTable(PieceBundle pieces[][], DiscoveryTable discoveries) {
		super(9, 9);

		int piecesW = pieces[0].length;
		int piecesH = pieces.length;

		int padX = (W - piecesW) / 2;
		int padY = (H - piecesH) / 2;

		for (int x = 0; x < piecesW; x++) {
			for (int y = 0; y < piecesH; y++) {
				if (pieces[y][x] == null) continue;
				slots[y + padY][x + padX] = new DraggablePiece(pieces[y][x]);
			}
		}

		this.discoveries = discoveries;

		setTheme(ETheme.DES_TABLE);
	}

	/**
	 * Do some operation with all selected pieces (eg. delete, change level,
	 * change control trigger)
	 * 
	 * @param fn operation
	 * @param args arguments
	 */
	public void doForSelected(PieceModifyFn fn, Object... args) {
		fn.prepare();
		for (CoordI coord : selectedSlots) {
			if (slots[coord.y][coord.x] != null) {
				slots[coord.y][coord.x] = fn.process(slots[coord.y][coord.x], args);
			}
		}
	}

	/**
	 * Get ship design, crop symmetrically empty rows and columns
	 * 
	 * @return table of pieces
	 */
	public PieceBundle[][] exportShipDesign() {

		int minX = W, minY = H, maxX = 0, maxY = 0;

		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				if (slots[y][x] != null) {
					minX = Math.min(minX, x);
					minY = Math.min(minY, y);
					maxX = Math.max(maxX, x);
					maxY = Math.max(maxY, y);
				}
			}
		}

		int cropL = minX, cropR = W - 1 - maxX;
		int cropU = minY, cropD = H - 1 - maxY;

		int cropX = Math.min(cropL, cropR);
		int cropY = Math.min(cropU, cropD);

		cropX = Math.min(cropX, W / 2 - 1);
		cropY = Math.min(cropY, H / 2 - 1);

		PieceBundle[][] pieces = new PieceBundle[H - cropY * 2][W - cropX * 2];
		for (int y = cropY; y < H - cropY; y++) {
			for (int x = cropX; x < W - cropX; x++) {
				pieces[y - cropY][x - cropX] = slots[y][x];
			}
		}

		return pieces;

	}

	/**
	 * Get current edit mode
	 * 
	 * @return edit mode
	 */
	public EdMode getEditMode() {
		return editMode;
	}


	@Override
	public void handleStaticInputs(Coord pos) {
		boolean left = Mouse.isButtonDown(0);
		boolean right = Mouse.isButtonDown(1);

		boolean dragging = isDragging();
		boolean building = isBuilding();
		boolean buildingSingle = isBuildingSingle();
		//boolean arranging = isArranging();
		boolean deleting = isDeleting();
		boolean repairing = isRepairing();
		boolean selecting = isSelecting();
		boolean isEditable = control.canEdit();

		// selection mode
		if (selecting && !dragging && (left || right)) {

			// repair placed piece
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) {
				return;
			}
			DraggablePiece p = slots[tile.y][tile.x];

			if (p != null) {
				if (left && !selectedSlots.contains(tile)) selectedSlots.add(tile);
				if (right && selectedSlots.contains(tile)) selectedSlots.remove(tile);
			}

			return;
		}

		// building & left down = build piece (paint)
		if (isEditable && building && !buildingSingle && dragging && left) {

			DraggablePiece p = control.getDragged();
			int cost = PieceRegistry.getPieceCost(p.baseCost, p.level, p.health, p.healthMax);

			if (control.hasMoney(cost)) {

				CoordI tile = getCoordUnderMouse(pos);
				if (tile == null || slots[tile.y][tile.x] != null) {
					return;
				}
				slots[tile.y][tile.x] = control.getDragged().copy();

				sndBuild();

				control.consumeMoney(cost);

			}


			checkDesign();

			return;
		}

		// DELETE + left = sell
		if (isEditable && deleting && left && !building && !dragging) {

			// change level of piece under / dragged
			DraggablePiece p = null;

			// sell placed piece (give money)
			// surely isn't building now.
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) return;
			p = slots[tile.y][tile.x];
			if (p == null) return;

			selectedSlots.remove(tile);

			sellPiece(p);

			slots[tile.y][tile.x] = null;

			checkDesign();
			return;
		}

		// R + LMB = repair
		if (!selecting && (repairing || right) && left && !building && !dragging) {
			// repair placed piece
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) return;
			DraggablePiece p = slots[tile.y][tile.x];
			repairPiece(p);

			return;
		}

		return;
	}

	/**
	 * Check design for errors
	 * 
	 * @return status
	 */
	public EShipStructureError checkDesign() {
		return (lastErrorFound = checkDesign_do());
	}

	private EShipStructureError checkDesign_do() {

		errorSlots.clear();

		DraggablePiece center = slots[H / 2][W / 2];
		if (center == null) {
			errorSlots.add(new CoordI(W / 2, H / 2));
			return EShipStructureError.EMPTY_CENTER;
		}
		if (!center.typeBody) {
			errorSlots.add(new CoordI(W / 2, H / 2));
			return EShipStructureError.INVALID_CENTER;
		}

		int jets = 0;
		int weapons = 0;
		boolean invalid = false;
		for (int y = 0; y < H; y++) {
			inner:
			for (int x = 0; x < W; x++) {
				DraggablePiece p = slots[y][x];
				if (p != null) {
					if (p.typeEngine) {
						jets++;

						// check if there is no other tile behing this engine.
						CoordI move = new CoordI(0, 1);
						CoordI tile = new CoordI(x, y);
						tile.add_ip(move);
						while (tile.x >= 0 && tile.y >= 0 && tile.x < W && tile.y < H) {
							if (slots[tile.y][tile.x] != null) {
								errorSlots.add(new CoordI(x, y));
								invalid = true;
								break;
							}
							tile.add_ip(move);
						}

					}
					if (p.typeWeapon) {
						weapons++;

						// check if can shoot
						int dir = Deg.round45(p.rotate);
						CoordI move;
						switch (dir) {
							case 360:
							case 0:
								move = new CoordI(0, -1);
								break;

							case 45:
								move = new CoordI(-1, -1);
								break;

							case 90:
								move = new CoordI(-1, 0);
								break;

							case 135:
								move = new CoordI(-1, 1);
								break;

							case 180:
								move = new CoordI(0, 1);
								break;

							case 225:
								move = new CoordI(1, 1);
								break;

							case 270:
								move = new CoordI(1, 0);
								break;

							case 315:
								move = new CoordI(1, -1);
								break;

							default:
								continue inner;
						}

						// we have move
						// now raytrace...

						CoordI tile = new CoordI(x, y);
						tile.add_ip(move);
						while (tile.x >= 0 && tile.y >= 0 && tile.x < W && tile.y < H) {
							if (slots[tile.y][tile.x] != null) {
								errorSlots.add(new CoordI(x, y));
								invalid = true;
								break;
							}
							tile.add_ip(move);
						}
					}
				}
			}
		}

		if (jets == 0) return EShipStructureError.NO_PROPULSION;
		if (weapons == 0) return EShipStructureError.NO_WEAPONS;

		// check integrity

		final boolean[][] inconsistent = new boolean[H][W];
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				inconsistent[y][x] = (slots[y][x] != null);
			}
		}

		class Checker {
			Piece getPiece(int x, int y) {
				try {
					if (slots[y][x] == null) return null;
					return slots[y][x].toPiece();
				} catch (ArrayIndexOutOfBoundsException e) {
					return null;
				}
			}

			void markNeighbor(int x, int y) {
				Piece p;
				p = getPiece(x, y);
				if (p == null) {
					return;
				}
				inconsistent[y][x] = false;

				for (int xx = -1; xx <= 1; xx++) {
					for (int yy = -1; yy <= 1; yy++) {
						if (xx == 0 && yy == 0) continue;
						//if (!p.canConnectToSide(xx, yy)) continue;

						Piece p2 = getPiece(x + xx, y + yy);

						int dx = -xx;
						int dy = -yy;

						if (p2 != null && inconsistent[y + yy][x + xx] && p2.canConnectToSide(dx, -dy)) {
							inconsistent[y + yy][x + xx] = false;
							markNeighbor(x + xx, y + yy);
						}
					}
				}
			}

		}

		new Checker().markNeighbor(W / 2, H / 2);

		boolean unst = false;
		for (int y = 0; y < H; y++) {
			for (int x = 0; x < W; x++) {
				if (inconsistent[y][x]) {
					unst = true;
					errorSlots.add(new CoordI(x, y));
				}
			}
		}

		// this is this far to make sure all checks are done and bad pieces are highlighted
		if (invalid) return EShipStructureError.INVALID;
		if (unst) return EShipStructureError.UNSTABLE;

		return EShipStructureError.OK;
	}

	/**
	 * Check if any pieces are selected
	 * 
	 * @return selected pieces
	 */
	public boolean isAnySelected() {
		return selectedSlots.size() > 0;
	}


	/**
	 * Get if is building.
	 * 
	 * @return is building.
	 */
	protected boolean isArranging() {
		return editMode == EdMode.ARRANGE;
	}

	/**
	 * Get if is building.
	 * 
	 * @return is building.
	 */
	protected boolean isBuilding() {
		return editMode == EdMode.BUILD || editMode == EdMode.BUILD_ONE;
	}

	/**
	 * Get if is building one only (discard after first placed)
	 * 
	 * @return state
	 */
	protected boolean isBuildingSingle() {
		return editMode == EdMode.BUILD_ONE;
	}

	@Override
	protected boolean isCentralSlotHighlited() {
		return true;
	}

	/**
	 * Get if delete mode is active.
	 * 
	 * @return is deleting
	 */
	protected boolean isDeleting() {
		return editMode == EdMode.DELETE;
	}

	/**
	 * Get if is building.
	 * 
	 * @return is building.
	 */
	protected boolean isDragging() {
		return control.isDragging();
	}

	/**
	 * Get if repair mode is active.
	 * 
	 * @return is repairing
	 */
	protected boolean isRepairing() {
		return editMode == EdMode.REPAIR;
	}

	/**
	 * Get if select mode is active.
	 * 
	 * @return is selecting
	 */
	protected boolean isSelecting() {
		return editMode == EdMode.SELECT;
	}

	@Override
	protected boolean isSlotSelected(int x, int y) {
		return selectedSlots.contains(new CoordI(x, y));
	}

	@Override
	protected boolean isSlotBad(int x, int y) {
		return errorSlots.contains(new CoordI(x, y));
	}

	/**
	 * Change level of dragged pieces
	 * 
	 * @param dir
	 */
	public void levelChangeDragged(int dir) {
		// change level of piece under / dragged
		DraggablePiece p = null;

		// piece dragged (if building, dont consume money)
		p = control.getDragged();
		if (control.isDragging() && p != null && p.health == p.healthMax) {

			fnLvl.prepare();
			fnLvl.process(p, dir, isBuilding());
			if (fnPlayedSnd) {
				if (dir < 0) sndLevelDown();
				if (dir > 0) sndLevelUp();
			}
			return;
		}
	}

	/**
	 * Change level of selected pieces
	 * 
	 * @param dir direction
	 */
	public void levelChangeSelected(int dir) {
		doForSelected(fnLvl, dir, false);
		if (fnPlayedSnd) {
			if (dir < 0) sndLevelDown();
			if (dir > 0) sndLevelUp();
		}
	}


	/**
	 * move selected pieces if possible
	 * 
	 * @param dir
	 */
	public void moveSelected(CoordI dir) {
		// up

		if (dir.x == 0 && dir.y == -1) {
			// check
			Iterator<CoordI> iter = selectedSlots.iterator();
			while (iter.hasNext()) {
				CoordI tile = iter.next();
				if (slots[tile.y][tile.x] == null) {
					iter.remove();
					continue;
				}
				if (tile.y == 0) return; // no place for this tile, cancel all
				// no space to move, do nothing.
				if (slots[tile.y - 1][tile.x] != null && !selectedSlots.contains(new CoordI(tile.x, tile.y - 1))) return;
			}

			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					CoordI tile = new CoordI(x, y);
					if (selectedSlots.contains(tile)) {
						DraggablePiece p = slots[tile.y][tile.x];
						slots[tile.y][tile.x] = null;

						slots[tile.y - 1][tile.x] = p;
					}
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.y--;
			}
			return;
		}

		if (dir.x == 0 && dir.y == 1) {
			// check
			Iterator<CoordI> iter = selectedSlots.iterator();
			while (iter.hasNext()) {
				CoordI tile = iter.next();
				if (slots[tile.y][tile.x] == null) {
					iter.remove();
					continue;
				}
				if (tile.y == H - 1) return; // no place for this tile, cancel all
				// no space to move, do nothing.
				if (slots[tile.y + 1][tile.x] != null && !selectedSlots.contains(new CoordI(tile.x, tile.y + 1))) return;
			}

			for (int y = H - 1; y >= 0; y--) {
				for (int x = 0; x < W; x++) {
					CoordI tile = new CoordI(x, y);
					if (selectedSlots.contains(tile)) {
						DraggablePiece p = slots[tile.y][tile.x];
						slots[tile.y][tile.x] = null;

						slots[tile.y + 1][tile.x] = p;
					}
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.y++;
			}
			return;
		}
		if (dir.x == -1 && dir.y == 0) {
			// check
			Iterator<CoordI> iter = selectedSlots.iterator();
			while (iter.hasNext()) {
				CoordI tile = iter.next();
				if (slots[tile.y][tile.x] == null) {
					iter.remove();
					continue;
				}
				if (tile.x == 0) return; // no place for this tile, cancel all
				// no space to move, do nothing.
				if (slots[tile.y][tile.x - 1] != null && !selectedSlots.contains(new CoordI(tile.x - 1, tile.y))) return;
			}

			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					CoordI tile = new CoordI(x, y);
					if (selectedSlots.contains(tile)) {
						DraggablePiece p = slots[tile.y][tile.x];
						slots[tile.y][tile.x] = null;

						slots[tile.y][tile.x - 1] = p;
					}
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.x--;
			}
			return;
		}

		if (dir.x == 1 && dir.y == 0) {
			// check
			Iterator<CoordI> iter = selectedSlots.iterator();
			while (iter.hasNext()) {
				CoordI tile = iter.next();
				if (slots[tile.y][tile.x] == null) {
					iter.remove();
					continue;
				}
				if (tile.x == W - 1) return; // no place for this tile, cancel all
				// no space to move, do nothing.
				if (slots[tile.y][tile.x + 1] != null && !selectedSlots.contains(new CoordI(tile.x + 1, tile.y))) return;
			}

			for (int y = 0; y < H; y++) {
				for (int x = W - 1; x >= 0; x--) {
					CoordI tile = new CoordI(x, y);
					if (selectedSlots.contains(tile)) {
						DraggablePiece p = slots[tile.y][tile.x];
						slots[tile.y][tile.x] = null;
						slots[tile.y][tile.x + 1] = p;
					}
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.x++;
			}
			return;
		}


	}

	/**
	 * Move all pieces if possible
	 * 
	 * @param dir
	 */
	public void moveAll(CoordI dir) {
		// up

		if (dir.x == 0 && dir.y == -1) {

			for (int x = 0; x < W; x++) {
				if (slots[0][x] != null) return; //cant
			}

			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					CoordI tile = new CoordI(x, y);

					DraggablePiece p = slots[tile.y][tile.x];
					if (p == null) continue;
					slots[tile.y][tile.x] = null;

					slots[tile.y - 1][tile.x] = p;
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.y--;
			}

			checkDesign();
			return;
		}

		if (dir.x == 0 && dir.y == 1) {

			for (int x = 0; x < W; x++) {
				if (slots[H - 1][x] != null) return; //cant
			}

			for (int y = H - 1; y >= 0; y--) {
				for (int x = 0; x < W; x++) {
					CoordI tile = new CoordI(x, y);

					DraggablePiece p = slots[tile.y][tile.x];
					if (p == null) continue;
					slots[tile.y][tile.x] = null;

					slots[tile.y + 1][tile.x] = p;
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.y++;
			}

			checkDesign();
			return;
		}

		if (dir.x == -1 && dir.y == 0) {
			// check
			for (int y = 0; y < H; y++) {
				if (slots[y][0] != null) return; //cant
			}

			for (int y = 0; y < H; y++) {
				for (int x = 0; x < W; x++) {
					CoordI tile = new CoordI(x, y);

					DraggablePiece p = slots[tile.y][tile.x];
					if (p == null) continue;
					slots[tile.y][tile.x] = null;

					slots[tile.y][tile.x - 1] = p;

				}
			}

			for (CoordI tile : selectedSlots) {
				tile.x--;
			}

			checkDesign();
			return;
		}

		if (dir.x == 1 && dir.y == 0) {
			// check
			// check
			for (int y = 0; y < H; y++) {
				if (slots[y][W - 1] != null) return; //cant
			}

			for (int y = 0; y < H; y++) {
				for (int x = W - 1; x >= 0; x--) {
					CoordI tile = new CoordI(x, y);

					DraggablePiece p = slots[tile.y][tile.x];
					if (p == null) continue;
					slots[tile.y][tile.x] = null;

					slots[tile.y][tile.x + 1] = p;
				}
			}

			for (CoordI tile : selectedSlots) {
				tile.x++;
			}

			checkDesign();
			return;
		}


	}


	@Override
	public Widget onKey(int key, char chr, boolean down) {

		unselectNullSlots();

		if (key == Keyboard.KEY_SPACE && down) {
			unselectAll();
			return this;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_A)) {
			selectAll();
			return this;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_W)) {
			selectWeapons();
			return this;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_B)) {
			selectBody();
			return this;
		}

		boolean dragging = isDragging();
		boolean building = isBuilding();
//		boolean buildingSingle = isBuildingSingle();
//		boolean arranging = isArranging();
//		boolean deleting = isDeleting();
//		boolean repairing = isRepairing();
//		boolean selecting = isSelecting();

		// key press while dragging deletes
		if (key == keyDel) {

			if (control.buildMode == EBuildingMode.LOCKED) return this;

			if (dragging) {
				if (!building) {
					sellPiece(control.getDragged());
				} else {
					sndDrop();
				}
				control.resetDragInfo();

			} else if (isAnySelected()) {

				designerGui.deleteSelectedAsk();

			} else {
				if (down) {
					setEditMode(EdMode.DELETE);
				} else {
					setEditMode(EdMode.ARRANGE);
				}
			}
			return this;
		}

		// key press while dragging deletes
		if (key == keyRepair) {
			if (dragging && !building) {
				repairPiece(control.getDragged());
			} else {
				if (down) {
					setEditMode(EdMode.REPAIR);
				} else {
					setEditMode(EdMode.ARRANGE);
				}
			}
			return this;
		}

		if (key == keySelect && !dragging && !building) {
			if (down) {
				setEditMode(EdMode.SELECT);
			} else {
				setEditMode(EdMode.ARRANGE);
			}
			return this;
		}

		// CTRL + SPACE = unselect
		if (Keyboard.isKeyDown(keySelect) && Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			unselectAll();
			return this;
		}

		return null;
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		Widget w = onMouseButton_do(pos, button, down);
		checkDesign();
		return w;
	}

	private Widget onMouseButton_do(Coord pos, int button, boolean down) {

		unselectNullSlots();

		boolean left = button == 0;
		boolean right = button == 1;

		boolean up = !down;

		boolean dragging = isDragging();
		boolean building = isBuilding();
		boolean buildingSingle = isBuildingSingle();
		boolean arranging = isArranging();
		boolean deleting = isDeleting();
		boolean repairing = isRepairing();
		boolean selecting = isSelecting();
		boolean isEditable = control.canEdit();

		// edit controls
		if (down && left && !dragging && !building && !deleting && !repairing && !isAnySelected()) {
			if (Keys.isDown(Keyboard.KEY_E) || Keys.isDown(Keyboard.KEY_RETURN)) {
				CoordI tile = getCoordUnderMouse(pos);
				if (tile != null && slots[tile.y][tile.x] != null && slots[tile.y][tile.x].hasTrigger()) {
					selectedSlots.add(tile);
					designerGui.editControlsSelected();
					return this;
				}
			}
		}


		if (isEditable && building && right && up && cancelBuildingOnRightRelease) {
			// cancel building now
			control.resetDragInfo();
			editMode = EdMode.ARRANGE;
			sndDrop();
			cancelBuildingOnRightRelease = false;
			return this;
		}

		if (building && right && down) {
			// cancel building when button is released
			cancelBuildingOnRightRelease = true;
			return this;
		}

		if (!isMouseOver(pos)) {
			// event outside

			if (down && !dragging) {
				// not my business
				return null;
			}

			// cancel piece drag (!shop must be before in the gui hierarchy to process this event properly!)
			if (dragging && up && !isBuilding()) {
				sndDrop();
				control.cancelDrag();
				return this; // consume.
			}

			return null;
		}

		// delete + click = destroy
		if (isEditable && deleting && down) {

			// change level of piece under / dragged
			DraggablePiece p = null;

			// piece dragged (if building, dont consume money)
			if (dragging) {
				p = control.getDragged();
				if (!building) {
					sellPiece(p);
				} else {
					sndDrop();
				}
				control.resetDragInfo();
				return this;
			}

			return this;
		}

		// R + click = repair
		// right click & nothing other => repair
		if ((repairing && down) || (!dragging && right && down && !selecting)) {

			// piece dragged (if building, dont consume money)
			if (dragging) {
				if (!building) {
					repairPiece(control.getDragged());
				}
				return this;
			}

			// repair placed piece
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) return null;
			DraggablePiece p = slots[tile.y][tile.x];
			repairPiece(p);

			return this;
		}

		// cancel selecting by r-click at nothing
		if (!dragging && selecting && down) {

			// repair placed piece
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) return null;
			DraggablePiece p = slots[tile.y][tile.x];

			if (p == null) {
				upAtEmptyWillStopSelecting = true;
			} else {
				upAtEmptyWillStopSelecting = false;
			}

			return this;
		}

		// cancel selecting by r-click at nothing
		if (!dragging && selecting && up) {

			// repair placed piece
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) return null;
			DraggablePiece p = slots[tile.y][tile.x];

			if (p == null && upAtEmptyWillStopSelecting) {
				setEditMode(EdMode.ARRANGE);
			}

			upAtEmptyWillStopSelecting = false;

			return this;
		}

		// left up = build single
		if (isEditable && buildingSingle && dragging && up && left) {

			DraggablePiece p = control.getDragged();
			int cost = PieceRegistry.getPieceCost(p.baseCost, p.level, p.health, p.healthMax);

			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) {
				control.resetDragInfo();
				return this;
			}

			if (control.hasMoney(cost)) {
				if (slots[tile.y][tile.x] == null) {
					sndDrop();
					slots[tile.y][tile.x] = control.getDragged();
					control.consumeMoney(cost);
				}
				control.resetDragInfo();
				editMode = EdMode.ARRANGE;

			}
			return this;
		}

		// ## DRAG-DROP

		// left down = start drag (if not building)
		if (isEditable && arranging && !dragging && down && left) {

			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null || slots[tile.y][tile.x] == null) {
				control.resetDragInfo();
				return this;
			}

			sndGrab();
			selectedSlots.remove(tile);

			control.startDrag(this, tile, slots[tile.y][tile.x], true);
			slots[tile.y][tile.x] = null;
			return this;
		}

		// left up = stop drag (if not building)
		if (isEditable && arranging && dragging && up && left) {

			sndDrop();

			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) {
				control.cancelDrag();
				return this;
			}

			if (slots[tile.y][tile.x] == null) {
				slots[tile.y][tile.x] = control.getDragged();
				control.resetDragInfo();
			} else {
				// swap.
				if (slots[control.pickFrom.y][control.pickFrom.x] == null) {
					slots[control.pickFrom.y][control.pickFrom.x] = slots[tile.y][tile.x];
					slots[tile.y][tile.x] = control.getDragged();
					control.resetDragInfo();
				}
			}
			return this;
		}

		return null;

	}

	@Override
	public Widget onScroll(final Coord pos, int scroll) {

		if (control.buildMode == EBuildingMode.LOCKED) return this;

		int scrollUnit = Calc.clampi(scroll, -1, 1);

		// level change action - scroll with modifier.
		if (Keyboard.isKeyDown(keyLevel) || Mouse.isButtonDown(1)) {
			cancelBuildingOnRightRelease = false;
			final int dir = scrollUnit;

			// change level of piece under / dragged
			DraggablePiece p = null;

			// piece dragged (if building, dont consume money)
			if (control.isDragging()) {
				levelChangeDragged(dir);
				return this;
			}

			if (!isAnySelected()) {
				// change level of placed piece (consume money)
				CoordI tile = getCoordUnderMouse(pos);
				if (tile == null) return null;
				p = slots[tile.y][tile.x];
				if (p == null || p.health < p.healthMax) return this;

				fnLvl.prepare();
				fnLvl.process(p, dir, false);
				if (fnPlayedSnd) {
					if (dir < 0) sndLevelDown();
					if (dir > 0) sndLevelUp();
				}
				return this;
			} else {
				levelChangeSelected(dir);
				return this;
			}
		}



		// else rotate the piece	
		sndRotate();

		DraggablePiece p = control.getDragged();

		if (control.isDragging() && p != null) {
			// nop
		} else {
			// get tile under.
			CoordI tile = getCoordUnderMouse(pos);
			if (tile == null) return null;
			p = slots[tile.y][tile.x];

		}

		if (p == null) return null;

		rotatePiece(p, scrollUnit);
		checkDesign();

		return null;

	}

	@Override
	public void render(Coord mouse) {
		super.render(mouse);
	}

	/**
	 * Repair piece and consume money
	 * 
	 * @param piece piece to repair
	 */
	private void repairPiece(DraggablePiece piece) {
		if (piece == null) return;
		control.consumeMoney(piece.getRepairCost());

		if (piece.health < piece.healthMax) sndRepair();

		piece.health = piece.healthMax;
	}


	public void rotatePiece(DraggablePiece p, int dir) {
		int rough = 45;
		int smooth = p.rotStep;

		if (p.rotStep > rough) rough = p.rotStep;

		if (Keyboard.isKeyDown(keyRotRough)) {
			p.rotate = Deg.roundX(p.rotate, rough);
			p.rotate += dir * rough;
		} else {
			p.rotate += dir * smooth;
		}

		p.rotate = (int) Deg.norm(p.rotate);
	}

	public void rotateSelected(final int dir) {
		doForSelected(pieceRotFn, dir);
		checkDesign();
	}


	/**
	 * Give back money for given piece
	 * 
	 * @param piece
	 */
	public void sellPiece(DraggablePiece piece) {
		sndDelete();
		sellPieceSilent(piece);
	}


	private void sellPieceSilent(DraggablePiece piece) {
		if (piece == null) return;
		if (!isBuilding()) control.addMoney(piece.getTotalValue());
	}

	public void unselectNullSlots() {
		Iterator<CoordI> iter = selectedSlots.iterator();
		while (iter.hasNext()) {
			CoordI tile = iter.next();
			if (slots[tile.y][tile.x] == null) {
				iter.remove();
				continue;
			}
		}
	}


	/**
	 * Set table edit mode.
	 * 
	 * @param mode edit mode.
	 */
	public void setEditMode(EdMode mode) {
		editMode = mode;
	}

	public void unselectAll() {
		selectedSlots.clear();
	}

	public void selectAll() {
		selectedSlots.clear();
		for (int x = 0; x < W; x++)
			for (int y = 0; y < H; y++)
				if (slots[y][x] != null) selectedSlots.add(new CoordI(x, y));
	}

	public void selectWeapons() {
		selectedSlots.clear();
		for (int x = 0; x < W; x++)
			for (int y = 0; y < H; y++)
				if (slots[y][x] != null && slots[y][x].typeWeapon) selectedSlots.add(new CoordI(x, y));
	}

	public void selectBody() {
		selectedSlots.clear();
		for (int x = 0; x < W; x++)
			for (int y = 0; y < H; y++)
				if (slots[y][x] != null && slots[y][x].typeBody) selectedSlots.add(new CoordI(x, y));
	}

	public void deleteSelected() {

		if (!control.canEdit()) return;

		boolean snd = false;
		for (CoordI tile : selectedSlots) {
			DraggablePiece p = slots[tile.y][tile.x];
			if (p != null) snd = true;
			sellPieceSilent(p);
			slots[tile.y][tile.x] = null;
		}
		if (snd) sndDelete();

		checkDesign();
	}

	@Override
	protected boolean hasSlotLevelOverlay(int x, int y) {
		return true;
	}

}
