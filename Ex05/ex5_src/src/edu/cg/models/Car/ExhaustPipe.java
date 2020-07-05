package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
/**
 * Exercise Number : 	5
 * Date: 				16/06/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 *
 * */
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.models.IRenderable;

public class ExhaustPipe implements IRenderable  {

    @Override
    public void render(GL2 gl) {
        GLU glu = new GLU();
        GLUquadric q  = glu.gluNewQuadric();

        // Place the first exhaust pipe
        Materials.SetDarkGreyMetalMaterial(gl);
        gl.glTranslated(-Specification.B_LENGTH/5.0, 0, -2*Specification.PAIR_OF_WHEELS_ROD_RADIUS);
        gl.glRotated(90,0,1,0);
        glu.gluCylinder(q,Specification.E_EXHAUST_RADIUS, Specification.E_EXHAUST_RADIUS,Specification.E_EXHAUST_RADIUS*4,20,1);

        // Place the middle exhaust pipe, we set it to black
        Materials.SetBlackMetalMaterial(gl);
        gl.glTranslated(-2*Specification.PAIR_OF_WHEELS_ROD_RADIUS,0,0);
        glu.gluCylinder(q,Specification.E_EXHAUST_RADIUS, Specification.E_EXHAUST_RADIUS,Specification.E_EXHAUST_RADIUS*4,20,1);

        // Place the last exhaust pipe
        Materials.SetDarkGreyMetalMaterial(gl);
        gl.glTranslated(-2*Specification.PAIR_OF_WHEELS_ROD_RADIUS,0,0);
        glu.gluCylinder(q,Specification.E_EXHAUST_RADIUS, Specification.E_EXHAUST_RADIUS,Specification.E_EXHAUST_RADIUS*4,20,1);

    }

    @Override
    public void init(GL2 gl) {
    }

    @Override
    public String toString() {
        return "ExhaustPipe";
    }

}


