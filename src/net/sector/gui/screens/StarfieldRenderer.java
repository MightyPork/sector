package net.sector.gui.screens;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import java.util.Random;

import net.sector.util.Log;

import com.porcupine.color.RGB;


/**
 * Class which renders starfield behind scene but in front of a fog.
 * 
 * @author MightyPork
 */
public class StarfieldRenderer {

	private static final int STARS = 1200;

	private Random rand = new Random();
	private int starfieldDL = -1;

	public boolean hasInit = false;

	/**
	 * Generate starfield and precompile render list.
	 */
	public void init() {
		if (hasInit) return;
		hasInit = true;
		Log.f1("Initializing background starfield.");

		starfieldDL = glGenLists(1);
		glNewList(starfieldDL, GL_COMPILE);
		glPushMatrix();
		glPushAttrib(GL_ENABLE_BIT | GL_MULTISAMPLE_BIT);
		glDisable(GL_MULTISAMPLE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glEnable(GL_POINT_SMOOTH);
		glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
		glLoadIdentity();
		RGB color;
		glRotated(-10, 1, 0, 0);
		
		int diffSizes = 10+rand.nextInt(6);
		
		int[] inGroup = new int[diffSizes];
		for(int i=0; i<STARS/5; i++) {
			inGroup[rand.nextInt(inGroup.length)] +=1;
		}
		
		for(int i=0; i<diffSizes; i++) {
			glPointSize(rand.nextFloat() * 2);
			glBegin(GL_POINTS);
			for (int j = 0; j < inGroup[i]; j++) {				
				color = new RGB(0.7 + rand.nextDouble() * 0.3, 0.7 + rand.nextDouble() * 0.3, 0.7 + rand.nextDouble() * 0.3);
				
				glColor4d(color.r, color.g, color.b, 1.0);
				for(int k=0; k<5; k++) {
					glVertex3d(-5 + rand.nextDouble() * 10, rand.nextDouble() * 5, 0);
				}
			}
			glEnd();
		}

		glPopAttrib();
		glPopMatrix();
		glEndList();
	}

	/**
	 * Render the starfield.
	 */
	public void render() {
		if (starfieldDL == -1) throw new RuntimeException("Starfield not initialized, can't render.");
		glCallList(starfieldDL);
	}
}