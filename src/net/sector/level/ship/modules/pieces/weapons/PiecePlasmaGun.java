package net.sector.level.ship.modules.pieces.weapons;


import net.sector.Constants;
import net.sector.entities.shots.EntityPlasma;
import net.sector.input.EInput;
import net.sector.input.Routine;
import net.sector.input.TriggerBundle;
import net.sector.level.ship.modules.pieces.PieceWeaponBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


public class PiecePlasmaGun extends PieceWeaponBase {

	private int counter = 0;

	private class FnShoot implements Routine {
		@Override
		public void run() {
			if (counter > 0) return;
			if (!tryToConsumeEnergy(techLevel * 180)) return;

			Vec front = CoordUtils.getLocalAxisZ(getRotY() + pieceRotate).norm(body.pieceDist);
			Coord lpos = getAbsoluteCoord().add(front);

			addEntityToScene(new EntityPlasma(lpos, front, getEntity(), techLevel).setGlobalMovement(false));

			counter = (int) (Constants.FPS_UPDATE * (0.5 - techLevel * 0.04));
		}
	}

	@Override
	public void update() {
		if (counter > 0) counter--;
	}

	public PiecePlasmaGun() {
		initAction(new TriggerBundle(EInput.BTN_DOWN, 0), new FnShoot());
	}

	@Override
	public RenderModel getModel() {
		return Models.piece_w_plasma;
	}

	@Override
	public double getHealthMax() {
		return 4;
	}

	@Override
	public int getBaseCost() {
		return 400;
	}

}
