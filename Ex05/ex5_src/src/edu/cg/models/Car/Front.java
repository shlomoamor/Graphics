/**
 * Exercise Number : 	5
 * Date: 				16/06/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 *
 * */
package edu.cg.models.Car;

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

	@Override
	public void init(GL2 gl) {
	}

	@Override
	public TreeBoundingSphere getBoundingSpheres() {
		// TODO: Return a list of bounding spheres the list structure is as follow:
		TreeBoundingSphere treeBS = new TreeBoundingSphere();
		treeBS.setBoundingSphere(this.bSphere);
		// Set the sphere to red as shown in PDF
		bSphere.setSphereColore3d(1,0,0);
		return treeBS;
}

	@Override
	public String toString() {
		return "CarFront";
	}
}
