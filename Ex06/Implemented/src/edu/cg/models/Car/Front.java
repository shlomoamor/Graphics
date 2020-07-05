package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL2;
import edu.cg.algebra.Point;
import edu.cg.models.*;

public class Front implements IRenderable, IIntersectable {
	// TODO: Add necessary fields (e.g. the bumper).
	private FrontHood hood = new FrontHood();
	private PairOfWheels wheels = new PairOfWheels();
	private FrontBumber frontBumber = new FrontBumber();
	// Trial and error for the best fit sphere
	private BoundingSphere  bSphere  = new BoundingSphere(Specification.F_LENGTH*0.6,
				new Point(0.0 ,Specification.F_HEIGHT * 0.5,0));

	@Override
	public void render(GL2 gl) {
		// TODO: Render the BUMPER. Look at how we place the front and the wheels of
		// the car.
		gl.glPushMatrix();
		// Render hood - Use Red Material.
		gl.glTranslated(-Specification.F_LENGTH / 2.0 + Specification.F_HOOD_LENGTH / 2.0, 0.0, 0.0);
		hood.render(gl);
		// Render Bumber - Use Red Material
		frontBumber.render(gl);
		// Render the wheels.
		gl.glTranslated(Specification.F_HOOD_LENGTH / 2.0 - 1.25 * Specification.TIRE_RADIUS,
				0.5 * Specification.TIRE_RADIUS, 0.0);
		wheels.render(gl);
		gl.glPopMatrix();
	}
	public void destroy(GL2 gl) {};

	@Override
	public void init(GL2 gl) {
	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		double r = Math.sqrt(Math.pow(0.25D, 2.0D) + Math.pow(0.2625D, 2.0D) + Math.pow(0.15D, 2.0D));
		Point c = new Point(0.0D, 0.15D, 0.0D);
		BoundingSphere sphere = new BoundingSphere(r, c);
		sphere.setSphereColore3d(0.0D, 0.0D, 1.0D);
		LinkedList<BoundingSphere> boundingSpheres = new LinkedList();
		boundingSpheres.add(sphere);
		return boundingSpheres;
	}

	@Override
	public String toString() {
		return "CarFront";
	}
}
