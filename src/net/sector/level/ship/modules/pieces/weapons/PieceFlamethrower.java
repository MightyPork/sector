package net.sector.level.ship.modules.pieces.weapons;


import net.sector.Constants;
import net.sector.entities.shots.EntityFireball;
import net.sector.input.EInput;
import net.sector.input.Routine;
import net.sector.input.TriggerBundle;
import net.sector.level.ship.modules.pieces.PieceWeaponBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


public class PieceFlamethrower extends PieceWeaponBase {

	private int counter = 0;

	private boolean brand_new = true;


	private double napalmContainer = 0;
	private boolean ignited = false;

	private double getNapalmContainerMax() {
		return (Constants.FPS_UPDATE * (0.2 + techLevel * 0.2));
	}

	private class FnShoot implements Routine {
		@Override
		public void run() {
			if (!ignited) {
				if (napalmContainer < getNapalmContainerMax() * 0.8) {
					return;
				}
				ignited = true;
			}

			if (!tryToConsumeEnergy(Math.pow(techLevel, 1.1) * 100)) {
				ignited = false;
				napalmContainer = 0;
				return;
			}

			if (counter > 0) return;

			Vec front = CoordUtils.getLocalAxisZ(getRotY() + pieceRotate).norm(body.pieceDist);
			Coord lpos = getAbsoluteCoord().add(front);

			addEntityToScene(new EntityFireball(lpos, front, getEntity(), techLevel));

			counter = (int) (Constants.FPS_UPDATE * 0.04F);
			napalmContainer -= counter;

			if (napalmContainer <= 0) {
				napalmContainer = 0;
				ignited = false;
			}

		}
	}

	@Override
	public void update() {
		if (brand_new) {
			napalmContainer = getNapalmContainerMax();
			brand_new = false;
		}
		if (napalmContainer < getNapalmContainerMax()) {
			napalmContainer += 0.3;
		}

		if (counter > 0) counter--;
	}

	public PieceFlamethrower() {
		initAction(new TriggerBundle(EInput.BTN_DOWN, 1), new FnShoot());
	}

	@Override
	public RenderModel getModel() {
		return Models.piece_w_flame;
	}

	@Override
	public double getHealthMax() {
		return 4;
	}

	@Override
	public int getBaseCost() {
		return 1800;
	}


}
