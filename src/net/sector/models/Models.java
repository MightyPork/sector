package net.sector.models;


import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sector.Constants;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.textures.TextureManager;
import net.sector.util.Log;

import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Buffers;


public class Models {

	private static Random rand = new Random();

	//@formatter:off
	public static List[] rockTypes = {
		new ArrayList<PhysModel>(),
		new ArrayList<PhysModel>(),
		new ArrayList<PhysModel>(),
		new ArrayList<PhysModel>(),
		new ArrayList<PhysModel>(),
		new ArrayList<PhysModel>()
	};
	//@formatter:on

	public static PhysModel enemyFighter;
	public static PhysModel enemyFalcon;
	public static PhysModel enemyShark;
	public static PhysModel spaceMine;
	public static PhysModel enemyBird;
	public static PhysModel enemyBurger;
	public static PhysModel[] enemyCube;

	public static RenderModel orbShield;
	public static RenderModel orbArtifact;

	public static PhysModel rocketThin, rocketFat;

	public static RenderModel piece_bb_arrow;
	public static RenderModel piece_bb_cube;
	public static RenderModel piece_bb_point;
	public static RenderModel piece_bb_triangle;

	public static RenderModel piece_bs_corner1_side1;
	public static RenderModel piece_bs_corner1_side1_m;
	public static RenderModel piece_bs_corner1_side2;
	public static RenderModel piece_bs_corner1;
	public static RenderModel piece_bs_corner2_side1;
	public static RenderModel piece_bs_corner2_next;
	public static RenderModel piece_bs_corner2_opp;
	public static RenderModel piece_bs_corner3;
	public static RenderModel piece_bs_corner4;
	public static RenderModel piece_bs_point;
	public static RenderModel piece_bs_side1;
	public static RenderModel piece_bs_side2_next;
	public static RenderModel piece_bs_side2_opp;
	public static RenderModel piece_bs_side3;
	public static RenderModel piece_bs_triangle;

	public static RenderModel piece_bw_arrow;
	public static RenderModel piece_bw_cube;
	public static RenderModel piece_bw_triangle;

	public static RenderModel piece_engine;
	public static RenderModel piece_engine_big;

	public static RenderModel piece_w_cannon;
	public static RenderModel piece_w_emp;
	public static RenderModel piece_w_laser;
	public static RenderModel piece_w_rocket;
	public static RenderModel piece_w_rocketG;
	public static RenderModel piece_w_plasma;
	public static RenderModel piece_w_flame;

	public static ModelEnemyShip enemyBurgerKing;



