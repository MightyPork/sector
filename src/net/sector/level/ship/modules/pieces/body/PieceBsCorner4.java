package net.sector.level.ship.modules.pieces.body;


import net.sector.level.ship.modules.pieces.PieceBodyBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBsCorner4 extends PieceBodyBase {

	public PieceBsCorner4() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bs_corner4;
	}

	@Override
	public int getPieceRotateStep() {
		return 360;
	}

}
