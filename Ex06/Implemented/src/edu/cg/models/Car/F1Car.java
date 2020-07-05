package edu.cg.models.Car;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jogamp.opengl.*;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;

/**
 * A F1 Racing Car.
 *
 */
public class F1Car implements IRenderable, IIntersectable {
	Center carCenter = new Center();
	Back carBack = new Back();
	Front carFront = new Front();

	public void render(GL2 gl) {
		this.carCenter.render(gl);
		gl.glPushMatrix();
		gl.glTranslated(-0.3875D, 0.0D, 0.0D);
		this.carBack.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(0.425D, 0.0D, 0.0D);
		this.carFront.render(gl);
		gl.glPopMatrix();
	}

	public String toString() {
		return "F1Car";
	}

	public void init(GL2 gl) {
	}

	public List<BoundingSphere> getBoundingSpheres() {
		ArrayList<BoundingSphere> bSList = new ArrayList();
		double radius1 = Math.sqrt(Math.pow(0.65D, 2.0D) + Math.pow(0.15D, 2.0D) + Math.pow(0.25D, 2.0D));
		double radius2 = Math.sqrt(Math.pow(0.725D, 2.0D) + Math.pow(0.2D, 2.0D));
		bSList.add(new BoundingSphere(Math.max(radius1, radius2), new Point(0.0D, 0.15D, 0.0D)));
		// Front spheres
		List<BoundingSphere> bSFront = this.carFront.getBoundingSpheres();
		Iterator var8 = bSFront.iterator();
		while(var8.hasNext()) {
			BoundingSphere s = (BoundingSphere)var8.next();
			s.translateCenter(0.425D, 0.0D, 0.0D);
		}
		bSList.addAll(bSFront);
		bSList.addAll(this.carCenter.getBoundingSpheres());
		// Back spheres
		List<BoundingSphere> bSBack = this.carBack.getBoundingSpheres();
		Iterator var9 = bSBack.iterator();
		while(var9.hasNext()) {
			BoundingSphere sphere = (BoundingSphere)var9.next();
			sphere.translateCenter(-0.3875D, 0.0D, 0.0D);
		}
		bSList.addAll(bSBack);
		return bSList;
	}

	public void destroy(GL2 gl) {
	}
}