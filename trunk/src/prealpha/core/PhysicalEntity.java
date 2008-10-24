package prealpha.core;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.contact.ContactInfo;

public abstract class PhysicalEntity extends Entity implements DynamicPhysicsSpatial {

	private DynamicPhysicsNode body;
	
	public PhysicalEntity( final Updater updater, final PhysicsSpace physics ) {
		super( updater );
		
		body = physics.createDynamicNode();
		body.setName(getID().toString());
		
		this.attachChild(body);
	}
	
	protected abstract void onCollision( ContactInfo c );

	@Override
	public Vector3f getDirection( Vector3f store ) {
		if ( store == null ) {
			store = new Vector3f();
		}
		return body.getWorldRotation().getRotationColumn(0, store);
	}

	@Override
	public Vector3f getLeft( Vector3f store ) {
		if ( store == null ) {
			store = calcV;
		}
		return body.getWorldRotation().getRotationColumn(2, store);
	}
	
	@Override
	public Vector3f getUp( Vector3f store ) {
		if ( store == null ) {
			store = calcV;
		}
		return body.getWorldRotation().getRotationColumn(2, store);
	}
	
	@Override
	public DynamicPhysicsNode getPhysicsNode() {
		return body;
	}

	protected class CollisionAction extends InputAction {

		public CollisionAction() {
			super();
		}
		
		@Override
		public void performAction(InputActionEvent evt) {
			onCollision( (ContactInfo) evt.getTriggerData());
		}
		
	}
}
