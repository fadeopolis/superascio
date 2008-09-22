package prealpha.curve;

import com.jme.intersection.CollisionResults;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.util.geom.BufferUtils;
import com.jme.curve.*;

public class CatmullRomCurve extends Curve {

	private static final long serialVersionUID = 1L;

	private Vector3f vbuff0;
  	private Vector3f vbuff1;
  	private Vector3f vbuff2;
  	private Vector3f vbuff3;
  	private Vector3f vbuff4;

  	private Vector3f start;
  	private Vector3f end;

  	private float partPercentage;
  	private float tension;

  	private Vector3f first;
  	private Vector3f second;
  	private Vector3f last;
  	private Vector3f beforeLast;

  	private Quaternion q = new Quaternion();
  
  /**
   	* Constructor instantiates a new <code>CatmullRomCurve</code> object. The control points that define the curve are
   * supplied.
   * 
   * @param name the name of the scene element. This is required for identification and comparision purposes.
   * @param samplingPoints the points that define the curve.
   */
  	public CatmullRomCurve(String name, Vector3f[] samplingPoints ) {		
  		this(name, samplingPoints, .5f);
  	}
  	
  	public CatmullRomCurve( String name, Vector3f[] samplingPoints, float tension ) {
  		this(name, samplingPoints, tension, null, null);
  	}

  	public CatmullRomCurve( String name, Vector3f[] samplingPoints, float tension, Vector3f start, Vector3f end ) {
  		super(name, samplingPoints);
  	    
  		this.tension = tension;
  		
  		vbuff0 = new Vector3f();
  		vbuff1 = new Vector3f();
  		vbuff2 = new Vector3f();
  		vbuff3 = new Vector3f();
  		vbuff4 = new Vector3f();
  		
  		float scale = .1f;
  			
  		if ( start == null ) {
  			vbuff0 = samplingPoints[0];
  			vbuff0.subtractLocal(samplingPoints[1]);
  			vbuff0.normalizeLocal();
  			vbuff0.multLocal(scale);
  			
  			this.start = samplingPoints[0].add(vbuff0);	
 		} else {
  			this.start = start;
  		}
  		if ( end == null ) {
  			int i = samplingPoints.length -1;	
  			vbuff0 = samplingPoints[i].subtract(samplingPoints[i-1]);
  			vbuff0.normalizeLocal();
  			vbuff0.multLocal(scale);
  			
  			this.end = samplingPoints[i].add(vbuff0);
 		} else {
  			this.end = end;
  		}
  					
  		partPercentage = 1.0f / (numberOfSamplingPoints - 1);
  		
  		//calculate length
  		length = 0;
  		float calcStep = 1f / ((float)((numberOfSamplingPoints - 1)*100));
  		Vector3f old = new Vector3f();
  		getPoint(0, old); 
  		Vector3f next = new Vector3f();
  			
  		for ( float f = 0; f < 1f; f += calcStep) {
  			old = getPoint(f);
  			next = getPoint(f+calcStep);
  			length += old.distance(next);
  		} 	
  	}
  	 /**
   	* <code>getPoint</code> calculates a point on a Catmull-Rom curve from a given time value within the interval [0,
   * 1]. If the value is zero or less, the first control point is returned. If the value is one or more, the last
   * control point is returned. Using the equation of a Catmull-Rom Curve, the point at the interval is calculated and
   * returned.
   * 
   * @see com.jme.curve.Curve#getPoint(float)
   */
  	@Override
  	public Vector3f getPoint(float time, Vector3f store) {
  		// first point
  		if (time <= 0) {
  			BufferUtils.populateFromBuffer(store, samplingPoints, 0);
  			return store;
  		}
  		// last point.
  		if (time >= 1) {
  			BufferUtils.populateFromBuffer(store, samplingPoints, numberOfSamplingPoints - 1);
  			return store;
  		}

  		float timeBetween = time / this.partPercentage;
  		int firstPointIndex = ((int)FastMath.floor(timeBetween));
/*  		
  		BufferUtils.populateFromBuffer(vbuff0, samplingPoints, 0);
  		BufferUtils.populateFromBuffer(vbuff1, samplingPoints, 1);
  		BufferUtils.populateFromBuffer(vbuff2, samplingPoints, 2);
  		BufferUtils.populateFromBuffer(vbuff3, samplingPoints, 3);
  		
  		return interpolate(time, vbuff0, vbuff1, vbuff2, vbuff3, .5f);*/
  		
  		time = timeBetween - firstPointIndex;

  		firstPointIndex--;
  		
  		if (firstPointIndex == -1) {
  			vbuff1 = start;
  		} else {
  			BufferUtils.populateFromBuffer(vbuff1, samplingPoints, firstPointIndex);
  		}

  		BufferUtils.populateFromBuffer(vbuff2, samplingPoints, ++firstPointIndex);
  		BufferUtils.populateFromBuffer(vbuff3, samplingPoints, ++firstPointIndex);

  		if (++firstPointIndex == numberOfSamplingPoints) {
  			vbuff4 = end;
  		} else {
  			BufferUtils.populateFromBuffer(vbuff4, samplingPoints, firstPointIndex);
  		}
    
  		store.zero();
  		interpolate( time, vbuff1, vbuff2, vbuff3, vbuff4, tension, store );
  		return store;
  }
  	
  	@Override
  	public Vector3f getPointByLength(float arclength, Vector3f store) {
  		// TODO Auto-generated method stub
		return getPoint( arclength/length, store);
  	}
  	
  	/**
  	 *  The original CatmullRom Interpolation between 2 points, with 2 points for "guidance"
  	 *  
  	 * @param time a number from [0,1]
  	 * @param p0 1 "guidance" point
  	 * @param p1 interpolation starts here
  	 * @param p2 interpolation stops here
  	 * @param p3 2 "guidance" point
  	 * @param tension parameter that influences the sharpness of the curves bends
  	 * @param store the result is stored in this vector
  	 * @return
  	 */
  	private Vector3f interpolate( float time, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float tension , Vector3f store ) {		
  		float t1;
  		if ( time == 0 ) t1 = .5f; else t1 = time;
  		float t2 = t1*time;
  		float t3 = t2*time;
  		
  		// formula
  		// f(t) = tension * ((2 * p1) + (p2 - p0)*t + (2*p0 - 5*p1 + 4*p2 - p3)*t*t + (3*p1 + p3 - p0 - 3*p2)*t*t*t)
  		float x = tension * ((2 * p1.x) + (p2.x - p0.x)*t1 + (2*p0.x - 5*p1.x + 4*p2.x - p3.x)*t2 + (3*p1.x + p3.x - p0.x - 3*p2.x)*t3);
  		float y = tension * ((2 * p1.y) + (p2.y - p0.y)*t1 + (2*p0.y - 5*p1.y + 4*p2.y - p3.y)*t2 + (3*p1.y + p3.y - p0.y - 3*p2.y)*t3);
  		float z = tension * ((2 * p1.z) + (p2.z - p0.z)*t1 + (2*p0.z - 5*p1.z + 4*p2.z - p3.z)*t2 + (3*p1.z + p3.z - p0.z - 3*p2.z)*t3);
  		
  		store.set(x, y, z);
  		return store;
  	}

}