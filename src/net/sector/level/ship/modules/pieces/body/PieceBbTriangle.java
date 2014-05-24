package net.sector.level.ship.modules.pieces.body;


import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBbTriangle extends PieceTriangleBase {

	public PieceBbTriangle() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bb_triangle;
	}

}
