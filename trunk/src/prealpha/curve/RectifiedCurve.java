package prealpha.curve;

import java.nio.FloatBuffer;

import prealpha.curve.Curve;
import com.jme.intersection.CollisionResults;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.geom.BufferUtils;

/**
 * Connects a set of Points with straight lines.
 * 
 * @author fader
 *
 */

public class RectifiedCurve extends Curve {

	// tells you how far along the interval [0,1] a sampling point is
	private float[] traversedLengthAtPoint;
	
	Vector3f buff;
	Vector3f buff1 = new Vector3f();
	Vector3f buff2 = new Vector3f();

	public RectifiedCurve(String name, Vector3f[] samplingPoints) {
		super(name, samplingPoints);
		length = 0;
		buff = new Vector3f();
		
		traversedLengthAtPoint = new float[samplingPoints.length];
		
		for (int i = 1; i < samplingPoints.length; i++) {
			length += samplingPoints[i-1].subtract(samplingPoints[i]).length();
			traversedLengthAtPoint[i] = length;
		}
		
		for (int i = 0; i < traversedLengthAtPoint.length; i++) {
			traversedLengthAtPoint[i] /= length;
		}
	}

	public RectifiedCurve( Curve curve, int precision) {
		super( curve.name );
		
		if ( precision == 0) precision++;
				
		this.numberOfSamplingPoints = curve.numberOfSamplingPoints*precision;
			
		Vector3f[] v = new Vector3f[this.numberOfSamplingPoints];
		float speed = 1f / (this.numberOfSamplingPoints -1);
		
		for ( float i = 0; i < this.numberOfSamplingPoints; i++) {
			v[(int) i] = curve.getPoint(i*speed);
		}
		
		length = 0;
		buff = new Vector3f();
		
		traversedLengthAtPoint = new float[this.numberOfSamplingPoints];
		
		for (int i = 1; i < this.numberOfSamplingPoints; i++) {		
			length += v[i-1].subtract(v[i]).length();
			traversedLengthAtPoint[i] = length;
		}
		
		for (int i = 0; i < this.numberOfSamplingPoints; i++) {
			traversedLengthAtPoint[i] /= length;
		}
		
		this.samplingPoints = BufferUtils.createFloatBuffer(v);
	}
	
	@Override
	public Vector3f getPoint(float time, Vector3f store) {
		// first point
		if (time < 0) {
		    BufferUtils.populateFromBuffer(store, samplingPoints, 0);
			return store;
		}
		//last point.
		if (time > 1) {
		    BufferUtils.populateFromBuffer(store, samplingPoints, numberOfSamplingPoints - 1);
			return store;
		}
		
		int foo = 1;		
		while ( traversedLengthAtPoint[foo] < time) foo++;	
		
		BufferUtils.populateFromBuffer(store, samplingPoints, foo-1);
		BufferUtils.populateFromBuffer(buff, samplingPoints, foo);
		
		store.interpolate(buff, (time - traversedLengthAtPoint[foo-1]) / (traversedLengthAtPoint[foo] - traversedLengthAtPoint[foo-1]) );
		return store;
	}

	@Override
	public Vector3f getPointByLength(float arclength, Vector3f store) {
		// TODO Auto-generated method stub
		return getPoint( arclength/length, store);
	}

}