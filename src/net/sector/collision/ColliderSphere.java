package net.sector.collision;


import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.Sphere;

import com.porcupine.color.RGB;
import com.porcupine.coord.Coord;
import com.porcupine.math.Calc;


/**
 * Simple spheric collider
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ColliderSphere extends Collider {

	/** sphere radius */
	public double radius = 1.0;

	/**
	 * Sphere collider
	 * 
	 * @param center central point
	 * @param radius sphere radius
	 */
	public ColliderSphere(Coord center, double radius) {
		pos.setTo(center);
		this.radius = radius;
	}

	@Override
	public boolean collidesWith(Collider other) {
		if (other instanceof ColliderSphere) {
			ColliderSphere otherSphere = (ColliderSphere) other;
			return pos.distTo(otherSphere.pos) < radius + otherSphere.radius;
		}

		throw new RuntimeException("Collision test not implemented for " + Calc.className(this) + " and " + Calc.className(other));
	}

	@Override
	public void render() {
		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT);
		glLoadIdentity();
		glTranslated(pos.x, pos.y, -pos.z);
		glColor4f(1.0f, 0.0f, 0.0f, 0.5f);

		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glDisable(GL_TEXTURE_2D);

		Sphere sp = new Sphere();
		sp.draw((float) this.radius, 6, 6);

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glPopAttrib();
		glPopMatrix();
	}

	/**
	 * Get coord
	 * 
	 * @return pos coord
	 */
	public Coord getPos() {
		return pos;
	}



}
