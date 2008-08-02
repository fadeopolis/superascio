package prealpha.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

import prealpha.ascio.Ascio;

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
	
	public enum PropType { Ascio, Camera, DisplaySystem, Renderer, PhysicsSpace};
	
	private static Util instance;

	public static PhysicsSpace space = PhysicsSpace.create();
	
	private HashMap<PropType, Object> props;
	
	private Util() {
		this(null);
	}	
	private Util(HashMap<PropType, Object> props) {
		if (props == null ) {
			this.props = new HashMap<PropType, Object>();
		} else {
			this.props = props;
		}
	}

	public static void create() {
		create(null);
	}
	public static void create(HashMap<PropType, Object> props) {
		instance = new Util(props);
	}
	
	public static Util util() {
		if (instance == null) {
			instance = new Util();
			return instance;
		} else return instance;
	}
	
	public static boolean check() {
		boolean stat = true;
		if (instance == null) {
			System.out.println("No Instance of Util exists");
			return false;
		}
		if (instance.props.get(PropType.Ascio) == null) {
			System.out.println("No Ascio in props");
			stat = false;
		}
		if (instance.props.get(PropType.Camera) == null) {
			System.out.println("No Camera in props");
			stat = false;
		}
		if (instance.props.get(PropType.DisplaySystem) == null) {
			System.out.println("No DisplaySystem in props");
			stat = false;
		}
		if (instance.props.get(PropType.PhysicsSpace) == null) {
			System.out.println("No PhysicsSpace in props");
			stat = false;
		}
		if (instance.props.get(PropType.Renderer) == null) {
			System.out.println("No Renderer in props");
			stat = false;
		}
		return stat;
	}
	
	public Object getProp(PropType type) {
		return props.get(type);
	}
	
	public Object putProp(Object prop) {
		PropType type; 
		if ( prop instanceof Ascio ) {
			type = PropType.Ascio;
		} else if ( prop instanceof Camera ) {
			type = PropType.Camera;
		} else if ( prop instanceof DisplaySystem ) {
			type = PropType.DisplaySystem;
		} else if ( prop instanceof Renderer ) {
			type = PropType.Renderer;
		} else if ( prop instanceof PhysicsSpace ) {
			type = PropType.PhysicsSpace;
		} else {
			return new String("FUCK");
		}
		return props.put(type, prop);
	}
	
	/**
	 * 
	 * @param f Float to be rounded
	 * @param i precision
	 * @return
	 */
    public static float round(float f) {
    	f*=1000;
    	f = (int) f;
    	f/=1000;
    	return f;
    }
    
    public static int randomInt ( int range, boolean negativePossible) {
    	return negativePossible ? ( rand.nextBoolean() ? rand.nextInt(range) : rand.nextInt(range)*-1 ) : ( rand.nextInt(range) );
    }
    
    /**
     * returns true if all the components differ by no more than .1f
     * @param v1
     * @param v2
     * @return true if the components of v1 and v2 are nearly equal
     */
 	public static boolean nearEqual( Vector3f v1, Vector3f v2) {
		if (v1.x-v2.x<.1f && v1.y-v2.y<.1f && v1.z-v2.z<.1f) return true;
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
    
    public static void main(String[] args) {
		}
}
