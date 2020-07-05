/**
 * Exercise Number : 	5
 * Date: 				16/06/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 *
 * */
package edu.cg.models;

import com.jogamp.opengl.GL2;

import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.models.Car.Materials;

public class BoundingSphere implements IRenderable {
	private double radius = 0.0;
	private Point center;
	private double color[];

	public BoundingSphere(double radius, Point center) {
		color = new double[3];
		this.setRadius(radius);
		this.setCenter(new Point(center.x, center.y, center.z));
	}

	public void setSphereColore3d(double r, double g, double b) {
		this.color[0] = r;
		this.color[1] = g;
		this.color[2] = b;
	}

	/**
	 * Given a sphere s - check if this sphere and the given sphere intersect.
	 * 
	 * @return true if the spheres intersects, and false otherwise
	 */
	public boolean checkIntersection(BoundingSphere s) {
		// TODO: Check if two spheres intersect.
		boolean isIntersect = false;
		// Calc the distance between both centers
		double disBetweenCenters = Ops.dist(s.center,this.center);
		// Sum both radii
		double sumOfRadii = s.radius+this.radius;
		if(disBetweenCenters <= sumOfRadii){
			isIntersect = true;
		}
		return isIntersect;
	}

	public void translateCenter(double dx, double dy, double dz) {
		// TODO: Translate the sphere center by (dx,dy,dz).
		Point p = new Point(this.center.x+dx,this.center.y+dy,this.center.z+dz);
		this.setCenter(p);
	}

	@Override
	public void render(GL2 gl) {
		// TODO: Render a sphere with the given radius and center.
		// NOTE : Use the specified color when rendering
		gl.glPushMatrix();
		GLU glu = new GLU();
		GLUquadric q = glu.gluNewQuadric();

		float[] arr = new float[this.color.length];
		for (int i = 0 ; i < this.color.length; i++)
		{
			arr[i] = (float) this.color[i];
		}
		Materials.SetMetalMaterial(gl,arr);
		gl.glTranslated(center.x,center.y,center.z);
		glu.gluSphere(q, this.radius, 	10, 10);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

}
