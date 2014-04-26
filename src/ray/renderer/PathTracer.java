package ray.renderer;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;


import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.math.Point3;
import ray.light.PointLight;

public abstract class PathTracer extends DirectOnlyRenderer {

    protected int depthLimit = 5;
    protected int backgroundIllumination = 1;

    public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }
    public void setBackgroundIllumination(int backgroundIllumination) { this.backgroundIllumination = backgroundIllumination; }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    
        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    public void gatherIllumination(Scene scene, Vector3 outDir, 
            IntersectionRecord iRec, SampleGenerator sampler, 
            int sampleIndex, int level, Color outColor) {
    	// W4160 TODO (B)
    	//
        // This method computes a Monte Carlo estimate of reflected radiance due to direct and/or indirect 
        // illumination.  It generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
    	// You need: 
    	//   1. Generate a random incident direction according to proj solid angle
    	//      pdf is constant 1/pi
    	//   2. Recursively find incident radiance from that direction
    	//   3. Estimate the reflected radiance: brdf * radiance / pdf = pi * brdf * radiance
    	
    	/*
   		*	Direct illumination
   		*/
   		/*
    	Material mat = iRec.surface.getMaterial();
    	BRDF matBRDF = mat.getBRDF(iRec);
    	
    	//Multiple light sources may be present. Iterate through them.
    	ArrayList<PointLight> lights = scene.getPointLights();
   		Iterator<PointLight> iterLight = lights.iterator();
   		
   		//Point of intersection of the ray with the surface.
   		Point3 P = iRec.frame.o;
   		
   		Color matColor	= new Color();
        Color ambColor	= new Color();
        
        //Ambient lighting.
	   	scene.getBackground().evaluate(outDir,ambColor);
    	//matBRDF.evaluate( iRec.frame, incDir , outDir, matColor );
    	        	
    	double r = ambColor.r * matColor.r;
    	double g = ambColor.g * matColor.g;
    	double b = ambColor.b * matColor.b;
    	
   		while( iterLight.hasNext() ){
   			PointLight light = iterLight.next();
   			Color lightDiff = light.diffuse;
       		Color lightSpec = light.specular;
       			
   			
   			Vector3 L = new Vector3(	(light.location.x-P.x),
   										(light.location.y-P.y),
   										(light.location.z-P.z));
   			
   			L.normalize();
   			
   			scene.frame.frameToCanonical( L );
    		double matPDF = matBRDF.pdf( scene.frame , L , outDir , outColor );
    		
    		r += lightDiff.r * matPDF;
   			g += lightDiff.g * matPDF;
   			b += lightDiff.b * matPDF;
   				
   			/*
   			//Make a shadow ray. A ray from the location of incidence to
   			//light source.
   			IntersectionRecord iRecBounce = new IntersectionRecord();
   			Ray rayBounce = new Ray(P,L);
   			rayBounce.makeOffsetRay();
   			
   			L_dot_out = outDir.dot(L);		//Cos theta
   			
   			//Check for shadows or occlusions.
   			if(scene.getAnyIntersection(iRecBounce,rayBounce)){
   				continue;
   			}

   			
   			
   		}
   		
    	Color outWeight = new Color(0.0,0.0,0.0);
    	
    	matBRDF.generate(iRec.frame,null,outDir,seed,outWeight);
    	outDir.normalize();
   		
   		/*
   		*	Indirect illumination
   		*
   		//A ray in the new directon.
   		Ray ray = new Ray(P,outDir);
   		ray.makeOffsetRay();
   		rayRadianceRecursive(scene,ray,sampler,sampleIndex,++level,outColor);
   		
   		
   		
   		/*   		
   		
   		//Color outRadiance = new Color();
   		//scene.incidentRadiance(P,outDir,outRadiance);
    	
    	outColor.set(	outWeight.r*r,
    					outWeight.g*g,
    					outWeight.b*b);
    					*/
    	
    }
}
