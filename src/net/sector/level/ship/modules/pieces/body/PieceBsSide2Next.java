package net.sector.level.ship.modules.pieces.body;


import net.sector.level.ship.modules.pieces.PieceBodyBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBsSide2Next extends PieceBodyBase {

	public PieceBsSide2Next() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bs_side2_next;
	}

}
