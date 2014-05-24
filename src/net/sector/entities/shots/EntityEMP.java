package net.sector.entities.shots;


import static org.lwjgl.opengl.GL11.*;

import java.util.Set;

import net.sector.Constants;
import net.sector.collision.ColliderSphere;
import net.sector.effects.Effects;
import net.sector.entities.EEntity;
import net.sector.entities.Entity;
import net.sector.sounds.Sounds;
import net.sector.textures.TextureManager;
import net.sector.util.RenderUtils;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.coord.Vec;
import com.porcupine.math.Calc;


public class EntityEMP extends EntityShotBase {

	protected RGB color = new RGB(0.75, 0.5, 1);

	private static final double SPEED = 0.3;

	protected static int renderlist = -1;

	private double scale = 0.25;

	private Entity target;

	private Vec origDirection = null;

	private int level;

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

		Coord texCoord = new Coord(0, 1);

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

	public EntityEMP(Coord pos, Vec speed, Entity origin, int level) {
		super(pos, speed, origin, SPEED);

		this.shotDamage = 0;
		this.collidePriority = 1004;
		this.mass = 0.002;
		this.scoreValue = 0;
		this.health = 0.17 * level * level;
		this.lifetime = Constants.FPS_UPDATE * 5;
		this.scale = Calc.clampd(0.06 * level, 0.1, 0.4);
		this.collider = new ColliderSphere(pos, scale);

		origDirection = speed.copy();

		this.level = level;
	}

	private static long lastSoundTime = 0;

	@Override
	public void onAddedToScene() {
		if (System.currentTimeMillis() - lastSoundTime > 50) {
			Sounds.shot_emp.playEffect(1f, 1.5f, false, getPos().setY(Constants.LISTENER_POS.y - 3));
			lastSoundTime = System.currentTimeMillis();
		}
	}

	public EntityEMP setColor(RGB clr) {
		color = clr;
		return this;
	}


	@Override
	public void onUpdate() {

		motion.add_ip(origDirection.norm(0.05));
		motion.norm_ip(shotSpeed);

		// magnetic
		Set<Entity> ents = scene.getEntitiesInRange(getPos(), 6);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == this) continue;
				if (e.getType() == EEntity.ENEMY) {
					double dist = e.getPos().distTo(getPos()) - e.getRadius();
					if (dist < 0) dist = 0.00001;
					double move = 0.4 / dist;
					move = Calc.clampd(move, 0, 0.2);
					motion.add_ip(((Vec) getPos().vecTo(e.getPos()).setY(0)).norm(move));
					motion.norm_ip(shotSpeed);
				}
			}
		}


		motion.norm_ip(shotSpeed);
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
	public void onDeath() {
		onHitTarget(null);
	}


	@Override
	public void onHitTarget(Entity target) {
		Effects.addEMPExplosion(scene.particles, getPos(), getMotion(), Calc.clampi(4 * level, 4, 12), hasGlobalMovement(), true);


		Set<Entity> ents = scene.getEntitiesInRange(getPos(), 3 + 2 * level);
		if (!ents.isEmpty()) {
			for (Entity e : ents) {
				if (e == this) continue;
				if (e.getType() == EEntity.ENEMY) {
					e.addEmp(level * 250);

					Effects.addEMPExplosion(scene.particles, e.getPos(), e.getMotion(), Calc.clampi(4 * level, 4, 12), e.hasGlobalMovement(), true);
				}
			}
		}
	}
}