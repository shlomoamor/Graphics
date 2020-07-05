/**
 * Exercise Number : 	3
 * Date: 				24/05/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 * 
 * */
package edu.cg.scene.lightSources;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class CutoffSpotlight extends PointLight {
	private Vec direction; // apex to circle center point
	private double cutoffAngle;

	public CutoffSpotlight(Vec dirVec, double cutoffAngle) {
		this.direction = dirVec;
		this.cutoffAngle = cutoffAngle;
	}

	public CutoffSpotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}

	public CutoffSpotlight initCutoffAngle(double cutoffAngle) {
		this.cutoffAngle = cutoffAngle;
		return this;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl + description() + "Direction: " + direction + endl;
	}

	@Override
	public CutoffSpotlight initPosition(Point position) {
		return (CutoffSpotlight) super.initPosition(position);
	}

	@Override
	public CutoffSpotlight initIntensity(Vec intensity) {
		return (CutoffSpotlight) super.initIntensity(intensity);
	}

	@Override
	public CutoffSpotlight initDecayFactors(double q, double l, double c) {
		return (CutoffSpotlight) super.initDecayFactors(q, l, c);
	}

	@Override
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		if (pointInSpotlight(rayToLight)) {
			return super.isOccludedBy(surface, rayToLight);
		}

		// point is outside of spotlight
		return true;
	}

	@Override
	public Vec intensity(Point hittingPoint, Ray rayToLight) {
		if (pointInSpotlight(rayToLight)) {
			return super.intensity(hittingPoint, rayToLight);
		}
		// point is outside of spotlight
		return intensity.mult(0);
	}

	private boolean pointInSpotlight(Ray ray) {
		// by the definition of dot product:
		double cosOfAlpha = ray.inverse().direction().normalize().dot(direction.normalize());
		double alpha = Math.toDegrees(Math.acos(cosOfAlpha));
		return alpha < cutoffAngle;
	}
}
