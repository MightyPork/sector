package net.sector.gui.widgets.composite;


import static net.sector.level.EBuildingMode.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.HashSet;
import java.util.Set;

import net.sector.gui.widgets.Widget;
import net.sector.level.EBuildingMode;
import net.sector.level.ship.PieceRegistry;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;


/**
 * Ship piece drag controller GUI (no size).<br>
 * In design must be after all other tables.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ShipDesignerController extends Widget {

	public int money = 0;

	private boolean dragging = false;
	private DraggablePiece dragged = null;
	/** coord in table the piece was picked from */
	protected CoordI pickFrom = new CoordI(-1, -1);
	private ShipDesignerBase dragFrom = null;

	public EBuildingMode buildMode = NORMAL;

	public boolean boughtPiece = false;

	private Set<ShipDesignerBase> tables = new HashSet<ShipDesignerBase>();

	public boolean infoIsBought = false;

	public boolean infoRepair = false;
	public int infoRepairCost = 0;

	public boolean infoValue = false;
	public int infoValueCost = 0;

	public boolean infoBuy = false;
	public int infoBuyCost = 0;

	public boolean infoUpgrade = false;
	public int infoUpgradeCost = 0;

	public boolean infoDowngrade = false;
	public int infoDowngradeCost = 0;

	public String pieceTriggerDesc = "";

	public boolean canEdit() {
		return buildMode != LOCKED;
	}

	/**
	 * Drag controller
	 * 
	 * @param money total money
	 */
	public ShipDesignerController(int money) {
		setMargins(0, 0, 0, 0);
		setMinSize(0, 0);
		setMoney(money);
	}

	/**
	 * Get total money stored in the controller
	 * 
	 * @return money
	 */
	public int getTotalMoney() {
		return money;
	}

	/**
	 * Check if has money
	 * 
	 * @param needed needed money
	 * @return has enough
	 */
	public boolean hasMoney(int needed) {
		if (buildMode == FREE) return true;
		return money >= needed;
	}

	/**
	 * Consume given amount of money if has enough
	 * 
	 * @param needed needed money
	 * @return consumed all
	 */
	public boolean consumeMoney(int needed) {
		if (buildMode == FREE) return true;
		if (hasMoney(needed)) {
			money -= needed;
			return true;
		}
		return false;
	}

	/**
	 * Set total money
	 * 
	 * @param total total money
	 */
	public void setMoney(int total) {
		money = total;
	}

	/**
	 * Add money to storage
	 * 
	 * @param added added money
	 */
	public void addMoney(int added) {
		money += added;
	}

	/**
	 * Register slot table and connect to this controller
	 * 
	 * @param table the table added
	 */
	public void addTable(ShipDesignerBase table) {
		tables.add(table);
		table.setDragController(this);
	}

	/**
	 * Start dragging a piece
	 * 
	 * @param from table it was grabbed in
	 * @param startPos pos in table
	 * @param piece piece dragged
	 * @param isPieceBought true if cancelDrag places this piece back to
	 *            original table.
	 */
	public void startDrag(ShipDesignerBase from, CoordI startPos, DraggablePiece piece, boolean isPieceBought) {
		dragFrom = from;
		pickFrom.setTo(startPos);
		dragging = true;
		dragged = piece;
		boughtPiece = isPieceBought;
	}

	/**
	 * Is dragging a valid piece?
	 * 
	 * @return is dragging
	 */
	public boolean isDragging() {
		return dragging && dragged != null;
	}

	/**
	 * Get dragged piece
	 * 
	 * @return dragged piece
	 */
	public DraggablePiece getDragged() {
		return dragged;
	}

	@Override
	public void render(Coord mouse) {
		if (!isPanelOnTop()) return;
		infoBuy = infoRepair = infoUpgrade = infoValue = infoUpgrade = infoDowngrade = false;
		pieceTriggerDesc = "";
		infoIsBought = false;
		if (isDragging()) {
			if (dragged.hasTrigger()) pieceTriggerDesc = dragged.getTrigger().getLabel(false);
			if (boughtPiece) {
				infoIsBought = true;
				infoValue = true;
				infoValueCost = dragged.getTotalValue();

				if (dragged.isDamaged()) {
					infoRepair = true;
					infoRepairCost = dragged.getRepairCost();
				}

				if (dragged.canBeUpgraded()) {
					infoUpgrade = true;
					infoUpgradeCost = dragged.getLevelChangeCost(1);
				}
				if (dragged.canBeDowngraded()) {
					infoDowngrade = true;
					infoDowngradeCost = dragged.getLevelChangeCost(-1);
				}
			} else {
				infoIsBought = false;
				infoBuy = true;
				infoBuyCost = dragged.getTotalValue();

				if (dragged.canBeUpgraded()) {
					infoUpgrade = true;
					infoUpgradeCost = dragged.getLevelChangeCost(1);
				}

				if (dragged.canBeDowngraded()) {
					infoDowngrade = true;
					infoDowngradeCost = dragged.getLevelChangeCost(-1);
				}
			}
		}

		// render dragged model
		if (isDragging() && getGuiRoot().isPanelOnTop()) {
			glPushMatrix();
			glTranslated(mouse.x, mouse.y, 50);
			dragged.renderModel();
			dragged.renderLabels(false);
			glPopMatrix();
		}

		if (isDragging()) return;

		for (ShipDesignerBase table : tables) {
			CoordI tile = null;
			DraggablePiece piece = null;
			if ((tile = table.getCoordUnderMouse(table.lastRenderMouse)) != null && (piece = table.slots[tile.y][tile.x]) != null) {

				if (piece.hasTrigger()) pieceTriggerDesc = piece.getTrigger().getLabel(false);

				if (table instanceof ShipDesignerTable) {
					infoIsBought = true;
					infoValue = true;
					infoValueCost = piece.getTotalValue();
				} else {
					infoIsBought = false;
					infoBuy = true;
					infoBuyCost = piece.getTotalValue();
				}

				if (piece.canBeUpgraded()) {
					infoUpgrade = true;
					infoUpgradeCost = piece.getLevelChangeCost(1);
				}

				if (piece.canBeDowngraded()) {
					infoDowngrade = true;
					infoDowngradeCost = piece.getLevelChangeCost(-1);
				}

				if (piece.isDamaged()) {
					infoRepair = true;
					infoRepairCost = piece.getRepairCost();
				}

				getGuiRoot().setTooltip(PieceRegistry.getPieceLabel(piece.id), RGB.WHITE);
				break;
			}
		}
	}

	/**
	 * Reset drag info, discard dragged piece and data.
	 */
	public void resetDragInfo() {
		pickFrom.setTo(-1, -1);
		dragged = null;
		dragging = false;
		boughtPiece = false;
	}

	/**
	 * Cancel drag, return piece to orig position if allowed, and reset drag
	 * info.
	 */
	public void cancelDrag() {
		if (dragging && dragged != null && boughtPiece) dragFrom.slots[pickFrom.y][pickFrom.x] = dragged;
		resetDragInfo();
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
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
	public void calcChildSizes() {
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

}
