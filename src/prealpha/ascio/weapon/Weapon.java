package prealpha.ascio.weapon;

import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public abstract class Weapon extends Node{
	
	public Weapon(PhysicsSpace space) {
		//node = space.createDynamicNode();
		
		getLocalTranslation().set(0, .0f, 2.f);
	}
	
	public abstract boolean fire();
	
	public void update(float time) {
		
	}

}
