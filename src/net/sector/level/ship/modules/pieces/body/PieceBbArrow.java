package net.sector.level.ship.modules.pieces.body;


import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBbArrow extends PiecePointBase {

	public PieceBbArrow() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bb_arrow;
	}

}
