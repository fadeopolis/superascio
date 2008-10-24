package prealpha.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;

/**
 * convenience class
 * about every method in here is extremely unefficient, only for development purposes
 * @author fader
 *
 */
public class Util {
	private static final Logger log = Logger.getLogger("Util");
	
	//TODO: maintaining props and sytem objects
	
	public enum Prop {
		Name, Camera, DisplaySystem, Renderer, PhysicsSpace, Floor, RootNode, Scene
		
	}
	
	private static Util instance;
	
	private Map<Prop, Object> props;
	
	private Util() {
		this.props = new EnumMap<Prop, Object>(Prop.class);
	}	
	
	public static Util get() {
		if (instance == null) {
			instance = new Util();
			return instance;
		} else return instance;
	}

	public void putProp(Object prop) {
		if ( prop instanceof Camera ) {
			props.put(Prop.Camera, prop );
		} else if ( prop instanceof DisplaySystem ) {
			props.put(Prop.DisplaySystem, prop );
		} else if ( prop instanceof Renderer ) {
			props.put(Prop.Renderer, prop );
		} else if ( prop instanceof PhysicsSpace ) {
			props.put(Prop.PhysicsSpace, prop );
		} else if ( prop instanceof PhysicsNode ) {
			props.put(Prop.Floor, prop );
		} else if ( prop instanceof Node ) {
			props.put(Prop.RootNode, prop );
		}
	}

	public Object getProp( Prop type ) {
		return props.get(type);
	}
	
	//TODO: static functions
	
	//TODO : rounding 
	
	/**
	 * 
	 * @param f Float to be rounded
	 * @return
	 */
    public static float round(float f ) {
    	f*=1000;
    	f = (int) f;
    	f/=1000;
    	return f;
    }
   
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static float round(float f, int i ) {
    	f *= Math.pow(10, i);
    	f = (int) f;
    	f /= Math.pow(10, i);
    	return f;
    }
  
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static Vector2f round( Vector2f v ) {
    	v.set( round(v.x), round(v.y) );
    	return v;
    }
    
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static Vector2f round( Vector2f v, int i ) {
    	v.set( round(v.x, i), round(v.y, i) );
    	return v;
    }
  
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static Vector3f round( Vector3f v ) {
    	v.set( round(v.x), round(v.y), round(v.z));
    	return v;
    }
    
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static Vector3f round( Vector3f v, int i ) {
    	v.set( round(v.x, i), round(v.y, i), round(v.z, i));
    	return v;
    }
    
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static Quaternion round( Quaternion q ) {
    	q.set( round(q.x), round(q.y), round(q.z), round(q.w));
    	return q;
    }
    
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static Quaternion round( Quaternion q, int i ) {
    	q.set( round(q.x, i), round(q.y, i), round(q.z, i), round(q.w, i));
    	return q;
    }

	/**
	 * 
	 * @param f float[] to be rounded
	 * @return
	 */
    public static float[] round( float[] f ) {
    	for ( int j = 0; j < f.length; j++ ) {
    		f[j] = round(f[j]);
    	}
    	return f;

    }
    
	/**
	 * 
	 * @param f float[] to be rounded
	 * @param i precision
	 * @return
	 */
    public static float[] round( float[] f, int i ) {
    	for ( int j = 0; j < f.length; j++ ) {
    		f[j] = round(f[j], i);
    	}
    	return f;
    }
    
    public static Vector3f clamp( Vector3f input, float min, float max) {
    	FastMath.clamp(input.x, min, max);
    	FastMath.clamp(input.y, min, max);
    	FastMath.clamp(input.z, min, max);
    	
    	return input;
    }
 
    //TODO: random number creation
    
    private static Random rand = new Random();
    
