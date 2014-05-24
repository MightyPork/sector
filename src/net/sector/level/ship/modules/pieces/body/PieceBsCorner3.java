package net.sector.level.ship.modules.pieces.body;


import net.sector.level.ship.modules.pieces.PieceBodyBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBsCorner3 extends PieceBodyBase {

	public PieceBsCorner3() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bs_corner3;
	}

}
