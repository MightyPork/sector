package net.sector.level.sequence.nodes;


import java.util.Map;

import net.sector.entities.EFormation;
import net.sector.entities.Entity;
import net.sector.entities.EntityNavigable;
import net.sector.level.dataobj.AiObjParser;
import net.sector.level.loading.XmlUtil;
import net.sector.level.sequence.LevelController;
import net.sector.level.spawners.EntityRegistry;
import net.sector.util.Utils;

import org.jdom2.Element;

import com.porcupine.coord.Coord;
import com.porcupine.math.Range;


/**
 * "Spawn entity" node
 * 
 * @author MightyPork
 */
public class NodeSpawnEntity extends LevelNodeBase {

	private String entity = null;
	private String wave = null;
	private String driver = null;
	private Range count = new Range(1);
	private Range level = new Range(1);
	private String formation = "none";
	private Range healthMul = new Range(1);

	private Range x = new Range(-10, 10);
	private Range z = new Range(100, 120);

	private Range dist = new Range(2);
	private Range variant = new Range(0);
	private Range speed = new Range(1);
	private Range size = new Range(1);
	private boolean uniform = true;

	private int artifact = 0;


	/**
	 * "Setup entity spawner" node
	 * 
	 * @param parent parent tag
	 * @param level level controller
	 */
	public NodeSpawnEntity(LevelNodeBase parent, LevelController level) {
		super(parent, level);
	}

	@Override
	public void reset() {}

	@Override
	public void loadFromXml(Element tag) {
		Map<String, Object> args = XmlUtil.loadArgs(tag);

		/*
		 * <spawn>
		 * 	<entity str="burger" />
		 * 	(opt) <x range="-10-10" />
		 * 	(opt) <z range="100-120" />
		 * 	(opt) <driver str="burger_zone_mad" />
		 * 	(opt) <count num="5" />
		 *  (opt) <formation str="snake" /> none/random, row, line, snake, leader
		 *  (opt) <level num="1" />
		 *  (opt) <speed num="1.3" />
		 *  (opt) <health num="1" />
		 * </spawn>
		 * 
		 */

		entity = AiObjParser.getString(args.get("entity"));

		wave = AiObjParser.getString(Utils.fallback(args.get("wave"), args.get("group")), null);

		driver = AiObjParser.getString(args.get("driver"), null);
		count = AiObjParser.getRange(args.get("count"), count);
		level = AiObjParser.getRange(args.get("level"), level);


		size = AiObjParser.getRange(args.get("size"), size);

		speed = AiObjParser.getRange(args.get("speed"), speed);

		healthMul = AiObjParser.getRange(args.get("health"), healthMul);


		dist = AiObjParser.getRange(Utils.fallback(args.get("dist"), args.get("space"), args.get("distance")), dist);

		variant = AiObjParser.getRange(Utils.fallback(args.get("variant"), args.get("type"), args.get("kind")), variant);

		formation = AiObjParser.getString(Utils.fallback(args.get("formation"), args.get("fleet"), args.get("shape")), formation);

		uniform = AiObjParser.getBoolean(args.get("uniform"), uniform);

		artifact = AiObjParser.getInteger(args.get("artifact"), artifact);

		x = AiObjParser.getRange(args.get("x"), x);
		z = AiObjParser.getRange(args.get("z"), z);
	}

	// formations:
	// none/random, row, line, snake, leader

