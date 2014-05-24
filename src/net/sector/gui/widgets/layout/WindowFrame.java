package net.sector.gui.widgets.layout;


import static net.sector.gui.widgets.EColorRole.*;
import static org.lwjgl.opengl.GL11.*;
import net.sector.gui.widgets.ETheme;
import net.sector.gui.widgets.GuiRoot;
import net.sector.gui.widgets.Widget;
import net.sector.util.Align;
import net.sector.util.RenderUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Frame for widgets (with shadow and border + background)
 * 
 * @author MightyPork
 */
public class WindowFrame extends Widget {

	public int alignH = 0;
	public int alignV = 0;

	public double borderWidth = 3;

	private Widget child = null;
	public double paddingBottom = 3;
	public double paddingLeft = 3;
	public double paddingRight = 3;

	public double paddingTop = 3;

	public double shadowX = 10;
	public double shadowY = 10;

	public boolean showBorder = true;
	public boolean showShadow = true;

	public WindowFrame() {
		setTheme(ETheme.BLUE_FRAME);
	}

	/**
	 * Set the primary and only child.
	 */
	@Override
	public void add(Widget child) {
		this.child = child;
	}

	@Override
	public void calcChildSizes() {
		child.calcChildSizes();

		Coord oldms = getMinSize().copy();

		//@formatter:off
		setMinSize( (int)(Math.max(minSize.x, child.getSize().x + paddingLeft + paddingRight + borderWidth * 2)),
					(int)(Math.max(minSize.y, child.getSize().y + paddingTop + paddingBottom + borderWidth * 2)));
		//@formatter:on

		rect.setTo(0, 0, minSize.x, minSize.y);

		//Coord lbPos = rect.getCenter();
		Coord childMove = new Coord();
		Coord sizeC = child.getSize();

		double bdrL = paddingLeft + borderWidth;
		double bdrT = paddingTop + borderWidth;
		double bdrR = paddingRight + borderWidth;
		double bdrB = paddingBottom + borderWidth;

		switch (alignH) {
			case Align.LEFT:
				childMove.x = bdrL;
				break;
			case Align.RIGHT:
				childMove.x = rect.getSize().x - bdrL - bdrR - sizeC.x;
				break;
			case Align.CENTER:
				childMove.x = rect.getSize().x / 2 - sizeC.x / 2;
				break;
		}


		switch (alignV) {
			case Align.TOP:
				childMove.y = rect.getSize().y - bdrB - bdrT - sizeC.y;
				break;
			case Align.BOTTOM:
				childMove.y = bdrB;
				break;
			case Align.CENTER:
				childMove.y = rect.getSize().y / 2 - sizeC.y / 2;
				break;
		}


		child.rect.add_ip(new Vec(childMove));
	}

	public WindowFrame enableBorder(boolean flag) {
		showBorder = flag;
		if (!flag) borderWidth = 0;
		return this;
	}

	public WindowFrame enableShadow(boolean flag) {
		showShadow = flag;
		return this;
	}

	@Override
	public Widget onKey(int key, char chr, boolean down) {
		if (!isVisible() || !isEnabled()) return null;

		if (!child.isEnabled() || !child.isVisible()) return null;
		return child.onKey(key, chr, down);
	}

	@Override
	public Widget onMouseButton(Coord pos, int button, boolean down) {
		if (!isVisible() || !isEnabled()) return null;

		Coord pos_r = pos.sub(rect.getMin());

		if (!child.isEnabled() || !child.isVisible()) return null;
		return child.onMouseButton(pos_r, button, down);
	}

	@Override
	public Widget onScroll(Coord pos, int scroll) {
		if (!isVisible() || !isEnabled()) return null;

		Coord pos_r = pos.sub(rect.getMin());
		if (!child.isEnabled() || !child.isVisible()) return null;
		return child.onScroll(pos_r, scroll);
	}


	@Override
	public void handleStaticInputs(Coord pos) {
		if (!isVisible() || !isEnabled()) return;

		Coord pos_r = pos.sub(rect.getMin());

		if (!child.isEnabled() || !child.isVisible()) return;
		child.handleStaticInputs(pos_r);
	}

	@Override
	public void render(Coord mouse) {
		if (!isVisible()) return;

		if (showShadow) {
			RenderUtils.setColor(getColor(SHADOW));
			RenderUtils.quadCoord(rect.getMin().x + shadowX, rect.getMin().y - shadowY, rect.getMax().x + shadowX, rect.getMax().y - shadowY);
		}

		if (showBorder) {
			RenderUtils.quadRectBorder(rect, borderWidth, getColor(BDR), getColor(BG));
		} else {
			RenderUtils.setColor(getColor(BG));
			RenderUtils.quadCoord(rect.getMin().x, rect.getMin().y, rect.getMax().x, rect.getMax().y);
		}


		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);

		glTranslated(rect.x1(), rect.y1(), 2);
		child.render(mouse.sub(rect.getMin()));

		glPopAttrib();
		glPopMatrix();
	}

	public WindowFrame setAlignH(int align) {
		alignH = align;
		return this;
	}

	public WindowFrame setAlignV(int align) {
		alignV = align;
		return this;
	}

	public WindowFrame setBorderSize(double width) {
		borderWidth = width;
		showBorder = true;
		return this;
	}

	@Override
	public void setGuiRoot(GuiRoot guiContainer) {
		super.setGuiRoot(guiContainer);
		child.setGuiRoot(guiContainer);
	}

	public WindowFrame setPadding(double left, double right, double top, double bottom) {
		paddingLeft = left;
		paddingRight = right;
		paddingTop = top;
		paddingBottom = bottom;
		return this;
	}


	public WindowFrame setShadowOffset(double shiftX, double shiftY) {
		shadowX = shiftX;
		shadowY = shiftY;
		showShadow = true;
		return this;
	}
}
