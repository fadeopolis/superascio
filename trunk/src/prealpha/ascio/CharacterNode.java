package prealpha.ascio;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

import prealpha.interfaces.Updateable;
import prealpha.util.Util;

/**
 * A Node that maintains at least one PhysicsNode for physics representation
 * The getters/setters for Translation,Rotation,... mostly pass data from the PhysicsNode
 * 
 * @author fader
 *
 */
public abstract class CharacterNode extends Node implements Updateable {
	private static final long serialVersionUID = 9104924022408381297L;

	/** the node for handling all physical interactions */
	protected DynamicPhysicsNode physicsNode;
	
	protected Vector3f vbuff = new Vector3f();
	protected float fbuff = 0;
	
	public CharacterNode( PhysicsSpace space ) {
		this(null, space);
	}
	
	public CharacterNode( String name, PhysicsSpace space ) {
		this( name, null, space );
	}
	
	public CharacterNode( String name, Vector3f location, PhysicsSpace space ) {
		super();
		
		if ( location != null) setLocalTranslation(location);
		
		setPhysicsNode(space.createDynamicNode());
		this.attachChild(physicsNode);
		physicsNode.setName(name);
	}
	
	public void setPhysicsNode(DynamicPhysicsNode node) {
		this.physicsNode = node;
		this.attachChild(node);
	}
	public DynamicPhysicsNode getPhysicsNode() {
		return physicsNode;
	}
}