    public static float randomSign() {
    	return rand.nextBoolean() ? -1f : 1f;
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
    
    //TODO: comparison
    
    public static boolean nearEqual( float f1, float f2, float precision ) {
    	return Math.abs( f1 - f2 ) < precision;
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
		return ( Math.abs(v1.x-v2.x) < .1f && Math.abs(v1.y-v2.y) < .1f && Math.abs(v1.z-v2.z) < .1f );
	}
 	
	public static boolean nearEqual( Vector3f v1, Vector3f v2, float precision) {
		return ( Math.abs(v1.x-v2.x) < precision && Math.abs(v1.y-v2.y) < precision && Math.abs(v1.z-v2.z) < precision );
	}
 	
	/**
	 * returns true if all the components differ by no more than .1f
     * @param q1
     * @param q2
     * @return true if the components of q1 and q2 are nearly equal
     */
 	public static boolean nearEqual( Quaternion q1, Quaternion q2) {
		return ( Math.abs(q1.x-q2.x) < .1f && Math.abs(q1.y-q2.y) < .1f && Math.abs(q1.z-q2.z) < .1f && Math.abs(q1.w-q2.w) < .1f );
	}
 	
	public static boolean nearEqual( Quaternion q1, Quaternion q2, float precision) {
		return ( Math.abs(q1.x-q2.x) < precision && Math.abs(q1.y-q2.y) < precision && Math.abs(q1.z-q2.z) < precision && Math.abs(q1.w-q2.w) < precision );
	}
 	
	
	public static boolean nearEqual( float[] f1, float[] f2 ) {
		if ( f1.length != f2.length ) return false;
		for ( int i = 0; i < f1.length; i++ ) 
			if ( Math.abs( f1[i] - f2[i] ) >= .1f ) return false;
		
		return true;
	}

	public static boolean nearEqual( float[] f1, float[] f2, float precision ) {
		if ( f1.length != f2.length ) return false;
		for ( int i = 0; i < f1.length; i++ ) 
			if ( Math.abs( f1[i] - f2[i] ) >= precision ) return false;
		
		return true;
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
    
 	//TODO: misc
 	
	private static final BigDecimal one = new BigDecimal("1");
	private static final BigDecimal two = new BigDecimal("2");
	
	/**
	 * Use this to test how the game behaves with a big load on CPU elsewhere ( a seperate thread )
	 * @param precision
	 * @param c
	 * @return
	 * @throws InterruptedException 
	 */
	public static BigDecimal computePi( int precision, MathContext c ) throws InterruptedException {
		
		class PieBaker extends Thread {

			Object[] store;
			int precision;
			MathContext c;
			
			PieBaker( int precision, MathContext c, Object[] store) {
				this.precision = precision;
				this.c = c;
				this.store = store;
			}
			
			@Override
			public void run() {
				if ( c == null ) c = new MathContext( 5000, RoundingMode.HALF_EVEN );
				precision = Math.abs(precision);
				
				BigDecimal[] b = new BigDecimal[precision+1];
				for ( int i = 0; i < b.length; i++ ) {
					b[i] = one.divide( new BigDecimal(precision), c).multiply( new BigDecimal(i, c), c);
				}
				
				BigDecimal[] v = new BigDecimal[precision+1];
				for ( int i = 0; i < v.length; i++) {
					v[i] = one.divide( one.add( b[i].pow( 2, c ) , c ) , c );
				}
				
				BigDecimal sum = new BigDecimal("0", c);
				for ( int i = 0; i < v.length; i++) { 
					if ( i == 0 || i == precision ) {
						sum = sum.add( v[i], c );
					} else {
						sum = sum.add( v[i].multiply( two , c ), c );
					}
				}
				
				BigDecimal multiplicand = one.divide( two.multiply( new BigDecimal( precision, c), c ) , c );
				BigDecimal pi = multiplicand.multiply( sum, c );
				pi = pi.multiply( two , c );
				pi = pi.multiply( two , c );	
				store[0] = pi;
			}
			
			BigDecimal getPi() {
				return (BigDecimal) store[0];
			}
		}
		
		Object[] o = new Object[1];
		PieBaker pb = new PieBaker( precision, c, o );
		pb.start();
		
		pb.join();
		o[0] = pb.getPi();
		
		return (BigDecimal) o[0];
	}
	
	//TODO: basic message output
	
 	public static void shoutln(Object... o) {
 		if ( o .length > 0 ) {
 	 		String s = from(o[0]);
 	 		
 	 		for ( int i = 1; i < o.length; i++ ) {
 	 	 			s += "\t" + from(o[i]);
 	 		}
 	 		
 	 		System.out.println(s);
 		} else System.out.println();
 	}
 		
 	public static void shout(Object... o) {
 		if ( o .length > 0 ) {
 	 		String s = from(o[0]);
 	 		
 	 		for ( int i = 1; i < o.length; i++ ) {
 	 	 			s += "\t" + from(o[i]);
 	 		}
 	 		
 	 		System.out.print(s);
 		}
 	}
 	
	@SuppressWarnings("unchecked")
 	private static String from( Object o ) {
 		if ( o instanceof Quaternion) {
 			Quaternion q = round((Quaternion) o);
 			return "Quaternion [x=" + q.x + " y=" + q.y + " z=" + q.z + " w=" + q.w + "]";
 		} else if ( o instanceof Vector3f) {
 			Vector3f v = round((Vector3f) o);
 			return "Vector3f [x=" + v.x + " y=" + v.y + " z=" + v.z + "]";
 		} else if ( o instanceof Vector2f) {
 			Vector2f v = round((Vector2f) o);
 			return "Vector2f [x=" + v.x + " y=" + v.y + "]";
 		} else if ( o instanceof float[] ) {
 			float[] f = round((float[]) o);
 			return Arrays.toString(f);
 		} else {
 			return o.toString();
 		}
 	}
 	
 	public static void log( String msg ) {
 		log.info(msg);
 	}
 	
}
