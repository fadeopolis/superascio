package prealpha.ascio;

import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public abstract class Weapon {
	protected DynamicPhysicsNode node;
	
	public Weapon(PhysicsSpace space) {
		node = space.createDynamicNode();
		node.getLocalTranslation().set(0, 0, 2.5f);
	}
	
	public abstract boolean fire();
	
	public void update(float time) {
		
	}
	
	public DynamicPhysicsNode getNode() {
		return node;
	}
}
