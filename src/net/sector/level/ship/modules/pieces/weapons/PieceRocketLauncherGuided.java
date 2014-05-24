package net.sector.level.ship.modules.pieces.weapons;


import net.sector.Constants;
import net.sector.entities.shots.EntityMissileGuided;
import net.sector.input.EInput;
import net.sector.input.Routine;
import net.sector.input.TriggerBundle;
import net.sector.level.ship.modules.pieces.PieceWeaponBase;
import net.sector.models.Models;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.util.CoordUtils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;


/**
 * Rocket launcher.
 * 
 * @author MightyPork
 */
public class PieceRocketLauncherGuided extends PieceWeaponBase {

	private int counter = 0;

	private class FnShoot implements Routine {
		@Override
		public void run() {
			if (counter > 0) return;
			if (!tryToConsumeEnergy(techLevel * 1200)) return;

			Vec front = CoordUtils.getLocalAxisZ(getRotY() + pieceRotate).norm(body.pieceDist);
			Coord pos = getAbsoluteCoord().add(front);

			int rocketLevel = techLevel;

			addEntityToScene(new EntityMissileGuided(pos, front, getEntity(), rocketLevel).setGlobalMovement(true));

			counter = (int) (Constants.FPS_UPDATE * (4.2 - techLevel * 0.4));
		}
	}

	@Override
	public void update() {
		if (counter > 0) counter--;
	}

	/**
	 * Rocket launcher (level 3+ is guided missile)
	 */
	public PieceRocketLauncherGuided() {
		initAction(new TriggerBundle(EInput.BTN_DOWN, 1), new FnShoot());
	}

	@Override
	public RenderModel getModel() {
		return Models.piece_w_rocketG;
	}

	@Override
	public double getHealthMax() {
		return 4;
	}

	@Override
	public int getBaseCost() {
		return 700;
	}

}
