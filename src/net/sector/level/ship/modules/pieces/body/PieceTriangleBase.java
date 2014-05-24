package net.sector.level.ship.modules.pieces.body;


import net.sector.level.ship.modules.pieces.PieceBodyBase;

import com.porcupine.math.Calc.Deg;


public abstract class PieceTriangleBase extends PieceBodyBase {

	@Override
	public boolean canConnectToSide(int x, int z) {
		int rot = Deg.round90(pieceRotate);
		if (rot == 0) {
			return (x < 0 && z == 0) || (z < 0 && x == 0);
		}
		if (rot == 90) {
			return (x > 0 && z == 0) || (z < 0 && x == 0);
		}
		if (rot == 180) {
			return (x > 0 && z == 0) || (z > 0 && x == 0);
		}
		if (rot == 270) {
			return (x < 0 && z == 0) || (z > 0 && x == 0);
		}
		return false;
	}

	@Override
	public double getPieceMass() {
		return 0.5;
	}

}
