/**
 * Exercise Number : 	5
 * Date: 				16/06/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 *
 * */
package edu.cg.models.Car;
import java.util.LinkedList;
import com.jogamp.opengl.*;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;
import edu.cg.models.TreeBoundingSphere;

/**
 * A F1 Racing Car.
 *
 */
public class F1Car implements IRenderable, IIntersectable {
	// TODO : Add new design features to the car.
	// Remember to include a ReadMe file specifying what you implemented.
	Center carCenter = new Center();

	Back carBack = new Back();
	Front carFront = new Front();
	double radiusCalc = Math.pow(Specification.S_DEPTH,2) +
				Math. pow(Specification.F_LENGTH + Specification.C_LENGTH + Specification.B_LENGTH,2) +
				Math.pow((0.5 * (Specification.C_BACK_HEIGHT_1 + Specification.C_BACK_HEIGHT_2) + Specification.S_ROD_HIEGHT
						+ Specification.S_WINGS_HEIGHT),2);

	double radius = 0.5 * Math.sqrt(radiusCalc);
	TreeBoundingSphere boundingSphere = new TreeBoundingSphere();

	BoundingSphere fullCarSphere  = new BoundingSphere(radius,new Point(0.05 * Specification.C_LENGTH, 0.5 * Specification.C_HIEGHT ,0));


	public F1Car() {
		// s1 - sphere bounding the whole car
		BoundingSphere s1 = fullCarSphere;
		// s2 - sphere bounding the car front
		TreeBoundingSphere s2 = carFront.getBoundingSpheres();
		// s3 - sphere bounding the car center
		TreeBoundingSphere s3 = carCenter.getBoundingSpheres();
		// s4 - sphere bounding the car back
		TreeBoundingSphere s4 = carBack.getBoundingSpheres();
		carBack.getBoundingSpheres().getBoundingSphere().translateCenter(-0.51 * Specification.B_LENGTH -0.5 * Specification.C_LENGTH, 0.0, 0.0);
		// Get the bounding sphere for the back
		if(carBack.getBoundingSpheres().getList() != null)
		{
			for (TreeBoundingSphere curr : carBack.getBoundingSpheres().getList()) {
				curr.getBoundingSphere().translateCenter(-0.14 * Specification.B_LENGTH -0.1 * Specification.C_LENGTH, 0.0, 0.0);;
			}
		}
		carFront.getBoundingSpheres().getBoundingSphere().translateCenter((Specification.C_LENGTH * 0.5) + (Specification.F_LENGTH * 0.5), 0.0, 0.0);
		// Get the bounding sphere for the front
		if(carFront.getBoundingSpheres().getList() != null)
		{
			for (TreeBoundingSphere curr : carFront.getBoundingSpheres().getList()) {
				curr.getBoundingSphere().translateCenter((Specification.C_LENGTH * 0.18) + (Specification.F_LENGTH * 0.1), 0.0, 0.0);;
			}
		}
		LinkedList<TreeBoundingSphere> sphereList = new LinkedList<>();
		boundingSphere.setBoundingSphere(s1);
		sphereList.add(s2);
		sphereList.add(s3);
		sphereList.add(s4);
		boundingSphere.setList(sphereList);
	}
	@Override
	public TreeBoundingSphere getBoundingSpheres() {
		return this.boundingSphere;
	}

	@Override
	public void render(GL2 gl) {
		carCenter.render(gl);
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 - Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
		carBack.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(Specification.F_LENGTH / 2.0 + Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
		carFront.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public String toString() {
		return "F1Car";
	}

	@Override
	public void init(GL2 gl) {

	}

}

