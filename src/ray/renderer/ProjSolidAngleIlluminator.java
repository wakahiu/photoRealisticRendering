package ray.renderer;

import java.util.ArrayList;
import java.util.Iterator;

import ray.misc.Ray;
import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Scene;
import ray.math.Point3;
import ray.light.PointLight;


/**
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 * 
 * @author srm, Changxi Zheng(+)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator {
    
    
    public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir, 
            IntersectionRecord iRec, Point2 seed, Color outColor) {
        // W4160 TODO (A)
    	// This method computes a Monte Carlo estimate of reflected radiance due to direct illumination.  It
        // generates samples uniformly wrt. the projected solid angle measure:
        //
        //    f = brdf * radiance
        //    p = 1 / pi
        //    g = f / p = brdf * radiance * pi
        //
        // The same code could be interpreted as an integration wrt. solid angle, as follows:
        //
        //    f = brdf * radiance * cos_theta
        //    p = cos_theta / pi
        //    g = f / p = brdf * radiance * pi
    	// 
    	// As a hint, here are a few steps when I code this function
    	// 1. Generate a random incident direction according to proj solid angle
        //    pdf is constant 1/pi
    	// 2. Find incident radiance from that direction
    	// 3. Estimate reflected radiance using brdf * radiance / pdf = pi * brdf * radiance
    	
    	Material mat = iRec.surface.getMaterial();
    	BRDF matBRDF = mat.getBRDF(iRec);
    	    					
    	//Point of intersection of the ray with the surface.
   		Point3 P = iRec.frame.o;
   		
   		
    	//Multiple light sources may be present. Iterate through them.
    	ArrayList<PointLight> lights = scene.getPointLights();
   		Iterator<PointLight> iterLight = lights.iterator();
   		
   		int incidentLightCount = lights.size();
   		
   		Color matColor	= new Color();
        Color ambColor	= new Color();
        
        //Ambient lighting.
	   	scene.getBackground().evaluate(outDir,ambColor);
	   	
	   	//Material color
    	matBRDF.evaluate( iRec.frame, incDir , outDir, matColor );
    	
    	//Radiance at this point
    	Color outWeight = new Color();
    	matBRDF.generate(iRec.frame,incDir,outDir,seed,outWeight);
    	outDir.normalize();
    	        	
    	double r = ambColor.r * matColor.r;
    	double g = ambColor.g * matColor.g;
    	double b = ambColor.b * matColor.b;
        	
   		double L_dot_out = 0.0f;
   		   		
   		while( false && iterLight.hasNext() ){
   			PointLight light = iterLight.next();
   			Color lightDiff = light.diffuse;
       		Color lightSpec = light.specular;
       			
   			
   			Vector3 L = new Vector3(	(light.location.x-P.x),
   										(light.location.y-P.y),
   										(light.location.z-P.z));
   			
   			L.normalize();
   			
   			//Make a shadow ray. A ray from the location of incidence to
   			//light source.
   			IntersectionRecord iRecBounce = new IntersectionRecord();
   			Ray rayBounce = new Ray(P,L);
   			rayBounce.makeOffsetRay();
   			//Check for shadows or occlusions.
   			if(scene.getAnyIntersection(iRecBounce,rayBounce)){
   				continue;
   			}
   			
   			
   			L_dot_out = outDir.dot(L);		//Cos theta
   			   			
   			r += lightDiff.r *L_dot_out;
   			g += lightDiff.g *L_dot_out;
   			b += lightDiff.b *L_dot_out;
   		}
   		
   		//Add light from illuminaraires
    	
   		//Radiance from illuminaire.
   		Color outRadiance = new Color(0.0,0.0,0.0);
   		scene.incidentRadiance( P, outDir, outRadiance);

    	double matPDF = Math.PI * matBRDF.pdf(iRec.frame,outDir,incDir);
    	
    	outColor.set(	outWeight.r*r/Math.PI + outRadiance.r*matColor.r*matPDF,
    					outWeight.g*g/Math.PI + outRadiance.g*matColor.g*matPDF,
    					outWeight.b*b/Math.PI + outRadiance.b*matColor.b*matPDF);
    					
    }
    
}
