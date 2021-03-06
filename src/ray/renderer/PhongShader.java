package ray.renderer;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;

import ray.misc.Color;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;
import ray.misc.IntersectionRecord;
import ray.light.PointLight;
import ray.material.Material;

public class PhongShader implements Renderer {
    
    private double phongCoeff = 1.5;
    
    public PhongShader() { }
    
    public void setAlpha(double a) {
        phongCoeff = a;
    }
    
    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
            int sampleIndex, Color outColor) {
        // W4160 TODO (A)
        // Here you need to implement the basic phong reflection model to calculate
        // the color value (radiance) along the given ray. The output color value 
        // is stored in outColor. 
        // 
        // For such a simple rendering algorithm, you might not need Monte Carlo integration
        // In this case, you can ignore the input variable, sampler and sampleIndex.
        
        //Find the ray intersection with any surface
        IntersectionRecord iRec = new IntersectionRecord();
        
        if(scene.getFirstIntersection(iRec,ray)){
        	
        	//Get the normal to the surface of intersection with the ray.
        	Vector3 tempW = iRec.frame.w;
       		Vector3 N = new Vector3(tempW.x,tempW.y,tempW.z);
       		
       		Vector3 V = ray.direction;
       		
        	//Get the BRDF of the material at the point of intersection.
        	Material mat = iRec.surface.getMaterial();
        	Color matColor		= new Color();
        	Color ambColor	= new Color();
        	
        	scene.getBackground().evaluate(ray.direction,ambColor);
        	mat.getBRDF(iRec).evaluate( iRec.frame, N , N, matColor );
        	        	
        	double r = ambColor.r * matColor.r;
        	double g = ambColor.g * matColor.g;
        	double b = ambColor.b * matColor.b;
        	       		
       		//Point of intersection of the ray with the surface.
       		Point3 P = iRec.frame.o;
       		
        	//Multiple light sources may be present. Iterate through them.
        	ArrayList<PointLight> lights = scene.getPointLights();
       		Iterator<PointLight> itLight = lights.iterator();
       		
       		int incidentLightCount = lights.size();
       		
       		while( itLight.hasNext() ){
       			PointLight light = itLight.next();
       			Color lightDiff = light.diffuse;
       			Color lightSpec = light.specular;
       			
       			Vector3 L = new Vector3(	light.location.x - P.x,
       										light.location.y - P.y,
       										light.location.z - P.z);
       			
       			L.normalize();
       			
       			double L_dot_N = L.dot(N);
       	      	
       	      	if(L_dot_N < 0){
       				incidentLightCount--;
       				continue;
       			}
       			
       			
       			//Diffuse component.
       			r += L_dot_N * matColor.r * lightDiff.r;
       			g += L_dot_N * matColor.g * lightDiff.g;
       			b += L_dot_N * matColor.b * lightDiff.b;
       			
       			
       			
       			//Specular component?
       			Vector3 R = new Vector3(-L.x + L_dot_N*2*N.x, 
       									-L.y + L_dot_N*2*N.y, 
       									-L.z + L_dot_N*2*N.z);
       			R.normalize();
       			
       			double R_dot_V = Math.max( 0 , -R.dot(V) );
       			
       			
       			r += Math.pow( R_dot_V, phongCoeff) * matColor.r * lightSpec.r;
       			g += Math.pow( R_dot_V, phongCoeff) * matColor.g * lightSpec.g;
       			b += Math.pow( R_dot_V, phongCoeff) * matColor.b * lightSpec.b;
       			
       				
       		}
			
			//Check to see if any light sources hit the surface. If not, the color
			//at that point is the color of the background.
			outColor.set( r, g, b);
			
        	return;
        }
        
        scene.getBackground().evaluate(ray.direction,outColor);
    }
}
