package prealpha.ascio.weapon;

import prealpha.util.Util;

import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.intersection.CollisionData;
import com.jme.intersection.CollisionResults;
import com.jme.math.*;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsSpace;

public class Sword extends Weapon {
	
	private Swing swing;
	
	CollisionResults result;
	
	public Sword(PhysicsSpace space) {
		super(space);
		
		Box hilt1 = new Box("hilt", Vector3f.ZERO, .15f, .2f, .5f);
		Box hilt2 = new Box("hilt", Vector3f.ZERO, .15f, .5f, .2f);
		Box blade = new Box("blade", Vector3f.UNIT_Z.mult(2.f), .1f, .2f, 1.25f);
		
		attachChild(hilt1);
		attachChild(hilt2);
		attachChild(blade);
		
		MaterialState state = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		state.setAmbient(ColorRGBA.white);
		state.setDiffuse(ColorRGBA.lightGray);
		state.setSpecular(ColorRGBA.lightGray);
		state.setShininess(100);
		
		setRenderState(state);
		updateRenderState();
		
		//node.getLocalRotation().fromAngleAxis(-030*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
		//Util.shout(node.getLocalRotation().toString());
		getLocalRotation().set( -.2f, 0, 0, .97f);
		
		setModelBound(new BoundingBox());
		updateModelBound();		
		
		//node.generatePhysicsGeometry();			
	}	
	
	@Override
	public void update (float time) {		
	}
	
	@Override
	public boolean fire() {
		// TODO Auto-generated method stub
		addController(new Swing(1, this));
		return false;
	}
	
	private class Swing extends SpatialTransformer {
		private static final long serialVersionUID = -8489183257913127409L;

		public Swing(int i, Spatial s) {
			super(i);
			
			// setup the controller
			Quaternion buff1 = new Quaternion();
			Quaternion buff2 = new Quaternion();
			
			setObject( s, 0, -1);
			
			
			Vector3f v1 = s.getLocalTranslation().add(1, 0, 0);
			Quaternion q1 = new Quaternion();
			buff1.fromAngleAxis(-135*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
			buff2.fromAngleAxis(-45*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
			q1 = buff1;
			q1.multLocal(buff2);
			//q1.fromAngleAxis(angle, axis)
			setPosition(0, 0, v1);
			setRotation(0, 0, q1);
			
			Vector3f v2 = s.getLocalTranslation().add(.5f, .0f, 1.5f);
			Quaternion q2 = new Quaternion();
			buff1.fromAngleAxis(30*FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
			buff2.fromAngleAxis(-75*FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
			q2 = buff1;
			q2.multLocal(buff2);
			setPosition(0, .2f, v2);
			setRotation(0, .2f, q2);
			
			Vector3f v3 = s.getLocalTranslation();
			Quaternion q3 = s.getLocalRotation();
			setPosition(0, .5f, v3);
			setRotation(0, .5f, q3);		
			
			interpolateMissing();
		}
		
		@Override
		public void update(float time) {
			// TODO Auto-generated method stub
			super.update(time);
		}
		
	}

}
