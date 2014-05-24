package net.sector.gui.widgets.composite;


import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.Widget;
import net.sector.gui.widgets.composite.ShipDesignerTable.EdMode;
import net.sector.level.ship.DiscoveryTable;
import net.sector.level.ship.PieceRegistry;
import net.sector.level.ship.PieceRegistry.PieceEntry;

import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;


/**
 * ship designer table
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ShipDesignerShop extends ShipDesignerBase {

	private ShipDesignerTable table = null;
	private DiscoveryTable discoveries;

	/**
	 * Designer Shop
	 * 
	 * @param table table
	 * @param discoveries discovery table
	 */
	public ShipDesignerShop(ShipDesignerTable table, DiscoveryTable discoveries) {
		super(3, 9);
		this.table = table;
		this.discoveries = discoveries;

		fillWithPieceGroup(PieceRegistry.designerDefaultGroup);

		setTheme(ETheme.DES_PANEL);
	}

	/**
	 * Erase shop
	 */
	public void clearSlots() {
		for (int x = 0; x < W; x++) {
			for (int y = 0; y < H; y++) {
				slots[y][x] = null;
			}
		}
	}

	/**
	 * Fill shop with pieces from a group
	 * 
	 * @param group group to use
	 */
	public void fillWithPieceGroup(String group) {
		clearSlots();
		int cnt = 0;
		for (PieceEntry pc : PieceRegistry.pieces.values()) {
			if (pc.group.equals(group)) {
				if (discoveries.isDiscovered(pc.discovery)) {
					DraggablePiece p = slots[cnt / W][cnt % W] = new DraggablePiece(pc.id);
					if (p.typeWeapon) p.rotate = -30;
					cnt++;
				}
			}
		}
	}



	@Override
	public void render(Coord mouse) {
		super.render(mouse);
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (!control.canEdit()) return null;

		boolean b0 = button == 0;
		boolean up = !down;

		boolean dragging = control.isDragging();

		// RMB to cancel building.
		if (isMouseOver(pos)) {
			// init one build
			if (b0 && down && (!dragging || table.isBuilding())) {
				CoordI tile = getCoordUnderMouse(pos);
				if (tile == null) return this;
				DraggablePiece p = slots[tile.y][tile.x];
				if (p == null) return this;
				p = p.copy();
				p.rotate = 0;
				control.startDrag(this, tile, p, false);
				table.editMode = EdMode.BUILD_ONE;
				sndGrab();
				return this;
			}

			// click - release → paint building
			if (b0 && up && dragging && table.isBuilding()) {
				table.editMode = EdMode.BUILD;
				return this;
			}

			// trash function
			if (b0 && !down && dragging && !table.isBuilding()) {
				table.sellPiece(control.getDragged());
				control.resetDragInfo();
				return this;
			}
		}

		return null;
	}

	@Override
	public Widget onScroll(Coord pos, int scroll) {
		return null;
	}

	@Override
	public Widget onKey(int key, char chr, boolean down) {
		return null;
	}

	@Override
	protected boolean isCentralSlotHighlited() {
		return false;
	}


	@Override
	protected boolean isSlotSelected(int x, int y) {
		return false;
	}

	@Override
	protected boolean isSlotBad(int x, int y) {
		return false;
	}

	@Override
	protected boolean hasSlotLevelOverlay(int x, int y) {
		return false;
	}

}
