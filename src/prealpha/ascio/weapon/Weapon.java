package prealpha.ascio.weapon;

import com.jme.input.AbsoluteMouse;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public abstract class Weapon extends Node{
	
	public Weapon(PhysicsSpace space) {
		//node = space.createDynamicNode();
		
		getLocalTranslation().set(0, .0f, 2.f);
		
		addController( new Turner(this));
	}
	
	public abstract boolean fire();
	
	public void update(float time) {
		
	}

	class Turner extends Controller {

		AbsoluteMouse mouse;
		Node target;
		
		public Turner( Node node) {
			super();
			
			this.target = node;
			mouse = new AbsoluteMouse("turner", DisplaySystem.getDisplaySystem().getHeight(), DisplaySystem.getDisplaySystem().getWidth());
		}
		
		@Override
		public void update(float time) {
			// TODO Auto-generated method stub
			Vector2f foo = new Vector2f(mouse.getHotSpotPosition().x, mouse.getHotSpotPosition().y);
			
			Vector3f bar = DisplaySystem.getDisplaySystem().getWorldCoordinates(foo, 0);
			
			target.lookAt(bar, Vector3f.UNIT_Y);
		}		
	}
}
