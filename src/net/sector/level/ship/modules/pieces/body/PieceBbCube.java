package net.sector.level.ship.modules.pieces.body;


import net.sector.level.ship.modules.pieces.PieceBodyBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBbCube extends PieceBodyBase {

	public PieceBbCube() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bb_cube;
	}

	@Override
	public int getPieceRotateStep() {
		return 360;
	}

}
