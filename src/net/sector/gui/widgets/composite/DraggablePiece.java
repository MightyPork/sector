package net.sector.gui.widgets.composite;


import static org.lwjgl.opengl.GL11.*;
import net.sector.fonts.FontManager;
import net.sector.level.ship.PieceBundle;
import net.sector.level.ship.PieceRegistry;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.Align;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;


/**
 * Variant of PieceBundle used in designer.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class DraggablePiece extends PieceBundle {
	/** Model for designer */
	RenderModel model = null;

	/**
	 * create draggable piece from name
	 * 
	 * @param id piece name
	 */
	public DraggablePiece(String id) {
		super(id);
		model = PieceRegistry.getModel(id);
	}

	/**
	 * Create draggable piece as copy of another
	 * 
	 * @param draggablePiece other piece
	 */
	public DraggablePiece(DraggablePiece draggablePiece) {
		super(draggablePiece);
		model = draggablePiece.model;
	}

	/**
	 * Create from bundle
	 * 
	 * @param bundle bundle
	 */
	public DraggablePiece(PieceBundle bundle) {
		super(bundle);
		model = PieceRegistry.getModel(id);
	}

	/**
	 * Convert to ordinary bundle
	 * 
	 * @return bundle
	 */
	public PieceBundle asBundle() {
		return this;
	}

	/**
	 * Render model in designer
	 */
	public void renderModel() {
		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);
		Models.renderBegin();
		glScaled(15, 15, 15);
		glRotated(90, 1, 0, 0);
		glRotated(rotate, 0, 1, 0);
		model.render();
		Models.renderEnd();
		glPopAttrib();
		glPopMatrix();

	}

	/**
	 * Render labels in designer (currently only number of level)
	 * 
	 * @param isSelected is piece selected (has # overlay)
	 */
	public void renderLabels(boolean isSelected) {
		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		glTranslated(0, 0, 40);

		Coord txtPos = new Coord(15, -20);

		// calc the color
		RGB clr = PieceRegistry.getDamageColor(health, healthMax);

		// prepare
		FontManager.setFont("designer_level");
		FontManager.setAlign(Align.RIGHT);
		FontManager.setColor(new RGB(0, 0, 0, 0.4));

		String l = level + "";

		// shadows on all sides...
		FontManager.draw(txtPos.add(1, -1), l);
		FontManager.draw(txtPos.add(-1, -1), l);
		FontManager.draw(txtPos.add(1, 1), l);
		FontManager.draw(txtPos.add(-1, 1), l);
		FontManager.draw(txtPos, l, clr);


		if (isSelected) {
			txtPos.setTo(15, -3);

			FontManager.setColor(new RGB(0, 0, 0, 0.4));

			l = "#";

			// shadows on all sides...
			FontManager.draw(txtPos.add(1, -1), l);
			FontManager.draw(txtPos.add(-1, -1), l);
			FontManager.draw(txtPos.add(1, 1), l);
			FontManager.draw(txtPos.add(-1, 1), l);

			clr.setTo(0.3, 0.4, 1, 1);

			FontManager.draw(txtPos, l, clr);
		}

		glPopAttrib();
		glPopMatrix();
	}

	/**
	 * Duplicate
	 */
	@Override
	public DraggablePiece copy() {
		return new DraggablePiece(this);
	}
}
