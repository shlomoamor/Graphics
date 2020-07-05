package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2;
import edu.cg.algebra.Point;
import edu.cg.models.*;

public class Back implements IRenderable, IIntersectable {
	private SkewedBox baseBox = new SkewedBox(Specification.B_BASE_LENGTH, Specification.B_BASE_HEIGHT,
			Specification.B_BASE_HEIGHT, Specification.B_BASE_DEPTH, Specification.B_BASE_DEPTH);
	private SkewedBox backBox = new SkewedBox(Specification.B_LENGTH, Specification.B_HEIGHT_1,
			Specification.B_HEIGHT_2, Specification.B_DEPTH_1, Specification.B_DEPTH_2);
	private PairOfWheels wheels = new PairOfWheels();
	private Spolier spoiler = new Spolier();
	private ExhaustPipe exhaustPipe = new ExhaustPipe();
	// Trial and error for the best fit sphere
	private BoundingSphere bSphere  = new BoundingSphere(Specification.B_LENGTH * 0.6 + 0.2 * Specification.B_HEIGHT,
			new Point(0.0, 0.6 * Specification.B_HEIGHT,0));

	public void destroy(GL2 gl) {};
	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();
		Materials.SetBlackMetalMaterial(gl);
		gl.glTranslated(Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0, 0.0, 0.0);
		baseBox.render(gl);
		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(-1.0 * (Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0),
				Specification.B_BASE_HEIGHT, 0.0);
		backBox.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 + Specification.TIRE_RADIUS, 0.5 * Specification.TIRE_RADIUS,
				0.0);
		wheels.render(gl);
		exhaustPipe.render(gl);

		gl.glPopMatrix();
		gl.glPushMatrix();

		gl.glTranslated(-Specification.B_LENGTH / 2.0 + 0.5 * Specification.S_LENGTH,
				0.5 * (Specification.B_HEIGHT_1 + Specification.B_HEIGHT_2), 0.0);
		spoiler.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		double r = Math.sqrt(Math.pow(0.25D, 2.0D) + Math.pow(0.2625D, 2.0D) + Math.pow(0.1590625D, 2.0D));
		Point c = new Point(0.0D, 0.15D, 0.0D);
		BoundingSphere sphere = new BoundingSphere(r, c);
		sphere.setSphereColore3d(0.0D, 0.0D, 1.0D);
		LinkedList<BoundingSphere> boundingSpheres = new LinkedList();
		boundingSpheres.add(sphere);
		return boundingSpheres;
	}


}
