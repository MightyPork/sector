package net.sector.effects;


import java.util.Random;

import net.sector.Constants;
import net.sector.effects.particles.*;
import net.sector.sounds.Sounds;
import net.sector.util.Utils;

import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


/**
 * Effects helper
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Effects {
	/** RNG */
	private static Random rand = new Random();

	/**
	 * Add fire from jet engine (rocket)
	 * 
	 * @param manager particle manager
	 * @param center engine pos
	 * @param motion engine motion
	 * @param repeatCount repeat count
	 */
	public static void addEngineFire(ParticleManager manager, Coord center, Vec motion, int repeatCount) {
		addEngineFire(manager, center, motion, repeatCount, 0);
	}

	/**
	 * Add fire from jet engine (rocket)
	 * 
	 * @param manager particle manager
	 * @param center engine pos
	 * @param motion engine motion
	 * @param repeatCount repeat count
	 * @param type 0 red, 1 blue
	 */
	public static void addEngineFire(ParticleManager manager, Coord center, Vec motion, int repeatCount, int type) {
		addEngineFire(manager, center, motion, repeatCount, type, 1);
	}

	/**
	 * Add fire from jet engine (rocket)
	 * 
	 * @param manager particle manager
	 * @param center engine pos
	 * @param motion engine motion
	 * @param repeatCount repeat count
	 * @param type 0 red, 1 blue
	 * @param size particle max size
	 */
	public static void addEngineFire(ParticleManager manager, Coord center, Vec motion, int repeatCount, int type, double size) {
		Coord pos;
		Vec pmotion;
		double rotSpeed;

		for (int i = 0; i < repeatCount; i++) {
			pos = center.random_offset(0.02);
			pmotion = motion.copy();
			rotSpeed = 0;
			ParticleFire p;
			manager.add(p = (ParticleFire) new ParticleFire(pos, pmotion, rotSpeed, (0.2 + rand.nextDouble() * 0.2) * size, false)
					.setGlobalMovement(false));
			p.maxAge = (int) (Constants.FPS_UPDATE * 0.3 * size);
			p.setType(type);
		}

	}

	/**
	 * Add orb effect
	 * 
	 * @param manager particle manager
	 * @param center center
	 * @param motionSource velocity of explosion source
	 * @param radius burst radius (3D units)
	 * @param particles number of particles
	 * @param color particle average color (0 blue, 1 red, 2 green)
	 * @param globalMovement should the particles move with "asteroid shift"?
	 */
	public static void addOrbBurst(ParticleManager manager, Coord center, Vec motionSource, double radius, int particles, int color,
			boolean globalMovement, boolean gaussian) {

		if (!Utils.canCoordBeSeen(center, 20)) return;

		Coord pos;
		Vec motion;

		for (int i = 0; i < particles; i++) {

			pos = center.random_offset(gaussian ? Math.abs(Calc.clampd(rand.nextGaussian() * radius, 0, radius * 1.2)) : radius);
			motion = (Vec) motionSource.mul(0.5).add(Vec.random(-0.005, 0.005));

			manager.add(new ParticleOrb(pos, motion, 0.2 + (0.2 + rand.nextDouble()) * 0.3, color).setGlobalMovement(globalMovement));
		}
	}

	/**
	 * Add fire burst
	 * 
	 * @param manager particle manager
	 * @param center center
	 * @param motionSource velocity of explosion source
	 * @param radius burst radius (3D units)
	 * @param particles number of particles
	 * @param globalMovement should the particles move with "asteroid shift"?
	 */
	public static void addFireBurst(ParticleManager manager, Coord center, Vec motionSource, double radius, int particles, boolean globalMovement,
			boolean gaussian) {

		if (!Utils.canCoordBeSeen(center, 20)) return;

		Coord pos;
		Vec motion;

		for (int i = 0; i < particles; i++) {

			pos = center.random_offset(gaussian ? Math.abs(Calc.clampd(rand.nextGaussian() * radius, 0, radius * 1.2)) : radius);
			motion = (Vec) motionSource.mul(0.8).add(Vec.random(-0.005, 0.005));
			double rotSpeed = -10 + rand.nextDouble() * 20;

			manager.add(new ParticleFire(pos, motion, rotSpeed, true).setGlobalMovement(globalMovement));

		}
	}

	/**
	 * Add explosion
	 * 
	 * @param manager particle manager
	 * @param center center
	 * @param motionSource velocity of explosion source
	 * @param strength explosion strength
	 * @param globalMovement should the particles move with "asteroid shift"?
	 * @param sound make sound
	 */
	public static void addEMPExplosion(ParticleManager manager, Coord center, Vec motionSource, double strength, boolean globalMovement, boolean sound) {

		if (!Utils.canCoordBeSeen(center, 20)) return;

		Coord pos;
		Vec motion;

		for (int i = 0; i < strength * 5; i++) {
			pos = center.random_offset(0.02 * strength);
			motion = (Vec) motionSource.mul(0.5).add(Vec.random(-0.005, 0.005));

			manager.add(new ParticleEMP(pos, motion, 0.3 + (0.2 + rand.nextDouble()) * 0.3 * strength).setGlobalMovement(globalMovement));
		}

		if (sound && center.distTo(new Coord(0, 0, 0)) < 80 /*25*/) {
			float pitch = 0.3f + rand.nextFloat() * 0.6f;
			float gain = Calc.clampf((float) strength * 0.12f, 0, 1.5f) * 0.3f;
			Sounds.explosion().playEffectLinearZ(pitch, gain, 60, false, center);
			Sounds.shot_emp_hit.playEffectLinearZ(pitch, gain, 60, false, center);
		}
	}

	/**
	 * Add explosion
	 * 
	 * @param manager particle manager
	 * @param center center
	 * @param motionSource velocity of explosion source
	 * @param strength explosion strength
	 * @param shards add shards
	 * @param globalMovement should the particles move with "asteroid shift"?
	 */
	public static void addExplosion(ParticleManager manager, Coord center, Vec motionSource, double strength, boolean shards, boolean globalMovement) {
		addExplosion(manager, center, motionSource, strength, shards, globalMovement, true);
	}

	/**
	 * Add explosion
	 * 
	 * @param manager particle manager
	 * @param center center
	 * @param motionSource velocity of explosion source
	 * @param strength explosion strength
	 * @param shards add shards
	 * @param globalMovement should the particles move with "asteroid shift"?
	 * @param sound enable sounds
	 */
	public static void addExplosion(ParticleManager manager, Coord center, Vec motionSource, double strength, boolean shards, boolean globalMovement,
			boolean sound) {

		if (!Utils.canCoordBeSeen(center, 20)) return;

		Coord pos;
		Vec motion;
		double rotSpeed;

		for (int i = 0; i < strength * 5; i++) {
			pos = center.random_offset(0.01 * strength);
			motion = (Vec) motionSource.mul(0.5).add(Vec.random(-0.005, 0.005));
			rotSpeed = -10 + rand.nextDouble() * 20;

			manager.add(new ParticleFire(pos, motion, rotSpeed, 0.6 + (0.7 + rand.nextDouble()) * 0.6 * strength).setGlobalMovement(globalMovement));

			if (i < strength * 3 && shards) {
				pos = center.random_offset(0.04);

				motion = (Vec) motionSource.mul(0.5).add(Vec.random(-0.02, 0.02));
				rotSpeed = -10 + rand.nextDouble() * 20;
				manager.add(new ParticleSmoke(pos, motion, rotSpeed).setGlobalMovement(globalMovement));
			}

			if (i < strength * 2 && shards) {
				pos = center.random_offset(0.04);
				motion = (Vec) motionSource.mul(0.5).add(Vec.random(-0.02, 0.02));
				rotSpeed = -10 + rand.nextDouble() * 20;
				manager.add(new ParticleShard(pos, motion, rotSpeed).setGlobalMovement(globalMovement));
			}
		}

		if (sound && center.distTo(new Coord(0, 0, 0)) < 80 /*25*/) {
			float pitch = 0.6f + rand.nextFloat() * 1.7f;
			float gain = Calc.clampf((float) strength * 0.09f, 0, 1.5f) * 0.3f;
			Sounds.explosion().playEffectLinearZ(pitch, gain, 60, false, center);
		}
	}

