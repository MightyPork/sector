package net.sector.level.ship.modules.pieces.body;


import net.sector.effects.Effects;
import net.sector.level.ship.modules.pieces.PieceBodyBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class PieceEngineIon extends PieceBodyBase {

	public PieceEngineIon() {}

	@Override
	public RenderModel getModel() {
		return Models.piece_engine_big;
	}

	@Override
	public boolean canConnectToSide(int x, int z) {
		return x == 0 && z > 0;
	}

	@Override
	public int getPieceRotateStep() {
		return 360;
	}

	@Override
	public int getBaseCost() {
		return 1000;
	}

	@Override
	public boolean isEngine() {
		return true;
	}

	@Override
	public double getEnginePoints() {
		return Math.pow(techLevel, 2) * 0.6;
	}

	@Override
	public boolean isBody() {
		return false;
	}

	@Override
	public double getHealthMax() {
		return 4;
	}

	@Override
	public double getResistance() {
		return 1 + (techLevel - 1) * 1;
	}

	@Override
	public void update() {
		if (isDead) return;

		Vec motion = getEntity().getMotion();

		tryToConsumeEnergy(techLevel * 40 * (0.4 + Math.abs(motion.x) * 7));

		Vec front = CoordUtils.getLocalAxisZ(getRotY() + pieceRotate).norm(body.pieceDist * 0.5);
		Coord pos = getAbsoluteCoord().sub(front);

		Effects.addEngineFire(getScene().particles, pos, front.norm(0.032 + 0.006 * techLevel).neg(), Calc.clampi(techLevel + 2, 1, 7), 1);
	}

}
