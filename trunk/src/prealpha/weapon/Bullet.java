package prealpha.weapon;

import java.util.Map;

import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jmex.physics.PhysicsSpace;

import prealpha.core.PhysicalEntity;

public abstract class Bullet extends PhysicalEntity {

	private static final long serialVersionUID = -7910440138297895472L;

	protected float speed;
	protected float mass;
	protected float damage;
	protected boolean fired;
	
	public Bullet( final PhysicsSpace physics, final InputHandler handler ) {
		super( null, physics );
		
		handler.addAction(new CollisionAction(), getPhysicsNode().getCollisionEventHandler(),false);
	}

	public abstract void fire( Vector3f origin, Vector3f direction );
}