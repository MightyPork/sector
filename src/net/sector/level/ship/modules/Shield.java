package net.sector.level.ship.modules;


import static org.lwjgl.opengl.GL11.*;
import net.sector.Constants;
import net.sector.level.ship.DiscoveryRegistry;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;
import net.sector.util.DeltaDouble;
import net.sector.util.DeltaDoubleDeg;

import org.lwjgl.util.glu.Sphere;

import com.porcupine.color.RGB;
import com.porcupine.math.Calc;


/**
 * Energy shield renderer
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Shield {

	/** Shield energy storage */
	public double shieldEnergy = 0;
	/** Shield energy storage max */
	public double shieldEnergyMax = 0;

	/**
	 * Get relative energy storage contents
	 * 
	 * @return 0-1
	 */
	public double getLoadRatio() {
		return shieldEnergy / shieldEnergyMax;
	}

	/**
	 * Add points to the shield.
	 * 
	 * @param shieldPoints
	 * @return something added
	 */
	public boolean addShieldPoints(double shieldPoints) {
		if (shieldEnergy == shieldEnergyMax) return false;
		shieldEnergy = Calc.clampd(shieldPoints + shieldEnergy, 0, shieldEnergyMax);
		return true;
	}

	/**
	 * Get base cost (level 1)
	 * 
	 * @return base cost
	 */
	public static int getBaseCost() {
		return 500;
	}

	/**
	 * Get max level
	 * 
	 * @return level
	 */
	public static int getLevelMax() {
		return DiscoveryRegistry.getDiscoveryLevelMax("shield");
	}

	/** Render alpha */
	public DeltaDouble alpha = new DeltaDouble(0);
	private DeltaDouble sinx = new DeltaDouble(0);
	/** Is shield active */
	public boolean forceFieldActive = false;
	public boolean forceFieldActive_last = false;

	/** Shield level */
	public int level = 0;

	private ShipBody body = null;

	private int list = -1;

	private double shieldRotSpeed;

	private DeltaDoubleDeg rotation = new DeltaDoubleDeg(0);

	private RGB color;

	/**
	 * New shield renderer
	 * 
	 * @param body the body
	 */
	public Shield(ShipBody body) {
		this.body = body;
	}

	public void onReady() {
		double r = body.collider.radius + 1;
		shieldRotSpeed = 1D / r;
		buildRenderList();
	}

	private void buildRenderList() {
		list = glGenLists(1);
		glNewList(list, GL_COMPILE);

		// LIST BEGIN	
		glPushAttrib(GL_ENABLE_BIT);
		glDepthMask(false);

		TextureManager.bind("shield");

		Sphere sp = new Sphere();
		sp.setTextureFlag(true);
		sp.draw((float) body.collider.radius, 25, 25);

		glDepthMask(true);
		glPopAttrib();
		// LIST END
		glEndList();
	}

	/**
	 * Rotate by angle
	 * 
	 * @param add added degrees
	 */
	public void rotate(double add) {
		rotation.pushLast();
		rotation.add(add);
	}


	/**
	 * Set shield color
	 * 
	 * @param color shield color
	 */
	public void setColor(RGB color) {
		this.color = color;
	}

	/**
	 * Set shield level
	 * 
	 * @param level level to set
	 */
	public void setLevel(int level) {
		this.level = Calc.clampi(level, 0, getLevelMax());
		shieldEnergy = shieldEnergyMax = this.level * 2000;
	}

	/**
	 * Update shield rotation
	 */
	public void update() {

		if (level == 0) return;

		rotate(shieldRotSpeed * Constants.SPEED_MUL);
		alpha.pushLast();
		sinx.pushLast();
		sinx.d += 0.15 * Constants.SPEED_MUL;
		if (forceFieldActive && alpha.d < 1) alpha.d += 0.1 * Constants.SPEED_MUL;
		if (!forceFieldActive && alpha.d > 0) alpha.d -= 0.05 * Constants.SPEED_MUL;
		alpha.d = Calc.clampd(alpha.d, 0, 1);

		// animate

		if (body.isShieldRequestedByPlayer()) {
			forceFieldActive = true;

			// if cant consume energy, fade away
			double consume = 2 * body.collider.radius * Constants.SPEED_MUL;
			if (forceFieldActive) shieldEnergy -= consume;

			if (shieldEnergy <= 0) {
				shieldEnergy = 0;
				forceFieldActive = false;

			}

		} else {
			forceFieldActive = false;
		}


		if (forceFieldActive_last && !forceFieldActive) {
			Sounds.shield_loop.stop();
		}

		if (!forceFieldActive_last && forceFieldActive) {
			Sounds.shield_loop.playAsEffectLoop(1f, 0.3f);
		}

		forceFieldActive_last = forceFieldActive;
	}


	/**
	 * Render shield
	 * 
	 * @param delta delta time (for rotation)
	 */
	public void render(double delta) {
		if (level == 0) return;

		double alph = alpha.delta(delta);
		// dont render if it isnt actibve in any way
		if (!forceFieldActive && alph <= 0.005 && !body.isShieldRequestedByPlayer()) return;
		//if (alph <= 0.005) alph = 0.25;

		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);

		glColor4d(color.r, color.g, color.b, color.a * alph * (0.8 - Math.sin(sinx.delta(delta)) * 0.3));

		glDepthMask(false);
		glEnable(GL_BLEND);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_LIGHTING);

		glScaled(1, 0.6, 1);

		glPushMatrix();
		glRotated(rotation.delta(delta), 0.6, 0.3, 0.7);
		glCallList(list);
		glPopMatrix();

		glPushMatrix();
		glRotated(rotation.delta(delta), 1, 0.5, -0.4);
		glCallList(list);
		glPopMatrix();

		glDisable(GL_BLEND);
		//glEnable(GL_TEXTURE_2D);

		glPopAttrib();
		glColor4d(1, 1, 1, 1);
		glPopMatrix();
	}


	public void fill() {
		shieldEnergy = shieldEnergyMax;
	}

}
