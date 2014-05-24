package net.sector.entities.shots;


import static org.lwjgl.opengl.GL11.*;
import net.sector.Constants;
import net.sector.collision.ColliderSphere;
import net.sector.entities.Entity;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class EntityPlasma extends EntityShotBase {

	protected RGB color = RGB.WHITE;

	private static final double SPEED = 0.3;

	protected static int renderlist = -1;

	private double scale = 0.25;

	static {
		renderlist = glGenLists(1);

		glNewList(renderlist, GL_COMPILE);

		glPushAttrib(GL_ENABLE_BIT);

		glDisable(GL_LIGHTING);
		glDisable(GL_CULL_FACE);
		glDisable(GL_COLOR_MATERIAL);

		glEnable(GL_TEXTURE_2D);
		//glDisable(GL_FOG);
		glDepthMask(false);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		TextureManager.bind("particles_blend");

		Coord texCoord = new Coord(1, 0);

		double left = (texCoord.x) * 0.125;
		double top = (texCoord.y) * 0.125;
		double right = (texCoord.x + 1) * 0.125;
		double bottom = (texCoord.y + 1) * 0.125;

		glBegin(GL_QUADS);
		double sh = 1;
		glTexCoord2d(left, top);
		glVertex3d(-sh, +sh, 0);
		glTexCoord2d(right, top);
		glVertex3d(+sh, +sh, 0);
		glTexCoord2d(right, bottom);
		glVertex3d(+sh, -sh, 0);
		glTexCoord2d(left, bottom);
		glVertex3d(-sh, -sh, 0);
		glEnd();

		TextureManager.unbind();

		glDepthMask(true);

		glPopAttrib();

		glEndList();

	}

	public EntityPlasma(Coord pos, Vec speed, Entity origin, double level) {
		super(pos, speed, origin, SPEED);

		this.shotDamage = 3 * level;
		this.collidePriority = 1003;
		this.mass = 0.002;
		this.scoreValue = 0;
		this.health = 0.15 * level * level;
		this.lifetime = Constants.FPS_UPDATE * 7;
		this.scale = Calc.clampd(0.05 * level, 0.1, 0.3);
		this.collider = new ColliderSphere(pos, scale);
	}

	/**
	 * Scale set
	 * 
	 * @param scale0.1-0.3
	 * @return this
	 */
	public EntityPlasma setScale(double scale) {
		this.scale = Calc.clampd(scale, 0.1, 0.3);
		return this;
	}

	private static long lastSoundTime = 0;

	@Override
	public void onAddedToScene() {
		if (System.currentTimeMillis() - lastSoundTime > 50) {
			Sounds.shot_plasma1.playEffect(1f, 0.04f, false, getPos().setY(Constants.LISTENER_POS.y - 3));
			lastSoundTime = System.currentTimeMillis();
		}
	}

	public EntityPlasma setColor(RGB clr) {
		color = clr;
		return this;
	}


	@Override
	public void onUpdate() {
		motion.norm_ip(shotSpeed);
		//getPos().add_ip(motion);


	}

	@Override
	public void render(double delta) {
		glPushMatrix();
		glLoadIdentity();
		Coord p = getPos().getDelta(delta);
		glTranslated(p.x, p.y, -p.z);
		glScaled(scale, scale, scale);

		RenderUtils.setColor(color);
		glCallList(renderlist);
		glPopMatrix();
	}

	@Override
	public double getHealthMax() {
		return getHealth();
	}
}
