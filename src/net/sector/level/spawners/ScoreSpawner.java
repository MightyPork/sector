package net.sector.level.spawners;


import java.util.Map;

import net.sector.Constants;
import net.sector.collision.Scene;
import net.sector.level.SuperContext;
import net.sector.level.dataobj.AiObjParser;
import net.sector.util.Utils;

import com.porcupine.math.Range;


/**
 * Score generator
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ScoreSpawner extends SpawnerBase {

	private Range money = new Range(0);
	private Range pause = new Range(1);

	private long counter = 0;
	private double nextReward = 0;

	/**
	 * Score generator
	 * 
	 * @param scene scene
	 */
	public ScoreSpawner(Scene scene) {
		super(scene);
	}

	/**
	 * Spawn score
	 */
	@Override
	public void onUpdate() {
		counter++;
		double secs = ((double) counter) / ((double) Constants.FPS_UPDATE);
		if (secs >= nextReward) {
			SuperContext.getGameContext().getCursor().addScore(money.randInt());
			nextReward = secs + pause.randDouble();
		}
	}

	@Override
	public void loadFromXmlArgs(Map<String, Object> args) {
		/*
		 * <money num="10" /> // also coins, points
		 * <delay num="10" /> // also: gap, pause, time, every, secs
		 * 
		 */

		money = AiObjParser.getRange(Utils.fallback(args.get("money"), args.get("coins"), args.get("points"), 0), money);

		pause = AiObjParser.getRange(
				Utils.fallback(args.get("pause"), args.get("time"), args.get("gap"), args.get("delay"), args.get("every"), args.get("secs"), 1),
				pause);
	}
}
