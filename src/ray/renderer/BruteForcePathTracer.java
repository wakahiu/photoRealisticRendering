package ray.renderer;

import ray.brdf.BRDF;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.material.Material;

public class BruteForcePathTracer extends PathTracer {
    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // Find the visible surface along the ray, then add emitted and reflected radiance
        // to get the resulting color.
    	//
    	// If the ray depth is less than the limit (depthLimit), you need
    	// 1) compute the emitted light radiance from the current surface if the surface is a light surface
    	// 2) reflected radiance from other lights and objects. You need recursively compute the radiance
    	//    hint: You need to call gatherIllumination(...) method.
    
    	//Find the ray intersection with any surface
        IntersectionRecord iRec = new IntersectionRecord();

        if((level < depthLimit) && scene.getFirstIntersection(iRec,ray) ){
        
        	//System.out.println("In BruteForce: sampleIndex " + sampleIndex + " level " + level);
        	
        	
        	
        	//Get the BRDF of the material at the point of intersection.
        	Material mat = iRec.surface.getMaterial();
        	Vector3 outDir = new Vector3(1.0,1.0,1.0);
        	
        	//Light source
        	if( mat.isEmitter() ){
        		
        		//emittedRadiance
				Color emitColor = new Color(0.0,0.0,0.0);
				mat.emittedRadiance(null,emitColor);
				outColor.add(emitColor);
				
        	}
        	
        	
        	//Consider negating the scene ray.direcion.
    		//Compute (recursively) the amount of light hitting the points.
    		gatherIllumination(scene, ray.direction, iRec,sampler,sampleIndex,level,outColor);
    		
    		return;
        	
        }
    	scene.getBackground().evaluate(ray.direction,outColor);
    	
    }

}