//
//	/**
//	 * Add stars
//	 * 
//	 * @param manager particle manager
//	 * @param center stars center
//	 * @param count stars count
//	 */
//	public static void addStar(ParticleManager manager, Coord center, int count) {
//		Coord pos;
//		Vec motion;
//		double rotSpeed;
//
//		for (int i = 0; i < count; i++) {
//			pos = center.random_offset(0.3);
//			motion = Vec.random(-0.005, 0.005);
//			rotSpeed = -5 + rand.nextDouble() * 10;
//
//			manager.add(new ParticleStar(pos, motion, rotSpeed, 0.3 + (0.1 + rand.nextDouble()) * 0.8));
//		}
//	}

	/**
	 * Add binary particles for splash
	 * 
	 * @param manager particle manager
	 * @param center center
	 * @param count binaries count
	 */
	public static void addBinaries(ParticleManager manager, Coord center, int count) {
		Coord pos;
		Vec motion;
		double rotSpeed;

		for (int i = 0; i < count; i++) {
			pos = center.random_offset(0.3);
			motion = Vec.random(-0.005, 0.005);
			rotSpeed = -3 + rand.nextDouble() * 6;

			manager.add(new ParticleBinary(pos, motion, rotSpeed, 0.1 + (rand.nextDouble()) * 0.35));
		}
	}
}
