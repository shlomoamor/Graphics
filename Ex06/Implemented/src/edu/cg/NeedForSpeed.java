package edu.cg;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import edu.cg.algebra.Vec;
import edu.cg.models.BoundingSphere;
import edu.cg.models.Track;
import edu.cg.models.TrackSegment;
import edu.cg.models.Car.F1Car;



/**
 * An OpenGL 3D Game.
 *
 */
public class NeedForSpeed implements GLEventListener {
	private GameState gameState = null; // Tracks the car movement and orientation
	private F1Car car = null; // The F1 car we want to render
	private Vec carCamTranslationMatrix = null; // The accumulated translation that should be applied on the car, camera
												// and light sources
	private Track gameTrack = null; // The game track we want to render
	private FPSAnimator ani; // This object is responsible to redraw the model with a constant FPS
	private Component glPanel; // The canvas we draw on.
	private boolean isModelInitialized = false; // Whether model.init() was called.
	private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
	private boolean isBirdseyeView = false; // Indicates whether the camera is looking from above on the scene or
	private double carScale = 4.0d;
	private double[] carInitialPosition;
	private double[] cameraInitialPositionBirdseye;
	private double[] cameraInitialPositionThirdperson;
	private float alpha;
	private float beta;
											// looking
	// towards the car direction.
	// TODO: add fields as you want. Fr example:o
	// - Car initial position (should be fixed).
	// - Camera initial position (should be fixed)
	// - Different camera settings
	// - Light colors
	// Or in short anything reusable - this make it easier for your to keep track of your implementation.

	public NeedForSpeed(Component glPanel) {
		this.carInitialPosition = new double[]{0.0D, this.carScale * 0.075D, this.carScale * -0.65D - 2.0D};
		this.cameraInitialPositionBirdseye = new double[]{this.carInitialPosition[0], 50.0D, this.carInitialPosition[2] - this.carScale * 0.725D - 22.0D};
		this.cameraInitialPositionThirdperson = new double[]{this.carInitialPosition[0], 2.0D, this.carInitialPosition[2] + 4.0D + this.carScale * 0.65D};
		this.alpha = (float)this.carScale * 0.25F / 2.0F + (float)this.carScale * 0.6F;
		this.beta = (float)this.carScale * 0.24F / 2.0F;
		this.glPanel = glPanel;
		this.gameState = new GameState();
		this.gameTrack = new Track();
		this.carCamTranslationMatrix = new Vec(0.0D);
		this.car = new F1Car();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		if (!isModelInitialized) {
			initModel(gl);
		}
		if (isDayMode) {
			// TODO: Setup background when day mode is on
			gl.glClearColor(0.52f, 0.824f, 1.0f, 1.0f);
			// use gl.glClearColor() function.
		} else {
			// TODO: Setup background when night mode is on
			gl.glClearColor(0.0f, 0.0f, 0.3f, 1.0f);
		}
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// TODO: This is the flow in which we render the scene.
		// Step (1) Update the accumulated translation that needs to be
		// applied on the car, camera and light sources.
		updateCarCameraTranslation(gl);
		// Step (2) Position the camera and setup its orientation
		setupCamera(gl);
		// Step (3) setup the lights.
		setupLights(gl);
		// Step (4) render the car.
		renderCar(gl);
		// Step (5) render the track.
		renderTrack(gl);
		// Step (6) check collision. Note this has nothing to do with OpenGL.
		if (checkCollision()) {
			JOptionPane.showMessageDialog(this.glPanel, "Game is Over");
			this.gameState.resetGameState();
			this.carCamTranslationMatrix = new Vec(0.0);
		}

	}