	@Override
	public boolean execute() {

		if (formation.equals("none") || formation.equals("random")) {

			int mVariant = variant.randInt();
			int mLevel = level.randInt();

			int mCount = count.randInt();
			double mHealthMul = healthMul.randDouble();

			double mSpeed = speed.randDouble();

			for (int i = 0; i < mCount; i++) {
				Coord mPos = new Coord(x.randDouble(), 0, z.randDouble());

				if (!uniform) {
					mVariant = variant.randInt();
					mLevel = level.randInt();
					mSpeed = speed.randDouble();
					mHealthMul = healthMul.randDouble();
				}

				spawnEntity(mPos, mVariant, mLevel, mSpeed, mHealthMul);
			}

			return true;
		}

		if (formation.equals("row")) {

			int mVariant = variant.randInt();
			int mLevel = level.randInt();

			int mCount = count.randInt();

			double mDist = dist.randDouble();
			double mSpeed = speed.randDouble();
			double mHealthMul = healthMul.randDouble();

			double width = mDist * mCount;

			Entity[] pieces = new Entity[mCount];
			Coord mPos = new Coord(x.randDouble(), 0, z.randDouble());
			for (int i = 0; i < mCount; i++) {

				if (!uniform) {
					mVariant = variant.randInt();
					mLevel = level.randInt();
					mSpeed = speed.randDouble();
					mHealthMul = healthMul.randDouble();
				}

				EntityNavigable en = spawnEntity(mPos.add(-width / 2 + i * mDist, 0, 0), mVariant, mLevel, mSpeed, mHealthMul);

				en.setFormation(pieces, EFormation.SHAPE);
				pieces[i] = en;
			}

			return true;
		}

		if (formation.equals("line")) {

			int mVariant = variant.randInt();
			int mLevel = level.randInt();

			int mCount = count.randInt();

			double mDist = dist.randDouble();
			double mSpeed = speed.randDouble();
			double mHealthMul = healthMul.randDouble();

			Coord mPos = new Coord(x.randDouble(), 0, z.randDouble());

			Entity[] pieces = new Entity[mCount];
			for (int i = 0; i < mCount; i++) {

				if (!uniform) {
					mVariant = variant.randInt();
					mLevel = level.randInt();
					mDist = dist.randDouble();
					mSpeed = speed.randDouble();
					mHealthMul = healthMul.randDouble();
				}

				EntityNavigable en = spawnEntity(mPos, mVariant, mLevel, mSpeed, mHealthMul);

				en.setFormation(pieces, EFormation.SHAPE);
				pieces[i] = en;

				mPos.add_ip(0, 0, -mDist);
			}

			return true;
		}

		if (formation.equals("snake") || formation.equals("swarm") || formation.equals("leader")) {
			boolean swarm = (formation.equals("swarm") || formation.equals("leader"));
			Coord mPos = new Coord(x.randDouble(), 0, z.randDouble());
			double mDist = dist.randDouble();
			int mVariant = variant.randInt();
			int mLevel = level.randInt();
			double mSpeed = speed.randDouble();
			double mHealthMul = healthMul.randDouble();

			int mCount = count.randInt();

			Entity[] pieces = new Entity[mCount];

			for (int i = 0; i < mCount; i++) {
				if (swarm) mPos = new Coord(x.randDouble(), 0, z.randDouble());

				if (!uniform) {
					mDist = dist.randDouble();
					mVariant = variant.randInt();
					mLevel = level.randInt();
					mSpeed = speed.randDouble();
					mHealthMul = healthMul.randDouble();
				}

				EntityNavigable en = spawnEntity(mPos, mVariant, mLevel, mSpeed, mHealthMul);

				en.setFormation(pieces, swarm ? EFormation.SWARM : EFormation.SNAKE);
				pieces[i] = en;

				mPos.add_ip(0, 0, -mDist);
			}

			return true;
		}



		return true;
	}


	private EntityNavigable spawnEntity(Coord mPos, int mVariant, int mLevel, double mSpeed, double healthMul) {
		EntityNavigable en = EntityRegistry.buildEntity(entity, size.randDouble(), mPos);

		if (driver != null) en.setDriver(getDriverStore().getDriver(driver));
		en.setShipLevel(mLevel);
		en.setShipVariant(mVariant);
		en.setStableSpeedMultiplier(mSpeed);
		en.healthMul = healthMul;
		if (artifact > 0) en.addArtifacts(artifact);

		getScene().add(en);
		if (wave != null) getLevel().addToWave(wave, en);

		return en;
	}


}
