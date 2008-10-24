package prealpha.core;

import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpatial;

public interface DynamicPhysicsSpatial extends PhysicsSpatial {

    /**
     * @return the PhysicsNode the collision geometry belongs to, or the node itself if this is a node
     */
	public DynamicPhysicsNode getPhysicsNode();

}
