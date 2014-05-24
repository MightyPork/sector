package net.sector.level.ship.modules.pieces.body;


import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBsTriangle extends PieceTriangleBase {

	public PieceBsTriangle() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bs_triangle;
	}

}
