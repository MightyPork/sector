package net.sector.level.ship.modules.pieces;


import net.sector.models.wavefront.loader.RenderModel;


/**
 * Ship body piece
 * 
 * @author MightyPork
 */
public abstract class PieceBodyBase extends Piece {

	@Override
	public boolean isBody() {
		return true;
	}

	@Override
	public double getResistance() {
		return 1 + (techLevel - 1) * 2;
	}

	@Override
	public abstract RenderModel getModel();

	@Override
	public double getRenderScale() {
		return 1;
	}

	@Override
	public double getHealthMax() {
		return 1.5;
	}

	@Override
	public boolean canConnectToSide(int x, int z) {
		return x == 0 || z == 0;
	}

	@Override
	public int getPieceRotateStep() {
		return 90;
	}

	@Override
	public int getBaseCost() {
		return 100;
	}

	@Override
	public double getPieceMass() {
		return 1;
	}

}
