package ray.renderer;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.light.PointLight;
//import ray.renderer;

/**
 * A renderer that computes radiance due to emitted and directly reflected light only.
 * 
 * @author srm
 */

public class DirectOnlyRenderer implements Renderer {
    
    /**
     * This is the object that is responsible for computing direct illumination.
     */
    DirectIlluminator direct = null;
        
    /**
     * The default is to compute using uninformed sampling wrt. projected solid angle over the hemisphere.
     */
    public DirectOnlyRenderer() {
        this.direct = new ProjSolidAngleIlluminator();
    }
    
    
    /**
     * This allows the rendering algorithm to be selected from the input file by substituting an instance
     * of a different class of DirectIlluminator.
     * @param direct  the object that will be used to compute direct illumination
     */
    public void setDirectIlluminator(DirectIlluminator direct) {
        this.direct = direct;
    }

    
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
        // W4160 TODO (A)
    	// In this function, you need to implement your direct illumination rendering algorithm
    	//
    	// you need:
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) direct reflected radiance from other lights. This is by implementing the function
    	//    ProjSolidAngleIlluminator.directIlluminaiton(...), and call direct.directIllumination(...) in this
    	//    function here.
    	
    	//Find the ray intersection with any surface
        IntersectionRecord iRec = new IntersectionRecord();

		outColor.set(0.0);
        if(scene.getFirstIntersection(iRec,ray)){
        	//Get the BRDF of the material at the point of intersection.
        	Material mat = iRec.surface.getMaterial();
        	Vector3 outDir = ray.direction;
        	
        	//LuminaireSamplingRecord LRec = new LuminaireSamplingRecord();
        	//LRec.set(iRec);
        	
        	//Light source
        	if( mat.isEmitter() ){
        		emittedRadiance(iRec, outDir, outColor);
        	}else{
        	
        		Random rand = new Random();
        		Point2 seed = new Point2(rand.nextDouble(),rand.nextDouble());
        		
        		Vector3 inDir = ray.direction;
        		
        		ProjSolidAngleIlluminator PSAI = new ProjSolidAngleIlluminator();
        		PSAI.directIllumination(scene,inDir,outDir,iRec,seed,outColor);
        	}
        	return;
        	
        }
        
    	scene.getBackground().evaluate(ray.direction,outColor);
    	
    }

    
    /**
     * Compute the radiance emitted by a surface.
     * @param iRec      Information about the surface point being shaded
     * @param dir      	The exitant direction (surface coordinates)
     * @param outColor  The emitted radiance is written to this color
     */
    protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir, Color outColor) {
    	// W4160 TODO (A)
        // If material is emitting, query it for emission in the relevant direction.
        // If not, the emission is zero.
    	// This function should be called in the rayRadiance(...) method above
    	
    	Material mat = iRec.surface.getMaterial();
    	
    	//emittedRadiance
        mat.emittedRadiance(null,outColor);
    	
    }
}
