package net.sector.effects;


import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.sector.Constants;
import net.sector.effects.particles.Particle;
import net.sector.effects.renderers.*;
import net.sector.util.Utils;

import com.porcupine.coord.Vec;


/**
 * Particle manager, container and animator of particles.<br>
 * It extends HashSet, so you can simply use add(particle) to add new effect.
 * 
 * @author MightyPork
 */
public class ParticleManager extends ArrayList<Particle> {

	@Override
	public boolean add(Particle e) {
		if (size() > Constants.PARTICLE_COUNT_LIMIT) return false;
		return super.add(e);
	}

	//@formatter:off
	/** particle renderers */
	public static ParticleRenderer[] renderers = {
		new ParticleSmokeRenderer(), 
		new ParticleShardRenderer(),
		new ParticleFireRenderer(),
		new ParticleStarRenderer(),
		new ParticleBinaryRenderer(),
		new ParticleEMPRenderer(),
		new ParticleOrbRenderer()
	};
	//@formatter:on

	/**
	 * Update all particles in this manager
	 */
	public void update() {
		Iterator<Particle> i = this.iterator();
		while (i.hasNext()) {
			Particle p = i.next();
			p.update();
			if (p.isDead) {
				i.remove();
			}
		}
		Collections.sort(this);
	}

	/**
	 * Render all particles in this manager
	 * 
	 * @param delta delta time
	 */
	public void render(double delta) {
		glPushMatrix();
		glDisable(GL_LIGHTING);
		glDisable(GL_FOG);
		glDepthMask(false);

		glLoadIdentity();

		for (Particle p : this) {

			if (Utils.canSkipRendering(p.pos)) continue;

			EParticle pt = p.getType();


			for (ParticleRenderer pr : renderers) {

				if (pr.getType() == pt) {

					pr.prepareRender();
					pr.renderParticle(p, delta);
					pr.finishRender();

				} else {
					continue;
				}
			}
		}

		glDepthMask(true);
//		for (ParticleRenderer pr : renderers) {
//
//			pr.prepareRender();
//
//			ParticleType pt = pr.getType();
//
//			for (Particle p : this) {
//
//				if (p.getType() == pt) {
//					pr.renderParticle(p);
//				}
//			}
//
//			pr.finishRender();
//
//		}

		glEnable(GL_LIGHTING);
		glEnable(GL_FOG);

		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		//glEnable(GL_DEPTH_TEST);

		glPopMatrix();
	}

	/**
	 * move all particles (same as move all entities)
	 * 
	 * @param motion
	 */
	public void moveAllParticles(Vec motion) {
		for (Particle p : this) {
			if (p.hasGlobalMovement()) p.pos.add_ip(motion).update();
		}
	}

}
