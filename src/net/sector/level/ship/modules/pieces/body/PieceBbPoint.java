package net.sector.level.ship.modules.pieces.body;


import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBbPoint extends PiecePointBase {

	public PieceBbPoint() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bb_point;
	}

}
