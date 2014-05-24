package net.sector.level.ship.modules.pieces.weapons;


import net.sector.Constants;
import net.sector.entities.shots.EntityLaser;
import net.sector.input.EInput;
import net.sector.input.Routine;
import net.sector.input.TriggerBundle;
import net.sector.level.ship.modules.pieces.PieceWeaponBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


public class PieceLaser extends PieceWeaponBase {

	private int counter = 0;

	private class FnShoot implements Routine {
		@Override
		public void run() {
			if (counter > 0) return;
			if (!tryToConsumeEnergy(techLevel * 120)) return;

			Vec front = CoordUtils.getLocalAxisZ(getRotY() + pieceRotate).norm(body.pieceDist);
			Coord lpos = getAbsoluteCoord().add(front);

			int[] colors = { 0x00ff00, 0xffee00, 0xff0066, 0xdd00dd, 0x6600ff, 0x0000ff };

			RGB color = RGB.fromHex(colors[techLevel - 1]);

			addEntityToScene(new EntityLaser(lpos, front, getEntity(), color, techLevel).setGlobalMovement(false));

			counter = (int) (Constants.FPS_UPDATE * (0.3 - techLevel * 0.03));
		}
	}

	@Override
	public void update() {
		if (counter > 0) counter--;
	}



	public PieceLaser() {
		initAction(new TriggerBundle(EInput.BTN_DOWN, 0), new FnShoot());
	}

	@Override
	public RenderModel getModel() {
		return Models.piece_w_laser;
	}

	@Override
	public double getHealthMax() {
		return 2;
	}

	@Override
	public int getBaseCost() {
		return 350;
	}

}
