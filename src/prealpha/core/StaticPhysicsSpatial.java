package prealpha.core;

import com.jmex.physics.PhysicsSpatial;
import com.jmex.physics.StaticPhysicsNode;

public interface StaticPhysicsSpatial extends PhysicsSpatial {

    /**
     * @return the PhysicsNode the collision geometry belongs to, or the node itself if this is a node
     */
	public StaticPhysicsNode getPhysicsNode();

}
