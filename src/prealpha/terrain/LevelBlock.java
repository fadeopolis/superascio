package prealpha.terrain;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import prealpha.curve.Curve;
import prealpha.curve.CurveWrapper;
import prealpha.curve.RectifiedCurve;
import prealpha.physics.StaticPhysicsNodeWrapper;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.util.export.xml.XMLImporter;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.StaticPhysicsNode;

public class LevelBlock extends Node implements PhysicsSpatial {
	public TerrainCoordinate coordinate;

	public static Scanner scan;
	
	StaticPhysicsNode physics;
	
	Curve[] curves;
	
	LevelBlock( int x, int y, int z, StaticPhysicsNode physics ) throws IOException {
		//super( "LevelBlock " + x + " " + y + " " + z, physics);
		super();
		this.setName("LevelBlock " + x + " " + y + " " + z);
		this.coordinate = new TerrainCoordinate(x,y,z);
		
		this.setLocalTranslation(100*x, 100*y, 100*z);
		this.physics = physics;
		super.attachChild(physics);
		
		File f = new File("/home/fader/workspace/ascio/data/level/1/"+x+" "+y+" "+z+".jme");
		physics.attachChild((Spatial) XMLImporter.getInstance().load(f));
		//Box b = new Box(x+" "+y+" "+z, new Vector3f(), 50,1, 2.5f);
		//b.setRandomColors();
		//physics.attachChild(b);
		
		curves = new Curve[1];
		Vector3f[] points = new Vector3f[2];
		points[0] = new Vector3f(-47, 2.5f, 0);
		points[1] = new Vector3f(47, 2.5f, 0);
		curves[0] = new RectifiedCurve( "curve", points );
			
		this.physics.setModelBound( new BoundingBox() );
		this.physics.updateModelBound();

		physics.generatePhysicsGeometry();
		
		//CurveWrapper cw = new CurveWrapper(curves[0]);
		//cw.setRandomColors();
		//this.attachChild( cw );
	}

	public void link( LevelBlock lvl ) {
		curves[0].setSuccessor(lvl.getCurve(0));
		System.out.print("THIS\tSUC" + curves[0].getSuccessor() + "\tPRE");
		lvl.getCurve(0).setPredecessor(curves[0]);
		System.out.println(curves[0].getPredecessor());
	}
	
	@Override
	public int attachChild(Spatial s) {	
		return physics.attachChild(s);
	}
	
	public Curve getCurve(int index) {
		return curves[index];
	}

	public void setCurves(Curve curve, int index) {
		this.curves[index] = curve;
	}
/*	
	@Override
	public void setModelBound( BoundingVolume bound ) {
		physics.setModelBound(bound);
	}
*/

	@Override
	public PhysicsNode getPhysicsNode() {
		// TODO Auto-generated method stub
		return physics;
	}
	
}
