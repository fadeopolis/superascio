package prealpha.ascio;

import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.math.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jmex.physics.PhysicsSpace;

public class Sword extends Weapon {
	public Sword(PhysicsSpace space) {
		super(space);
		
		Box hilt1 = new Box("hilt", Vector3f.ZERO, .2f, .2f, .5f);
		Box hilt2 = new Box("hilt", Vector3f.ZERO, .2f, .5f, .2f);
		Box blade = new Box("blade", Vector3f.UNIT_Z.mult(1.5f), .2f, .2f, .75f);
		
		node.attachChild(hilt1);
		node.attachChild(hilt2);
		node.attachChild(blade);
		
		node.setModelBound(new BoundingBox());
		node.updateModelBound();
		
		node.generatePhysicsGeometry();
			
	}
	
	@Override
	public void update (float time) {		
	}
	
	@Override
	public boolean fire() {
		// TODO Auto-generated method stub
		node.getLocalTranslation().addLocal(0, 1, 1);
		
		Quaternion buff1 = new Quaternion();
		Quaternion buff2 = new Quaternion();
		
		SpatialTransformer swing = new SpatialTransformer(1);
		System.out.println("HOHAAH");
		swing.setObject(node, 0, -1);
		
		Vector3f v1 = new Vector3f(-1, 2, 1.5f);
		Quaternion q1 = new Quaternion();
		buff1.fromAngleAxis(-135*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
		buff2.fromAngleAxis(-45*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
		q1 = buff1;
		q1.multLocal(buff2);
		//q1.fromAngleAxis(angle, axis)
		swing.setPosition(0, 0, v1);
		swing.setRotation(0, 0, q1);
		
		Vector3f v2 = new Vector3f(.5f, .0f, 1.5f);
		Quaternion q2 = new Quaternion();
		buff1.fromAngleAxis(30*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
		buff2.fromAngleAxis(-75*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
		q2 = buff1;
		q2.multLocal(buff2);
		swing.setPosition(0, .5f, v2);
		swing.setRotation(0, .5f, q2);
		
		swing.interpolateMissing();		
		node.addController(swing);
		return false;
	}
	
	private class Swing extends Controller {
		private static final long serialVersionUID = -8489183257913127409L;

		public Swing() {
			
		}
		@Override
		public void update(float time) {
			// TODO Auto-generated method stub
			
	 
		}
		
	}

}
