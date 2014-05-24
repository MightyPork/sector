package net.sector.level.ship.modules.pieces;


import net.sector.models.wavefront.loader.RenderModel;

import com.porcupine.math.Calc.Deg;


/**
 * Weapon stub
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class PieceWeaponBase extends Piece {

	@Override
	public double getPieceMass() {
		return 0.75;
	}

	@Override
	public double getResistance() {
		return techLevel * 1.5;
	}

	@Override
	public abstract RenderModel getModel();

	@Override
	public double getRenderScale() {
		return 1;
	}

	@Override
	public boolean isWeapon() {
		return true;
	}

	@Override
	public abstract double getHealthMax();

	@Override
	public boolean canConnectToSide(int x, int z) {
		int rot = Deg.round45(pieceRotate);
		if (rot == 0) {
			return z < 0 && x == 0;
		}
		if (rot == 45) {
			return z < 0 && x > 0;
		}
		if (rot == 90) {
			return z == 0 && x > 0;
		}
		if (rot == 135) {
			return z > 0 && x > 0;
		}
		if (rot == 180) {
			return z > 0 && x == 0;
		}
		if (rot == 225) {
			return z > 0 && x < 0;
		}
		if (rot == 270) {
			return z == 0 && x < 0;
		}
		if (rot == 315) {
			return z < 0 && x < 0;
		}
		return false;
	}

	@Override
	public int getPieceRotateStep() {
		return 5;
	}

	@Override
	public int getBaseCost() {
		return 500;
	}

}
