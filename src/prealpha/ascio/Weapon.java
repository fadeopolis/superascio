package prealpha.ascio;

import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public abstract class Weapon {
	protected Node node;
	
	public Weapon(PhysicsSpace space) {
		//node = space.createDynamicNode();
		node = new Node();
		
		node.getLocalTranslation().set(0, .0f, 2.f);
	}
	
	public abstract boolean fire();
	
	public void update(float time) {
		
	}
	
	public Node getNode() {
		return node;
	}
}
