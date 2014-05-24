package net.sector.gui.widgets.composite;


import static net.sector.gui.widgets.EColorRole.*;
import static org.lwjgl.opengl.GL11.*;
import net.sector.gui.widgets.Widget;
import net.sector.sounds.Sounds;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.CoordI;


/**
 * ship designer table
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class ShipDesignerBase extends Widget {

	protected ShipDesignerController control = null;

	public Widget setDragController(ShipDesignerController ctrl) {
		control = ctrl;
		return this;
	}

	protected int W = 9;
	protected int H = 9;
	public DraggablePiece[][] slots;

	protected double slotSize = 40;
	protected double line = 2;

	protected Coord lastRenderMouse = new Coord();

	public ShipDesignerBase(int w, int h) {
		W = w;
		H = h;
		slots = new DraggablePiece[H][W];
	}

	protected abstract boolean isCentralSlotHighlited();

	protected RGB getBgColor() {
		return getColor(BG);
	}

	protected RGB getCenterColor() {
		return getColor(FG);
	}

	protected RGB getLineColor() {
		return getColor(BDR);
	}

	@Override
	public void render(Coord mouse) {

		lastRenderMouse.setTo(mouse);

		glPushMatrix();
		// BG
		RenderUtils.setColor(getBgColor());
		RenderUtils.quadCoord(rect.getMin().x, rect.getMin().y, rect.getMax().x, rect.getMax().y);

		if (isCentralSlotHighlited()) {
			RenderUtils.setColor(getCenterColor());
			RenderUtils.quadCoord(rect.getCenter().x - slotSize / 2, rect.getCenter().y - slotSize / 2, rect.getCenter().x + slotSize / 2,
					rect.getCenter().y + slotSize / 2);
		}
		// LINES
		RenderUtils.setColor(getLineColor());

		// vertical		
		for (int i = 0; i < W + 1; i++) {
			RenderUtils.quadCoord(rect.getMin().x + i * (line + slotSize), rect.getMin().y, rect.getMin().x + i * (line + slotSize) + line,
					rect.getMax().y);
		}

		// horizontal
		for (int i = 0; i < H + 1; i++) {
			RenderUtils.quadCoord(rect.getMin().x, rect.getMin().y + i * (line + slotSize), rect.getMax().x, rect.getMin().y + i * (line + slotSize)
					+ line);
		}


		// piece models
		for (int x = 0; x < W; x++) {
			for (int y = 0; y < H; y++) {
				glPushMatrix();
				glTranslated(rect.getMin().x + x * (line + slotSize) + line + slotSize / 2, rect.getMin().y + y * (line + slotSize) + line + slotSize
						/ 2, 5);

				if (isSlotBad(x, H - 1 - y)) {
					RenderUtils.setColor(new RGB(1, 0, 0, 0.6));
					RenderUtils.quadSize(-slotSize / 2, -slotSize / 2, slotSize, slotSize);
					RenderUtils.setColor(RGB.WHITE);
				}

				if (slots[H - 1 - y][x] != null) slots[H - 1 - y][x].renderModel();
				glPopMatrix();
			}
		}

		// piece labels
		for (int x = 0; x < W; x++) {
			for (int y = 0; y < H; y++) {
				if (slots[H - 1 - y][x] == null) continue;
				if (!hasSlotLevelOverlay(x, H - 1 - y)) continue;
				glPushMatrix();
				glTranslated(rect.getMin().x + x * (line + slotSize) + line + slotSize / 2, rect.getMin().y + y * (line + slotSize) + line + slotSize
						/ 2, 20);
				slots[H - 1 - y][x].renderLabels(isSlotSelected(x, H - 1 - y));
				glPopMatrix();
			}
		}

		glPopMatrix();
	}

	/**
	 * Get if slot at given coords has been marked as selected and should be
	 * rendered with a hash mark.
	 * 
	 * @param x x coord
	 * @param y y coord
	 * @return is selected
	 */
	protected abstract boolean isSlotSelected(int x, int y);



	/**
	 * Get if slot is erroneous
	 * 
	 * @param x slot x coord
	 * @param y slot y coord
	 * @return is bad
	 */
	protected abstract boolean isSlotBad(int x, int y);

	/**
	 * Get cell coord under mouse (null if outside this widget)
	 * 
	 * @param mouse mouse pos
	 * @return cell coord
	 */
	protected final CoordI getCoordUnderMouse(Coord mouse) {
		if (!isMouseOver(mouse)) return null;
		Coord pos = mouse.copy();

		pos.sub_ip(rect.getMin());
		CoordI c = new CoordI((int) Math.floor(pos.x / (slotSize + line)), (int) (H - 1 - Math.floor(pos.y / (slotSize + line))));
		if (c.x < 0 || c.x >= W || c.y < 0 || c.y >= H) return null;
		return c;
	}

	protected final void sndGrab() {
		Sounds.click2.playEffect(1.3f, 0.3f, false);
	}

	protected final void sndDrop() {
		Sounds.click2.playEffect(0.8f, 0.3f, false);
	}

	protected final void sndDelete() {
		Sounds.click2.playEffect(0.5f, 0.4f, false);
	}

	protected final void sndLevelUp() {
		Sounds.des_level_up.playEffect(1f, 0.8f, false);
	}

	protected final void sndLevelDown() {
		Sounds.des_level_down.playEffect(1f, 0.8f, false);
	}

	protected final void sndRepair() {
		Sounds.des_repair.playEffect(0.7f, 1, false);
	}

	protected final void sndRotate() {
		Sounds.click1.playEffect(1.5f, 0.2f, false);
	}

	protected final void sndBuild() {
		sndDrop();
	}

	@Override
	public abstract Widget onMouseButton(Coord pos, int button, boolean down);

	@Override
	public void onBlur() {}

	@Override
	public abstract Widget onScroll(Coord pos, int scroll);

	@Override
	public abstract Widget onKey(int key, char chr, boolean down);

	@Override
	public final void calcChildSizes() {
		setMinSize(new Coord((slotSize + line) * W + line, (slotSize + line) * H + line));
		rect.setTo(0, 0, minSize.x, minSize.y);
	}

	protected abstract boolean hasSlotLevelOverlay(int x, int y);

}
