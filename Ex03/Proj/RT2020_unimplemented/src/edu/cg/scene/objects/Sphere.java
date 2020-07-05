/**
 * Exercise Number : 	3
 * Date: 				24/05/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 * 
 * */
package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.*;

public class Sphere extends Shape {
	private Point center;
	private double radius;
	
	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + 
				"Center: " + center + endl +
				"Radius: " + radius + endl;
	}
	
	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}
	// Getters for the Dome class
	public double getRadius() {
		return this.radius;
	}
	// Getters for the Dome class
	public Point getCenter() {
		return this.center;
		
	}
	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}
	// Solving a quadratic equation
	public double[] solveQuadraticEquation(double a, double b, double c){

		double[] roots = new double[2];
		double d = b * b - 4 * a * c;

		if(d > 0)
		{
			roots[0] = (-b + Math.sqrt(d))/(2*a);
			roots[1]= (-b - Math.sqrt(d))/(2*a);
		}
		else if(d == 0)
		{
			roots[0] = (-b+Math.sqrt(d))/(2*a);
		}

		// The array of the two solutions
		return roots;
	}
	
	@Override
	public Hit intersect(Ray ray) {
		int a = 1;
		double t = Double.MAX_VALUE;
		boolean isIn = false;
		double b = Ops.dot(Ops.mult(2,ray.direction()),(Ops.sub(ray.source(),center)));
		double c = Ops.normSqr(Ops.sub(ray.source(), center)) - Math.pow(radius,2);
		double[] roots = this.solveQuadraticEquation(a,b,c);

		// Find the t of the proper hitting point
		for (double root : roots) {
			if((!Double.isNaN(root)) &&  Ops.epsilon < root && root < t){
				t = root;
			}
		}

		// finding the hit point and its direction
		Point hitPoint = ray.add(t);
		Vec rDirection = Ops.sub(hitPoint,center);
		Vec normRDirection = Ops.normalize(rDirection);

		// Check if the given ray is within the sphere for the use of the refraction
		// Change the normal accordingly
		if(roots[1] < Ops.epsilon){
			isIn = true;
			normRDirection = Ops.normalize(rDirection).neg();
		}

		// create the hit and set its location value(is in or out)
		Hit hit = new Hit(t, normRDirection).setIsWithin(isIn);
		if (t == Double.MAX_VALUE){
			hit = null;
		}

		return hit;
	}
}
