package edu.cg.models.Car;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;


public class FrontBumber implements IRenderable {
	// TODO: Add fields as you like (and methods if you think they are necessary).
	private SkewedBox b_box = new SkewedBox(Specification.F_BUMPER_LENGTH,Specification.F_BUMPER_HEIGHT_1,Specification.F_BUMPER_HEIGHT_2,Specification.F_BUMPER_DEPTH,Specification.F_BUMPER_DEPTH);
	private SkewedBox b_wingbox = new SkewedBox(Specification.F_BUMPER_LENGTH,Specification.F_BUMPER_WINGS_HEIGHT_1,Specification.F_BUMPER_HEIGHT_2,Specification.F_BUMPER_WINGS_DEPTH,Specification.F_BUMPER_WINGS_DEPTH);


	@Override
	public void render(GL2 gl) {
		// TODO: Render the front bumper relative to it's local coordinate system.
		// Remember the dimensions of the bumper, this is important when you
		// combine the bumper with the hood.
		gl.glPushMatrix();
		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();

		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(Specification.F_HOOD_LENGTH_2+Specification.F_BUMPER_LENGTH/2, 0, 0);
		b_box.render(gl);
		// First wingbox
		Materials.SetDarkRedMetalMaterial(gl);
		gl.glTranslated(0, 0, -Specification.F_BUMPER_DEPTH/2-Specification.F_BUMPER_WINGS_DEPTH/2);
		b_wingbox.render(gl);

		// First Light
		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(0.0, 0.2 * Specification.F_BUMPER_LENGTH , 0.0);
		glu.gluSphere(q, 0.35*Specification.F_BUMPER_WINGS_DEPTH, 	36, 18);

		// Second wingbox
		Materials.SetDarkRedMetalMaterial(gl);
		gl.glTranslated(0, -0.03, Specification.F_BUMPER_DEPTH+Specification.F_BUMPER_WINGS_DEPTH);
		b_wingbox.render(gl);

//		// Second Light
		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(0.0, 0.2 * Specification.F_BUMPER_LENGTH , 0.0);
		Materials.SetRedMetalMaterial(gl);
		glu.gluSphere(q, 0.35*Specification.F_BUMPER_WINGS_DEPTH, 	36, 18);

		// Go back to the center of hood 2
		gl.glPopMatrix();
	}
	public void destroy(GL2 gl) {};

	@Override
	public void init(GL2 gl) {
	}

	@Override
	public String toString() {
		return "FrontBumper";
	}

}
