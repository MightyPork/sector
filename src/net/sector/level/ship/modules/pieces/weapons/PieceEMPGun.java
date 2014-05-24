package net.sector.level.ship.modules.pieces.weapons;


import net.sector.Constants;
import net.sector.entities.shots.EntityEMP;
import net.sector.input.EInput;
import net.sector.input.Routine;
import net.sector.input.TriggerBundle;
import net.sector.level.ship.modules.pieces.PieceWeaponBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


public class PieceEMPGun extends PieceWeaponBase {

	private int counter = 0;

	private class FnShoot implements Routine {
		@Override
		public void run() {
			if (counter > 0) return;
			if (!tryToConsumeEnergy(techLevel * 1200)) return;

			Vec front = CoordUtils.getLocalAxisZ(getRotY() + pieceRotate).norm(body.pieceDist);
			Coord lpos = getAbsoluteCoord().add(front);

			addEntityToScene(new EntityEMP(lpos, front, getEntity(), techLevel).setGlobalMovement(false));

			counter = (int) (Constants.FPS_UPDATE * (5 - techLevel * 0.4));
		}
	}

	@Override
	public void update() {
		if (counter > 0) counter--;
	}

	public PieceEMPGun() {
		initAction(new TriggerBundle(EInput.BTN_DOWN, 1), new FnShoot());
	}

	@Override
	public RenderModel getModel() {
		return Models.piece_w_emp;
	}

	@Override
	public double getHealthMax() {
		return 3;
	}

	@Override
	public int getBaseCost() {
		return 1600;
	}

}
