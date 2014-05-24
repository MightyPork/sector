package net.sector.level.ship.modules.pieces.body;


import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBsPoint extends PiecePointBase {

	public PieceBsPoint() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bs_point;
	}

}
