package net.sector.level.ship.modules.pieces.body;


import net.sector.level.ship.modules.pieces.PieceBodyBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBwCube extends PieceBodyBase {

	public PieceBwCube() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bw_cube;
	}

	@Override
	public int getBaseCost() {
		return 60;
	}

	@Override
	public double getResistance() {
		return 1 + (techLevel - 1) * 0.2;
	}

	@Override
	public int getPieceRotateStep() {
		return 360;
	}

	@Override
	public double getPieceMass() {
		return super.getPieceMass() * 0.25;
	}

}
