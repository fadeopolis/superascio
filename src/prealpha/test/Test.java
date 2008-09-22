package prealpha.test;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jmex.physics.PhysicsSpace;

import prealpha.curve.CatmullRomCurve;
import prealpha.curve.Curve;
import prealpha.curve.RectifiedCurve;
import prealpha.util.Util;

public class Test {
	public static void main(String[] args) {
		PhysicsSpace space = PhysicsSpace.create();
	    
	    
	    int numberOfPoints = 11;
	    int pointRange = 100;
		Vector3f[] points = new Vector3f[numberOfPoints ];
	    
		for ( int i = 0; i < numberOfPoints; i++) {	    	
			points[i] = new Vector3f(  Util.randomInt(pointRange, true),  Util.randomInt(pointRange, true),  Util.randomInt(pointRange, true));
	    }
	    /*
	    points[0] = new Vector3f(0,0,0);
	    points[1] = new Vector3f(1,0,0);
	    points[2] = new Vector3f(2,0,0);
	    points[3] = new Vector3f(3,0,0);
	    points[4] = new Vector3f(4,0,0);
	    points[5] = new Vector3f(5,0,0);
	    points[6] = new Vector3f(6,0,0);
	    points[7] = new Vector3f(7,0,0);
	    points[8] = new Vector3f(8,0,0);
	    points[9] = new Vector3f(9,0,0);
	    points[10] = new Vector3f(10,0,0);
	    */
	    Curve c = new CatmullRomCurve("", points);
	    Curve z = new RectifiedCurve("", points);
	    System.out.println(c.getLength() + "\t" + z.getLength());
	    for ( int i = 1; i < numberOfPoints; i+=1) {	 
	    	Curve d = new RectifiedCurve(c, i-1);
	    	Curve e = new RectifiedCurve(c, i);
	    	System.out.println(i + "\t" + d.getLength() +"\t" + c.getLength() + "\t" + (d.getLength()-e.getLength())+"\t" + (d.getLength()-c.getLength()));
	    }
	}
}

class TestWrapper<E extends WrapMe> extends WrapMe {
	
	E e;
	
	public TestWrapper( E e) {
		this.e = e;
	}
	
	public int wrap(int i) {
		return e.wrap(i);
	}
}

class SonOfTestWrapper<E extends SonOfWrapMe> extends TestWrapper {
	
//	E e;
	
	public SonOfTestWrapper( E e) {
		super(e);
//		this.e = e;
	}
	
	public int wrap(int i) {
		return super.e.wrap(i);
	}
	
	public void wrapTwo(int i) {
//		e.wrapTwo(s + " Wrapper");
//		super.e.wrap(s);
	}
	
	public Object test() {
		return super.e;
	}
}

class WrapMe {
	protected int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int wrap(int i) {
		System.out.println(i);
		id = (i);
		return id;
	}
}

class SonOfWrapMe extends WrapMe {
	
	float id;
	
	public int wrap(int i) {
		return super.wrap(i);
	}
	
	public float wrapTwo(int i) {
		System.out.println(i);
		id = (i);
		return id;
	}
}