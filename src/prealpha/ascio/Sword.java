package prealpha.ascio;

import com.jme.animation.SpatialTransformer;
import com.jme.math.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;

public class Sword extends Weapon {
	public Sword() {
		super();
		
		Box hilt1 = new Box("hilt", Vector3f.ZERO, .2f, .2f, .5f);
		Box hilt2 = new Box("hilt", Vector3f.ZERO, .2f, .5f, .2f);
		Box blade = new Box("blade", Vector3f.UNIT_Z.mult(1.5f), .2f, .2f, .75f);
		
		node.attachChild(hilt1);
		node.attachChild(hilt2);
		node.attachChild(blade);
		
		SpatialTransformer swing = new SpatialTransformer(1);
		System.out.println("HOHAAH");
		swing.setObject(node, 0, -1);
		System.out.println("HOHAAH");
	
	}
	
	@Override
	public void update (float time) {
	}
	
	private class Swing extends Controller {
		private static final long serialVersionUID = -8489183257913127409L;

		@Override
		public void update(float time) {
			// TODO Auto-generated method stub
			node.getLocalTranslation().set(0, 0, 1.5f+time*.1f);
		}
		
	}
}
