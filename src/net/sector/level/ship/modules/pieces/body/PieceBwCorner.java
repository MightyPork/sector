package net.sector.level.ship.modules.pieces.body;


import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;


public class PieceBwCorner extends PieceTriangleBase {

	public PieceBwCorner() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_bw_triangle;
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
	public double getPieceMass() {
		return super.getPieceMass() * 0.25;
	}

}
