package net.sector.entities.shots;


import static org.lwjgl.opengl.GL11.*;
import net.sector.Constants;
import net.sector.collision.ColliderSphere;
import net.sector.entities.Entity;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc.Rad;
import com.porcupine.math.Polar;


public class EntityShell extends EntityShotBase {

	private static final double SPEED = 0.5;

	private static int renderlist = -1;
	private double scale = 0.1;
	private RGB color = new RGB(0.8, 0.8, 0.8);

	static {
		renderlist = glGenLists(1);

		glNewList(renderlist, GL_COMPILE);

		glPushAttrib(GL_ENABLE_BIT);

		glEnable(GL_LIGHTING);
		glDisable(GL_CULL_FACE);
		glDisable(GL_COLOR_MATERIAL);

		glEnable(GL_TEXTURE_2D);
		//glDisable(GL_FOG);
		glDepthMask(false);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		TextureManager.bind("particles_plain");

		Coord texCoord = new Coord(3, 1);

		double left = (texCoord.x) * 0.125;
		double top = (texCoord.y) * 0.125;
		double right = (texCoord.x + 1) * 0.125;
		double bottom = (texCoord.y + 1) * 0.125;

		glBegin(GL_QUADS);
		double sh = 0.03;
		double len = 0.2;

		glTexCoord2d(left, top);
		glVertex3d(-sh, 0, -len);
		glTexCoord2d(right, top);
		glVertex3d(+sh, 0, -len);
		glTexCoord2d(right, bottom);
		glVertex3d(+sh, 0, 0);
		glTexCoord2d(left, bottom);
		glVertex3d(-sh, 0, 0);

		glTexCoord2d(left, top);
		glVertex3d(0, -sh, -len + 0.01);
		glTexCoord2d(right, top);
		glVertex3d(0, +sh, -len + 0.01);
		glTexCoord2d(right, bottom);
		glVertex3d(0, +sh, 0 - 0.01);
		glTexCoord2d(left, bottom);
		glVertex3d(0, -sh, 0 - 0.01);
		glEnd();
		TextureManager.unbind();

		glDepthMask(true);
		glPopAttrib();

		glEndList();

	}

	public EntityShell(Coord pos, Vec speed, Entity origin, int level) {
		super(pos, speed, origin, SPEED);
		this.shotDamage = 0.3 + 0.5 * level;
		this.collidePriority = 1000;
		this.mass = 0.01;
		this.health = 0.2 * level * level;
		this.lifetime = Constants.FPS_UPDATE * 4;
		this.collider = new ColliderSphere(pos, scale);
	}

	private static long lastSoundTime = 0;

	@Override
	public void onAddedToScene() {
		if (System.currentTimeMillis() - lastSoundTime > 50) {
			Sounds.shot_gun2.playEffect(0.4f, 0.07f, false, getPos().setY(Constants.LISTENER_POS.y - 3));
			lastSoundTime = System.currentTimeMillis();
		}
	}


	@Override
	public void onUpdate() {
		motion.norm_ip(shotSpeed);

		// rotate to match motion
		Polar p = Polar.fromCoord(motion.x, motion.z);
		rotAngle.set(-90 + Rad.toDeg(p.angle));
	}

	@Override
	public void render(double delta) {
		glPushMatrix();
		glLoadIdentity();
		Coord p = getPos().getDelta(delta);
		glTranslated(p.x, p.y, -p.z);
		glRotated(rotAngle.delta(delta), rotDir.x, rotDir.y, rotDir.z);
		glColor4d(color.r, color.g, color.b, 1);

		glCallList(renderlist);
		glPopMatrix();
	}
}
