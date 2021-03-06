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
import com.porcupine.math.Calc;
import com.porcupine.math.Calc.Rad;
import com.porcupine.math.Polar;


public class EntityLaser extends EntityShotBase {

	private static final double SPEED = 0.5;

	private static int renderlist = -1;
	private double scale = 0.1;
	private RGB color = new RGB(0, 1, 0);

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

		Coord texCoord = new Coord(3, 1);

		double left = (texCoord.x) * 0.125;
		double top = (texCoord.y) * 0.125;
		double right = (texCoord.x + 1) * 0.125;
		double bottom = (texCoord.y + 1) * 0.125;

		glBegin(GL_QUADS);
		double sh = 0.05;

		glTexCoord2d(left, top);
		glVertex3d(-sh, 0, -1);
		glTexCoord2d(right, top);
		glVertex3d(+sh, 0, -1);
		glTexCoord2d(right, bottom);
		glVertex3d(+sh, 0, 0);
		glTexCoord2d(left, bottom);
		glVertex3d(-sh, 0, 0);

		glTexCoord2d(left, top);
		glVertex3d(0, -sh, -0.9);
		glTexCoord2d(right, top);
		glVertex3d(0, +sh, -0.9);
		glTexCoord2d(right, bottom);
		glVertex3d(0, +sh, -0.1);
		glTexCoord2d(left, bottom);
		glVertex3d(0, -sh, -0.1);
		glEnd();

		TextureManager.unbind();

		glDepthMask(true);
		glPopAttrib();

		glEndList();

	}

	public EntityLaser(Coord pos, Vec speed, Entity origin) {
		super(pos, speed, origin, SPEED);
		this.shotDamage = 0.8;
		this.collidePriority = 1000;
		this.mass = 0.01;
		this.lifetime = Constants.FPS_UPDATE * 6;
		this.collider = new ColliderSphere(pos, scale);
		this.health = 0.005;
	}

	public EntityLaser(Coord pos, Vec speed, Entity origin, RGB color) {
		this(pos, speed, origin);
		this.color.setTo(color);
	}

	public EntityLaser(Coord pos, Vec speed, Entity origin, RGB color, int level) {
		this(pos, speed, origin, color);
		this.shotDamage = 0.8 * level;
		this.shotDamage = Calc.clampd(this.shotDamage, 1.3);
		this.health = 0.1 * level * level;
	}

	private static long lastSoundTime = 0;

	@Override
	public void onAddedToScene() {
		if (System.currentTimeMillis() - lastSoundTime > 50) {
			Sounds.shot_laser.playEffect(1f, 0.4f, false, getPos().setY(Constants.LISTENER_POS.y - 3));
			lastSoundTime = System.currentTimeMillis();
		}
	}


	public Entity setDamage(double dmg) {
		this.shotDamage = dmg;
		return this;
	}


	@Override
	public void onUpdate() {
		//collider.pos.add_ip(motion);

		motion.norm_ip(shotSpeed);

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
