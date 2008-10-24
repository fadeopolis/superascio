package prealpha.character;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.NoSuchElementException;

import prealpha.core.ActionPermitter;
import prealpha.core.Damageable;
import prealpha.core.DynamicPhysicsSpatial;
import prealpha.core.Entity;
import prealpha.core.PhysicalEntity;
import prealpha.core.Updateable;
import prealpha.core.Updater;
import prealpha.curve.Curve;
import prealpha.util.Util;
import prealpha.util.Util.Prop;

import com.jme.input.InputHandler;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.Timer;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.contact.ContactInfo;

public abstract class Character extends PhysicalEntity implements DynamicPhysicsSpatial, Damageable {

	private Node scene;

	private Faction faction;
	
	private float health;
	
	private InputHandler handler;
	private ActionPermitter permitter;
	
	private boolean isAlive;
	private boolean isActive;
	
	
	public Character( final Updater updater, final PhysicsSpace space, final Node scene, Faction faction ) {
		super(updater, space);
		
		// gets all necessary values from the map data
		// throws a InstantiationException
			this.handler = new InputHandler();
			
			this.permitter = new ActionPermitter();
			
			this.scene = scene;
		
			if ( scene != null ) {
				handler.addAction(new InputAction() {
					
					public void performAction( InputActionEvent evt ) {
		               	onCollision( (ContactInfo) evt.getTriggerData());
		            }
				}, getPhysicsNode().getCollisionEventHandler(), false);
			}
		
			this.faction = (faction == null) ? Faction.None : faction;
			
			this.health = 100;	
			
			isAlive = true;
			isActive = true;
	}
	
	// game mechanics

	/**
	 * Called every time this Character collides with something
	 */
	protected void onCollision( ContactInfo c ) {
		if ( ( c.getNode1().hasAncestor(scene) || c.getNode2().hasAncestor(scene) ) ) {
        	permitter.setOnFloor();
        }
	}
	
	@Override
	public void damage(float damage) {
		health -= damage;
		if ( isAlive() && health < 0  ) kill();
	}
	
	@Override
	public void kill() {
		isAlive = false;
		System.out.println(getID() + " was killed at " + Timer.getTimer().getTimeInSeconds());
	}
	
	@Override
	public void delete() {
		getPhysicsNode().setActive(false);
		removeFromParent();
		removeFromUpdater();
//		this.handler = null;
//		this.permitter = null;
		this.scene = null;
	}

	@Override
	public void update(float time) {
		permitter.update();
		handler.update(time); 
		
		getPhysicsNode().getLocalTranslation().set(2, 0);
	}

	// getters/setters
	
	public ActionPermitter getActionPermitter() {
		return this.permitter;
	}
		
	public Node getScene() {
		return scene;
	}
	
	public float getHealth() {
		return health;
	}
	
	protected InputHandler getInputHandler() {
		return handler;
	}
	
	public Faction getFaction() {
		return faction;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	// serialization
	
	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		
		InputCapsule cap = im.getCapsule(this);
		cap.readFloat("health", 100);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
		
		OutputCapsule cap = ex.getCapsule(this);
		cap.write(health, "health", 100);
	}

	// alter stuff from Node
	
//	@Override
//	public void updateGeometricState( float time, boolean initiator ) {
//		
//	}
	
//    @Override
//    public void updateWorldVectors(boolean recurse) {
//        super.updateWorldVectors(recurse);
//        
//        getLocalTranslation().set( getPhysicsNode().getLocalTranslation() );
//        getPhysicsNode().getLocalTranslation().zero();
//    }
}