	/**
	 * @return Checks if the car intersects the one of the boxes on the track.
	 */
	private boolean checkCollision() {
		List<BoundingSphere> f1SphereList = this.car.getBoundingSpheres();
		List<BoundingSphere> trackBoundingSpheres = this.gameTrack.getBoundingSpheres();
		Iterator f1Iterator = f1SphereList.iterator();
		BoundingSphere bSBox;
		while(f1Iterator.hasNext()) {
			bSBox = (BoundingSphere)f1Iterator.next();
			bSBox.scaleRadius(this.carScale);
			bSBox.translateCenter(this.carInitialPosition[0] + (double)this.carCamTranslationMatrix.x,
					this.carInitialPosition[1] + (double)this.carCamTranslationMatrix.y,
					this.carInitialPosition[2] + (double)this.carCamTranslationMatrix.z);
		}
		f1Iterator = trackBoundingSpheres.iterator();
		do {
			do {
				if (!f1Iterator.hasNext()) {
					return false;
				}

				bSBox = (BoundingSphere)f1Iterator.next();
			} while(!bSBox.checkIntersection((BoundingSphere)f1SphereList.get(0)));
		} while(!bSBox.checkIntersection((BoundingSphere)f1SphereList.get(1)) && !bSBox.checkIntersection((BoundingSphere)f1SphereList.get(2)) && !bSBox.checkIntersection((BoundingSphere)f1SphereList.get(3)));

		return true;
	}



	private void updateCarCameraTranslation(GL2 gl) {
		Vec nextGameStage = gameState.getNextTranslation();
		carCamTranslationMatrix = carCamTranslationMatrix.add(nextGameStage);
		double dx = Math.max(carCamTranslationMatrix.x, -TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 - 2);
		carCamTranslationMatrix.x = (float) Math.min(dx, TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 + 2);
		if (Math.abs(carCamTranslationMatrix.z) >= TrackSegment.TRACK_LENGTH + 10.0) {
			carCamTranslationMatrix.z = -(float) (Math.abs(carCamTranslationMatrix.z) % TrackSegment.TRACK_LENGTH);
			gameTrack.changeTrack(gl);
		}
	}

	private void setupCamera(GL2 gl) {
		GLU glu = new GLU();
		// Check if we are in birds eye view
		if (this.isBirdseyeView) {
			double camX = this.cameraInitialPositionBirdseye[0] + (double)this.carCamTranslationMatrix.x;
			double camY = this.cameraInitialPositionBirdseye[1] + (double)this.carCamTranslationMatrix.y;
			double camZ = this.cameraInitialPositionBirdseye[2] + (double)this.carCamTranslationMatrix.z;
			glu.gluLookAt(camX, camY, camZ, camX, camY - 1.0D, camZ, 0.0D, 0.0D, -1.0D);
		}
		// Normal view
		else {
			double camX = this.cameraInitialPositionThirdperson[0] + (double)this.carCamTranslationMatrix.x;
			double camY = this.cameraInitialPositionThirdperson[1] + (double)this.carCamTranslationMatrix.y;
			double camZ = this.cameraInitialPositionThirdperson[2] + (double)this.carCamTranslationMatrix.z;
			glu.gluLookAt(camX, camY, camZ, camX, camY, camZ - 10.0D, 0.0D, 1.0D, 0.0D);
		}

	}


