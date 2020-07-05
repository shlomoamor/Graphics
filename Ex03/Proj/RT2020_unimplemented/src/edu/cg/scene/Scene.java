/**
 * Exercise Number : 	3
 * Date: 				24/05/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 * 
 * */
package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Ops;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.camera.PinholeCamera;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; // gets the values of 1, 2 and 3
	private boolean renderRefractions = false;
	private boolean renderReflections = false;

	private PinholeCamera camera;
	private Vec ambient = new Vec(1, 1, 1); // white
	private Vec backgroundColor = new Vec(0, 0.5, 1); // blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();

	// MARK: initializers
	public Scene initCamera(Point eyePoistion, Vec towardsVec, Vec upVec, double distanceToPlain) {
		this.camera = new PinholeCamera(eyePoistion, towardsVec, upVec, distanceToPlain);
		return this;
	}

	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}

	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}

	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}

	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}

	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}

	public Scene initName(String name) {
		this.name = name;
		return this;
	}

	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefractions = renderRefarctions;
		return this;
	}

	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}

	// MARK: getters
	public String getName() {
		return name;
	}

	public int getFactor() {
		return antiAliasingFactor;
	}

	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}

	public boolean getRenderRefarctions() {
		return renderRefractions;
	}

	public boolean getRenderReflections() {
		return renderReflections;
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Camera: " + camera + endl + "Ambient: " + ambient + endl + "Background Color: " + backgroundColor + endl
				+ "Max recursion level: " + maxRecursionLevel + endl + "Anti aliasing factor: " + antiAliasingFactor
				+ endl + "Light sources:" + endl + lightSources + endl + "Surfaces:" + endl + surfaces;
	}

	private transient ExecutorService executor = null;
	private transient Logger logger = null;

	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
		// TODO: initialize your additional field here.
	}

	public BufferedImage render(int imgWidth, int imgHeight, double viewAngle, Logger logger)
			throws InterruptedException, ExecutionException, IllegalArgumentException {

		initSomeFields(imgWidth, imgHeight, logger);

		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		camera.initResolution(imgHeight, imgWidth, viewAngle);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);

		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][]) (new Future[imgHeight][imgWidth]);

		this.logger.log("Starting to shoot " + (imgHeight * imgWidth * antiAliasingFactor * antiAliasingFactor)
				+ " rays over " + name);

		for (int y = 0; y < imgHeight; ++y)
			for (int x = 0; x < imgWidth; ++x)
					futures[y][x] = calcColor(x, y);


		this.logger.log("Done shooting rays.");
		this.logger.log("Wating for results...");
		
		for (int y = 0; y < imgHeight; ++y)
			for (int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}
		executor.shutdown();

		this.logger.log("Ray tracing of " + name + " has been completed.");

		executor = null;
		this.logger = null;

		return img;
	}

	private Future<Color> calcColor(int x, int y) {
		return this.executor.submit(() -> {
	         Point point1 = this.camera.transform(x, y);
	         Point point2 = this.camera.transform(x + 1, y + 1);
	         Vec colorVector = new Vec();

	         for(int i = 0; i < this.antiAliasingFactor; ++i) {
	            for(int j = 0; j < this.antiAliasingFactor; ++j) {
	               Point point1weight = (new Point((double)(this.antiAliasingFactor - j), (double)(this.antiAliasingFactor - i), (double)this.antiAliasingFactor)).mult(1.0D / (double)this.antiAliasingFactor);
	               Point point2weight = (new Point((double)j, (double)i, 0.0D)).mult(1.0D / (double)this.antiAliasingFactor);
	               Point pointOnScreenPlain = Ops.add(point1.mult(point1weight), point2.mult(point2weight));
	               Ray ray = new Ray(this.camera.getCameraPosition(), pointOnScreenPlain);
	               colorVector = colorVector.add(this.calcColor(ray, 0));
	            }
	         }

	         return colorVector.mult(1.0D / (double)(Math.pow(this.antiAliasingFactor,2 ))).toColor();
	      });
	   }

	private Vec calcColor(Ray ray, int recursionLevel) {
		// Base case for recursion
		if(recursionLevel == maxRecursionLevel) {
			Vec empty = new Vec();
			return empty;
		}
		else {
	         Hit rayIntersect = this.intersection(ray);
	         // if we don't intersect with an object return the background colour
	         if (rayIntersect == null) {
	            return this.backgroundColor;
	         } else {
	            Point hitPoint = ray.getHittingPoint(rayIntersect);
	            Surface surf = rayIntersect.getSurface();
	            // calc  K_aI_a 
	            Vec colourModel = surf.Ka().mult(this.ambient);
	            Iterator<Light> lightSourceIterator = this.lightSources.iterator();
	            
	            // As long as there are still lightsources
	            while(lightSourceIterator.hasNext()) {
	            	// get the current light source from the list 
	            	Light currLightSrc = (Light)lightSourceIterator.next();
	            	// set the ray from light to hitting point
	            	Ray rayToLight = currLightSrc.rayToLight(hitPoint);
	            	boolean blocked = this.isOccluded(currLightSrc, rayToLight);
	            	if (!blocked) {
	            		// calc the diffuse vector
	            		Vec intersectToLight = rayToLight.direction();
	          	      	Vec intersectToNormal = rayIntersect.getNormalToSurface();
	          	      	Vec Kd = rayIntersect.getSurface().Kd();
	            		Vec tmpColor = Kd.mult(Math.max(intersectToNormal.dot(intersectToLight), 0.0D));
	            		tmpColor = tmpColor.add(this.specular(rayIntersect, rayToLight, ray));
	            		Vec intensity = currLightSrc.intensity(hitPoint, rayToLight);
	            		colourModel = colourModel.add(tmpColor.mult(intensity));
	               }
	            }
	            // check if renderReflections
	            if (this.renderReflections) {
	               Vec refracColour = Ops.reflect(ray.direction(), rayIntersect.getNormalToSurface());
	               Vec reflecWeight = new Vec(surf.reflectionIntensity());
	               // get the reflection colour through the recursion remembering to up the recursionLevel
	               Vec reflecColor = this.calcColor(new Ray(hitPoint, refracColour), recursionLevel + 1).mult(reflecWeight);
	               // add to the colour model our colour
	               colourModel = colourModel.add(reflecColor);
	            }

	            if (this.renderRefractions) {
	               if (surf.isTransparent()) {
	                  colourModel = colourModel.add(colourModel);
	               }
	            }
	            return colourModel;
	         }
	      }
	}
	
	//Checks if the given surface occludes the light-source. The surface occludes the light source
	// if the given ray first intersects the surface before reaching the light source.
	private boolean isOccluded(Light light, Ray rayToLight) {
	      Iterator<Surface> lightSourceIterator = this.surfaces.iterator();

	      while(lightSourceIterator.hasNext()) {
	         Surface surf = (Surface)lightSourceIterator.next();
	         if (light.isOccludedBy(surf, rayToLight)) {
	            return true;
	         }
	      }
	      // the surface doesnt occlude the light source
	      return false;
	   }
	
	 private Hit intersection(Ray ray) {
	      Hit minHit = null;
	      Iterator<Surface> surfIterator = this.surfaces.iterator();

	      while(true) {
	         Hit newHit;
	         do {
	            if (!surfIterator.hasNext()) {
	               return minHit;
	            }

	            Surface surface = (Surface)surfIterator.next();
	            newHit = surface.intersect(ray);
	         } while(minHit != null && (newHit == null || newHit.compareTo(minHit) >= 0));

	         minHit = newHit;
	      }
	   }
	 

	   private Vec specular(Hit minHit, Ray rayToLight, Ray rayFromViewer) {
	      Vec L = rayToLight.direction();
	      Vec N = minHit.getNormalToSurface();
	      Vec R = Ops.reflect(L.neg(), N);
	      Vec Ks = minHit.getSurface().Ks();
	      Vec v = rayFromViewer.direction();
	      int shininess = minHit.getSurface().shininess();
	      double dot = R.dot(v.neg());
	      return dot < 0.0D ? new Vec() : Ks.mult(Math.pow(dot, (double)shininess));
	   }
}
