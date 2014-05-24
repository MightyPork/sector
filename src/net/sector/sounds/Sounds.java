package net.sector.sounds;


import java.nio.FloatBuffer;
import java.util.Random;

import net.sector.Constants;
import net.sector.GameConfig;
import net.sector.util.Log;

import org.lwjgl.openal.AL10;
import org.newdawn.slick.openal.SoundStore;

import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


/**
 * Preloaded sounds.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
@SuppressWarnings("javadoc")
public class Sounds {

	private static final int EXPLODE_CNT = 5;


	public static AudioX shield_loop;
	public static AudioX shield_hit;

	public static AudioX timer_loop;
	public static AudioX timer_end;

	public static AudioX shot_laser;
	public static AudioX shot_laser_acid;

	public static AudioX shot_plasma1;
	public static AudioX shot_plasma2;

	public static AudioX shot_emp;
	public static AudioX shot_emp_hit;

	public static AudioX shot_gun1;
	public static AudioX shot_gun2;
	public static AudioX shot_gun3;
	public static AudioX shot_fireball;

	public static AudioX rocket;


	private static AudioX[] explode = new AudioX[EXPLODE_CNT];


	public static AudioX beep_popup;
	public static AudioX beep_slow1;
	public static AudioX beep_slow2;
	public static AudioX beep_soft;
	public static AudioX beep_soft_short;
	public static AudioX des_level_up;
	public static AudioX des_level_down;
	public static AudioX des_repair;
	public static AudioX click1;
	public static AudioX click2;
	public static AudioX shutter;


	public static AudioX appear;
	public static AudioX powerup1;
	public static AudioX powerup2;
	public static AudioX powerdown1;

	protected static AudioX musIntro;
	protected static AudioX musIngameLoop;
	protected static AudioX musMenuLoop;
	protected static AudioX musDesignerLoop;



	public static SoundStore soundManager = SoundStore.get();


	public static void loadForSplash() {
		if (GameConfig.enableSplash) {
			musIntro = loadSound("res/sounds/music/intro.ogg");
		} else {
			musIntro = loadSound("res/sounds/music/intro-nosplash.ogg");
		}

		shutter = loadSound("res/sounds/effect/shutter.ogg");
	}

	/**
	 * Load sounds
	 */
	public static void load() {
		musIngameLoop = loadSound("res/sounds/music/random-loop.ogg");
		musMenuLoop = loadSound("res/sounds/music/cosmic-journey.ogg");
		musDesignerLoop = loadSound("res/sounds/music/dust-loop.ogg");

		shot_fireball = loadSound("res/sounds/effect/shot-fireball2.ogg");

		shot_laser = loadSound("res/sounds/effect/shot-laser.ogg");
		shot_laser_acid = loadSound("res/sounds/effect/shot-laser-acid.ogg");

		shot_plasma1 = loadSound("res/sounds/effect/shot-plasma1.ogg");
		shot_plasma2 = loadSound("res/sounds/effect/shot-plasma2.ogg");

		shot_emp = loadSound("res/sounds/effect/shot-emp.ogg");
		shot_emp_hit = loadSound("res/sounds/effect/shot-emp-sparks.ogg");

		shot_gun1 = loadSound("res/sounds/effect/shot-gun1.ogg");
		shot_gun2 = loadSound("res/sounds/effect/shot-gun2.ogg");
		shot_gun3 = loadSound("res/sounds/effect/shot-gun3.ogg");

		explode[0] = loadSound("res/sounds/effect/explode1.ogg");
		explode[1] = loadSound("res/sounds/effect/explode2.ogg");
		explode[2] = loadSound("res/sounds/effect/explode3.ogg");
		explode[3] = loadSound("res/sounds/effect/explode4.ogg");
		explode[4] = loadSound("res/sounds/effect/explode5.ogg");

		rocket = loadSound("res/sounds/effect/shot-rocket.ogg");
		powerup2 = loadSound("res/sounds/effect/powerup2.ogg");
		powerup1 = loadSound("res/sounds/effect/powerup.ogg");
		powerdown1 = loadSound("res/sounds/effect/powerdown.ogg");
		appear = loadSound("res/sounds/effect/appear.ogg");


		timer_loop = loadSound("res/sounds/effect/timer-loop.ogg");
		timer_end = loadSound("res/sounds/effect/timer-end.ogg");

		beep_popup = loadSound("res/sounds/effect/beep-popup.ogg");
		beep_slow1 = loadSound("res/sounds/effect/beep-slow1.ogg");
		beep_slow2 = loadSound("res/sounds/effect/beep-slow2.ogg");

		beep_soft = loadSound("res/sounds/effect/beep-soft.ogg");
		beep_soft_short = loadSound("res/sounds/effect/beep-soft-short.ogg");

		des_level_up = loadSound("res/sounds/effect/beep-des-levelup.ogg");
		des_level_down = loadSound("res/sounds/effect/beep-des-leveldown.ogg");
		des_repair = loadSound("res/sounds/effect/beep-des-repair.ogg");

		click1 = loadSound("res/sounds/effect/click1.ogg");
		click2 = loadSound("res/sounds/effect/click2.ogg");

		shield_hit = loadSound("res/sounds/effect/shield-hit.ogg");
		shield_loop = loadSound("res/sounds/effect/shield-loop.ogg");

		Music.prepareLoops();
	}

	public static Coord listener = new Coord();
	private static Random rand = new Random();



	/**
	 * Get random explode sound
	 * 
	 * @return
	 */
	public static AudioX explosion() {
		return explode[rand.nextInt(EXPLODE_CNT)];
	}


	/**
	 * Set listener pos
	 * 
	 * @param pos
	 */
	public static void setListener(Coord pos) {
		listener.setTo(pos);
		FloatBuffer buf3 = Calc.Buffers.mkBuff(3);
		FloatBuffer buf6 = Calc.Buffers.mkBuff(6);
		buf3.clear();
		Calc.Buffers.fillBuff(buf3, (float) pos.x, (float) pos.y, (float) pos.z);
		AL10.alListener(AL10.AL_POSITION, buf3);

		buf3.clear();
		Calc.Buffers.fillBuff(buf3, 0, 0, 0);
		AL10.alListener(AL10.AL_VELOCITY, buf3);

		buf6.clear();
		Calc.Buffers.fillBuff(buf6, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
		AL10.alListener(AL10.AL_ORIENTATION, buf6);

		buf3 = buf6 = null;
	}

	/**
	 * load one sound
	 * 
	 * @param path file path
	 * @return the sound
	 */
	private static AudioX loadSound(String path) {
		try {
			String ext = path.substring(path.length() - 3).toLowerCase();
			AudioX audio = null;
			if (ext.equals("ogg")) {
				audio = new AudioX(soundManager.getOgg(path));
			}
			if (ext.equals("wav")) {
				audio = new AudioX(soundManager.getWAV(path));
			}
			if (ext.equals("aif")) {
				audio = new AudioX(soundManager.getAIF(path));
			}
			if (ext.equals("mod")) {
				audio = new AudioX(soundManager.getMOD(path));
			}
			if (Constants.LOG_SOUNDS) Log.f2("Sound " + path + " loaded.");
			return audio;
		} catch (Exception e) {
			Log.e("ERROR WHILE LOADING: " + path);
			throw new RuntimeException(e);
		}
	}
}
