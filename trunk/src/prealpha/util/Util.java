package prealpha.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsSpace;

/**
 * convenience class
 * about every method in here is extremely unefficient, only for development purposes
 * @author fader
 *
 */
public class Util {
	public static Random rand = new Random();
	
	public enum Prop {
		Ascio {
			@Override
			public Object get() {
				return prop;
			}
			
			public void put(Object o) {
				this.prop = o;
			}
		}
		, Camera{
			@Override
			public Camera get() {
				return (Camera) prop;
			}
			public void put(Camera c) {
				this.prop = c;
			}
		}
		, DisplaySystem{
			@Override
			public DisplaySystem get() {
				return (DisplaySystem) prop;
			}
			public void put(DisplaySystem d) {
				this.prop = d;
			}
		}
		, Renderer{
			@Override
			public Renderer get() {
				return (Renderer) prop;
			}
			public void put(Renderer r) {
				this.prop = r;
			}
		}
		, PhysicsSpace{
			@Override
			public PhysicsSpace get() {
				return (PhysicsSpace) prop;
			}
			public void put(PhysicsSpace s) {
				this.prop = s;
			}
		}
		, RootNode{
			@Override
			public Node get() {
				return (Node) prop;
			}
			public void put(Node n) {
				this.prop = n;
			}
		}
		;

		
		Object prop;
		
		public Object get() {
			return prop;
		}
		
		public void put( Object prop ) {
			this.prop = prop;
		}
	};
	
	private static Util instance;
	
	private Util() {
		this(null);
	}	
	
	private Util(Object... props) {
		if ( props != null ) {
			for ( Object o : props ) {
				putProp(o);
			}
		}
	}
	
	public static Util get() {
		if (instance == null) {
			instance = new Util();
			return instance;
		} else return instance;
	}
	
	public void putProp(Object prop) {
		/*
		if ( prop instanceof Ascio ) {
			type = PropType.Ascio;
		} else*/ if ( prop instanceof Camera ) {
			Prop.Camera.put(prop);
		} else if ( prop instanceof DisplaySystem ) {
			Prop.DisplaySystem.put(prop);
		} else if ( prop instanceof Renderer ) {
			Prop.DisplaySystem.put(prop);
		} else if ( prop instanceof PhysicsSpace ) {
			Prop.DisplaySystem.put(prop);
		} else if ( prop instanceof Node ) {
			Prop.DisplaySystem.put(prop);
		}
	}
	
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static float round(float f ) {
    	f*=1000;
    	f = (int) f;
    	f/=1000;
    	return f;
    }
    
    public static Vector3f round( Vector3f v ) {
    	v.set( round(v.x), round(v.y), round(v.z));
    	return v;
    }
    
    public static int randomInt ( int range) {
    	return rand.nextInt(range);
    }
    
    public static int randomInt ( int range, boolean negativePossible) {
    	if (negativePossible) {
    		return rand.nextBoolean() ? rand.nextInt(range) : rand.nextInt(range)*-1;
    	} else {
    		return rand.nextInt(range);
    	}
    }
 
    public static int randomInt ( int rangeMax, int rangeMin, boolean negativePossible) {
    	if (negativePossible) {
    		return rand.nextBoolean() ? (rand.nextInt()+rangeMin)%rangeMax : ((rand.nextInt()+rangeMin)%rangeMax)*-1;
    	} else {
    		return (rand.nextInt()+rangeMin)%rangeMax;
    	}
    }
    
    public static float randomFloat ( float range) {
    	return rand.nextFloat()*range;
    }
    
    public static float randomFloat ( float range, boolean negativePossible) {
    	if (negativePossible) {
    		return rand.nextBoolean() ? rand.nextFloat()*range : rand.nextFloat()*-range;
    	} else {
    		return rand.nextFloat()*range;
    	}
    }
 
    public static float randomFloat ( float rangeMax, float rangeMin, boolean negativePossible) {
    	if (negativePossible) {
    		return rand.nextBoolean() ? (rand.nextFloat()*rangeMax + rangeMin)%(rangeMax + Float.MIN_VALUE) : ((rand.nextFloat()*rangeMax + rangeMin)%(rangeMax + Float.MIN_VALUE))*-1;
    	} else {    		    
    		return (rand.nextFloat()*rangeMax + rangeMin)%(rangeMax + Float.MIN_VALUE);
    	}
    }
    
    public static Vector3f clamp( Vector3f input, float min, float max) {
    	FastMath.clamp(input.x, min, max);
    	FastMath.clamp(input.y, min, max);
    	FastMath.clamp(input.z, min, max);
    	
    	return input;
    }
    
    public static boolean nearEqual( float f1, float f2 ) {
    	return Math.abs( f1 - f2 ) < .1f;
    }
    
    /**
     * returns true if all the components differ by no more than .1f
     * @param v1
     * @param v2
     * @return true if the components of v1 and v2 are nearly equal
     */
 	public static boolean nearEqual( Vector3f v1, Vector3f v2) {
 		float precision = .1f;
		if ( Math.abs(v1.x-v2.x)<precision && Math.abs(v1.y-v2.y)<precision && Math.abs(v1.z-v2.z)<precision ) return true;
		return false;
	}
 	
	public static boolean nearEqual( Vector3f v1, Vector3f v2, float precision) {
		if ( Math.abs(v1.x-v2.x)<precision && Math.abs(v1.y-v2.y)<precision && Math.abs(v1.z-v2.z)<precision ) return true;
		return false;
	}
 	
 	/**
 	 * 
 	 * @param s1
 	 * @param s2
 	 * @return
 	 */
 	public static boolean close( Spatial s1, Spatial s2) {
 		return nearEqual(s1.getWorldTranslation(), s2.getWorldTranslation());
 	}
 	
 	public static boolean closeAndSamePlane( Spatial s1, Spatial s2) {
 		return false;
 	}
 
 	public static float howSimilar( Vector3f v1, Vector3f v2 ) {
		return ( (v1.x-v2.x)*(v1.y-v2.y)*(v1.z-v2.z) )/3;
	}
    
 	public static void shout(Object o) {
 		try {
 			System.out.println(o.toString());
 		} catch (Exception e) {
 			System.out.println(o);
 		}
 	}
 	
    public static void main(String[] args) {
    	
	}
}
