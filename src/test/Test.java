package test;

import prealpha.character.Character;
import prealpha.character.pc.Ascio;
import prealpha.character.pc.PlayerCharacter;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

import prealpha.core.DynamicPhysicsSpatial;
import prealpha.core.Entity;
import prealpha.util.Util;

public class Test {

	static Quaternion facingForward = new Quaternion();
	static Quaternion facingBackward = new Quaternion();
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		Quaternion[] q = new Quaternion[15];
		
		for ( int i = 0; i < q.length; i++ ) 
			q[i] = new Quaternion(Util.randomFloat(1, true),0,Util.randomFloat(1, true),Util.randomFloat(1, true));
		
		
		float[][] f = new float[q.length][3];
		Vector3f[][] v = new Vector3f[q.length][3];

		for ( int i = 0; i < q.length; i++ ) {
			q[i].toAxes(v[i]);
			q[i].toAngles(f[i]);
		}
		
		q[0].fromAngleNormalAxis(180*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
//		q[1].set(0,.2f,0,1);
//		q[2].set(0,.3f,0,1);
//		q[3].set(.1f,.2f,.3f,1);
//		q[4].set(0,2,0,.1f);

		Util.shoutln( from(q[0]) + "\t" + from(f[0]) + "\n" + from(v[0][0]) + "\t" + from(v[0][1]) + "\t" + from(v[0][2]) + "\n" );
	}
	
	public static String from( Quaternion[] q, float[][] f ) {
		String s = new String();
		for ( int i = 0; i < q.length; i++ )  {
			s += from(q[i]) + "\t" + from(f[i]) + "\n\n";
		}
		return s;
	}
	
	public static String from( Vector3f v ) {
		return "[x= " + from(v.x) + ", y= " + from(v.y) + ", z= " + from(v.z) + "]";
	}
	
	public static String from( Quaternion q ) {
		return "[x= " + from(q.x) + ", y= " + from(q.y) + ", z= " + from(q.z) + ", w= " + from(q.w) + "]";
	}
	
	public static String from( float[] f ) {
		return "[x= " + from(f[0]*FastMath.RAD_TO_DEG) + ", y= " + from(f[1]*FastMath.RAD_TO_DEG) + ", z= " + from(f[2]*FastMath.RAD_TO_DEG) + "]";
	}
	
	public static String from( float f ) {
		return ((f < 0) ? "" : "+")  + Util.round(f,5);
	}
}