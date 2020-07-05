/**
 * Exercise Number : 	3
 * Date: 				24/05/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 * 
 * */
package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class Dome extends Shape {
	private Sphere sphere;
	private Plain plain;

	public Dome() {
		sphere = new Sphere().initCenter(new Point(0, -0.5, -6));
		plain = new Plain(new Vec(-1, 0, -1), new Point(0, -0.5, -6));
	}

	public Dome(Point center, double radious, Vec plainDirection) {
		sphere = new Sphere(center, radious);
		plain = new Plain(plainDirection, center);
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Dome:" + endl + sphere + plain + endl;
	}

	@Override
	public Hit intersect(Ray ray) {
	      Hit hit = this.sphere.intersect(ray);
	      if (hit == null) {
	         return null;
	      } else {
	    	  if(hit.isWithinTheSurface()) {
	    		  Point hittingPoint = ray.getHittingPoint(hit);
	    	      if (this.plain.substitute(ray.source()) > 1.0E-5D) {
	    	         if (this.plain.substitute(hittingPoint) > 0.0D) {
	    	            return hit;
	    	         } else {
	    	            hit = this.plain.intersect(ray);
	    	            if(hit == null) {
	    	            	return null;
	    	            }
	    	            else {
	    	            	hit.setWithin();
	    	            }
	    	         }
	    	      } else {
	    	    	  if(this.plain.substitute(hittingPoint) > 0.0) {
	    	    		  return this.plain.intersect(ray);
	    	    	  }
	    	    	  else {
	    	    		  return null;
	    	    	  }
	    	      }
	    	  }
	    	  else {
	    		  Point hittingPoint = ray.getHittingPoint(hit);
	    	      if (this.plain.substitute(hittingPoint) > 0.0D) {
	    	         return hit;
	    	      } else {
	    	         hit = this.plain.intersect(ray);
	    	         if (hit == null) {
	    	            return null;
	    	         } else {
	    	            hittingPoint = ray.getHittingPoint(hit);
	    	            if(hittingPoint.distSqr(this.sphere.getCenter()) - this.sphere.getRadius() * this.sphere.getRadius() < 0.0){
	    	            	return hit;
	    	            }
	    	            else {
	    	            	return null;
	    	            }
	    	         }
	    	      }
	    	  }
	      }
	    return null;
	   }
	}
