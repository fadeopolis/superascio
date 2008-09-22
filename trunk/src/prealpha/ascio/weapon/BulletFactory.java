package prealpha.ascio.weapon;

import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

public class BulletFactory {
	PhysicsSpace space;
	
	public BulletFactory( PhysicsSpace space ) {
		this.space = space;
	}
	
	public DynamicPhysicsNode createBullet() {
		return space.createDynamicNode();
	}
}