	@SuppressWarnings("unchecked")
	public static void load() {

		Log.f2("Loading asteroids...");

		// asteroid loading with various models and textures.
		String prefix = "res/models/asteroids/";
		String[] models = { "asteroid02.obj", "asteroid03.obj", "asteroid04.obj", "asteroid05.obj", "asteroid06.obj", "shard01.obj", "shard02.obj" };
		String[] textures = { "limestone.jpg", "black.jpg", "moon.jpg", "moon_big.jpg", "red.jpg", "brown.jpg" };

		// all asteroid materials are by default with limestone.jpg texture.
		for (String model : models) {
			((List<PhysModel>) rockTypes[0]).add(new ModelAsteroid(prefix + model, 0.98, 1.1, 5, 30, 30));
		}
		for (int i = 1; i < textures.length; i++) {
			String tx = textures[i];
			for (int j = 0; j < rockTypes[0].size(); j++) {
				((List<PhysModel>) rockTypes[i]).add(new ModelAsteroid((ModelAsteroid) rockTypes[0].get(j), tx));
			}
		}


		Log.f2("Loading enemies and rockets...");

		rocketFat = new ModelRocket("res/models/shots/rocket_fat.obj", 0.35, 0.5, 4, 10, 0);
		rocketThin = new ModelRocket("res/models/shots/rocket_thin.obj", 0.8, 0.9, 4, 10, 0);

		enemyFighter = new ModelEnemyShip("res/models/ships/fighter/starship1.obj", 3.47, 1.1, 3, 16, 100);
		enemyFalcon = new ModelEnemyShip("res/models/ships/falcon/starship2.obj", 0.7, 1.2, 10, 80, 500);
		enemyShark = new ModelEnemyShip("res/models/ships/shark/shark.obj", 1.5, 1.6, 3, 200, 750);
		enemyBird = new ModelEnemyShip("res/models/ships/bird/bird.obj", 0.4, 1.1, 3, 10, 100);
		enemyBurger = new ModelEnemyShip("res/models/ships/burger/burger.obj", 0.6, 0.7, 3, 20, 200);
		enemyBurgerKing = new ModelEnemyShip(enemyBurger.model, 3, 3, 10, 220, 800);

		spaceMine = new ModelEnemyShip("res/models/ships/mine/mine.obj", 0.2, 0.6, 100, 10, 80);

		enemyCube = new PhysModel[5];
		enemyCube[0] = new ModelEnemyShip("res/models/ships/cube/cube.obj", 0.6, 0.7, 3, 20, 60);
		enemyCube[1] = new ModelEnemyShip(enemyCube[0], "cube_red.png");
		enemyCube[2] = new ModelEnemyShip(enemyCube[0], "cube_blue.png");
		enemyCube[3] = new ModelEnemyShip(enemyCube[0], "cube_purple.png");
		enemyCube[4] = new ModelEnemyShip(enemyCube[0], "cube_yellow.png");

		Log.f2("Loading ship body pieces...");

		String body = "res/models/playership/body/";
		String misc = "res/models/playership/misc/";
		String weapons = "res/models/playership/weapons/";
		String powerup = "res/models/powerup/";

		piece_bb_arrow = new RenderModel(body + "bb_arrow.obj");
		piece_bb_cube = new RenderModel(body + "bb_cube.obj");
		piece_bb_point = new RenderModel(body + "bb_point.obj");
		piece_bb_triangle = new RenderModel(body + "bb_triangle.obj");

		piece_bs_corner1_side1 = new RenderModel(body + "bs_corner1_side1.obj");
		piece_bs_corner1_side1_m = new RenderModel(body + "bs_corner1_side1_m.obj");
		piece_bs_corner1_side2 = new RenderModel(body + "bs_corner1_side2.obj");
		piece_bs_corner1 = new RenderModel(body + "bs_corner1.obj");
		piece_bs_corner2_side1 = new RenderModel(body + "bs_corner2_side1.obj");
		piece_bs_corner2_next = new RenderModel(body + "bs_corner2_next.obj");
		piece_bs_corner2_opp = new RenderModel(body + "bs_corner2_opp.obj");
		piece_bs_corner3 = new RenderModel(body + "bs_corner3.obj");
		piece_bs_corner4 = new RenderModel(body + "bs_corner4.obj");
		piece_bs_point = new RenderModel(body + "bs_point.obj");
		piece_bs_side1 = new RenderModel(body + "bs_side1.obj");
		piece_bs_side2_next = new RenderModel(body + "bs_side2_next.obj");
		piece_bs_side2_opp = new RenderModel(body + "bs_side2_opp.obj");
		piece_bs_side3 = new RenderModel(body + "bs_side3.obj");
		piece_bs_triangle = new RenderModel(body + "bs_triangle.obj");

		piece_bw_arrow = new RenderModel(body + "bw_arrow.obj");
		piece_bw_cube = new RenderModel(body + "bw_cube.obj");
		piece_bw_triangle = new RenderModel(body + "bw_triangle.obj");

		piece_engine = new RenderModel(misc + "engine.obj");
		piece_engine_big = new RenderModel(misc + "engine_big.obj");

		piece_w_cannon = new RenderModel(weapons + "w_cannon.obj");
		piece_w_emp = new RenderModel(weapons + "w_emp.obj");
		piece_w_laser = new RenderModel(weapons + "w_laser.obj");
		piece_w_rocket = new RenderModel(weapons + "w_rocket.obj");
		piece_w_plasma = new RenderModel(weapons + "w_plasma.obj");
		piece_w_flame = new RenderModel(weapons + "w_flame.obj");
		piece_w_rocketG = new RenderModel(weapons + "w_rocket2.obj");

		Log.f2("Loading power-up models...");

		orbShield = new RenderModel(powerup + "sphere.obj", "blue-silver.png");
		orbArtifact = new RenderModel(orbShield, "red-gold.png");
	}

	public static PhysModel pickAsteroidOfType(int type) {
		type = Calc.clampi(type, 0, rockTypes.length);
		return (PhysModel) Calc.pick(rockTypes[type]);
	}

	private static int beginList = -1;
	private static int endList = -1;


	public static void renderBegin() {
		if (beginList == -1) {
			beginList = glGenLists(1);
			glNewList(beginList, GL_COMPILE);
			glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

			glEnable(GL_TEXTURE_2D);
			glEnable(GL_COLOR_MATERIAL);
			glEnable(GL_CULL_FACE);

			float spec = Constants.SCENE_MAT_SPECULAR;
			float amb = Constants.SCENE_MAT_AMBIENT;
			float diff = Constants.SCENE_MAT_DIFFUSE;

			FloatBuffer buff = Calc.Buffers.mkBuff(4);

			Calc.Buffers.fillBuff(buff, amb, amb, amb, 1.0f);
			glMaterial(GL_FRONT, GL_AMBIENT, Buffers.fBuff(amb, amb, amb, 1f));

			buff.clear();
			Calc.Buffers.fillBuff(buff, spec, spec, spec, 1.0f);
			glLight(GL_LIGHT0, GL_SPECULAR, buff);
			glMaterial(GL_FRONT, GL_SPECULAR, Buffers.fBuff(spec, spec, spec, 1f));

			buff.clear();
			Calc.Buffers.fillBuff(buff, diff, diff, diff, 1.0f);
			glLight(GL_LIGHT0, GL_DIFFUSE, buff);
			glMaterial(GL_FRONT, GL_DIFFUSE, Buffers.fBuff(diff, diff, diff, 1f));
			glMaterialf(GL_FRONT, GL_SHININESS, 8);

			glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);

			glColor4d(1, 1, 1, 1);
			glEndList();
		}
		glCallList(beginList);
	}

	public static void renderEnd() {
		if (endList == -1) {
			endList = glGenLists(1);
			glNewList(endList, GL_COMPILE);
			glDisable(GL_COLOR_MATERIAL);
			glDisable(GL_CULL_FACE);
			TextureManager.unbind();
			glDisable(GL_TEXTURE_2D);
			glEndList();
		}
		glCallList(endList);
	}

}