	private void setupLights(GL2 gl) {
		int light = 16385;
		// Check if we are in Day Mode
		if (this.isDayMode) {
			gl.glDisable(light);
			//int light = 16384;
			float[] directionalLightColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
			Vec directionVector = (new Vec(0.0D, 1.0D, 1.0D)).normalize();
			float[] pos = new float[]{directionVector.x, directionVector.y, directionVector.z, 0.0F};
			gl.glLightfv(light, 4610, directionalLightColor, 0);
			gl.glLightfv(light, 4609, directionalLightColor, 0);
			gl.glLightfv(light, 4611, pos, 0);
			gl.glLightfv(light, 4608, new float[]{0.1F, 0.1F, 0.1F, 1.0F}, 0);
			gl.glEnable(light);
		}
		// Night mode
		else {
			gl.glLightModelfv(2899, new float[]{0.25F, 0.25F, 0.3F, 1.0F}, 0);
			double carAngle = -Math.toRadians(this.gameState.getCarRotation());
			float[] pos1 = new float[]{this.carCamTranslationMatrix.x + (float) this.carInitialPosition[0] - this.alpha *
					(float) Math.sin(carAngle) + this.beta * (float) Math.cos(carAngle), (float) this.carInitialPosition[1] +
					(float) (this.carScale * 0.014D), this.carCamTranslationMatrix.z +
					(float) this.carInitialPosition[2] - this.alpha * (float) Math.cos(carAngle) - this.beta * (float) Math.sin(carAngle), 1.0F};

			float[] lightDir = new float[]{-(float) Math.sin(carAngle), 0.0F, -(float) Math.cos(carAngle)};
			// Set up the car light for the second headlight
			float[] dayColour = new float[]{0.85F, 0.85F, 0.85F, 1.0F};
			gl.glLightfv(light, 4611, pos1, 0);
			gl.glLightf(light, 4614, 90.0F);
			gl.glLightfv(light, 4612, lightDir, 0);
			gl.glLightfv(light, 4610, dayColour, 0);
			gl.glLightfv(light, 4609, dayColour, 0);
			gl.glEnable(light);

			float[] pos2 = new float[]{this.carCamTranslationMatrix.x + (float) this.carInitialPosition[0] - this.alpha *
					(float) Math.sin(carAngle) - this.beta * (float) Math.cos(carAngle), (float) this.carInitialPosition[1] +
					(float) (this.carScale * 0.014D), this.carCamTranslationMatrix.z +
					(float) this.carInitialPosition[2] - this.alpha * (float) Math.cos(carAngle) + this.beta * (float) Math.sin(carAngle), 1.0F};
			// Set up the car light for the second headlight
			float[] dayColour2 = new float[]{0.85F, 0.85F, 0.85F, 1.0F};
			gl.glLightfv(light, 4611, pos2, 0);
			gl.glLightf(light, 4614, 90.0F);
			gl.glLightfv(light, 4612, lightDir, 0);
			gl.glLightfv(light, 4610, dayColour2, 0);
			gl.glLightfv(light, 4609, dayColour2, 0);
			gl.glEnable(light);
		}

	}

	private void renderTrack(GL2 gl) {
		// * Note: the track is not translated. It should be fixed.
		gl.glPushMatrix();
		gameTrack.render(gl);
		gl.glPopMatrix();
	}

	private void renderCar(GL2 gl) {
		double roatation = this.gameState.getCarRotation();
		gl.glPushMatrix();
		gl.glTranslated(this.carInitialPosition[0] + (double)this.carCamTranslationMatrix.x, this.carInitialPosition[1] +
				(double)this.carCamTranslationMatrix.y, this.carInitialPosition[2] + (double)this.carCamTranslationMatrix.z);
		gl.glRotated(90.0D - roatation, 0.0D, 1.0D, 0.0D);
		gl.glScaled(this.carScale, this.carScale, this.carScale);
		this.car.render(gl);
		gl.glPopMatrix();
	}

	public GameState getGameState() {
		return gameState;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Initialize display callback timer
		ani = new FPSAnimator(30, true);
		ani.add(drawable);
		glPanel.repaint();

		initModel(gl);
		ani.start();
	}

	public void initModel(GL2 gl) {
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_CULL_FACE);

		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_SMOOTH);

		car.init(gl);
		gameTrack.init(gl);
		isModelInitialized = true;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		gl.glMatrixMode(5889);
		gl.glLoadIdentity();
		// Check if this is birdseye view
		if (this.isBirdseyeView) {
			glu.gluPerspective(60.0D, (double)width / (double)height, 2.0D, 500.0D);
		}
		// Otherwise, we are normal view
		else {
			glu.gluPerspective(60.0D, (double)width / (double)height, 2.0D, 500.0D);
		}

	}

	/**
	 * Start redrawing the scene with 30 FPS
	 */
	public void startAnimation() {
		if (!ani.isAnimating())
			ani.start();
	}

	/**
	 * Stop redrawing the scene with 30 FPS
	 */
	public void stopAnimation() {
		if (ani.isAnimating())
			ani.stop();
	}

	public void toggleNightMode() {
		isDayMode = !isDayMode;
	}

	public void changeViewMode() {
		isBirdseyeView = !isBirdseyeView;
	}

}