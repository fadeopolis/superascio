package prealpha.foes;

import prealpha.ascio.Ascio;
import prealpha.interfaces.Destructible;
import prealpha.interfaces.Updateable;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpatial;

public abstract class Foe extends Node implements Destructible, Updateable {
	private static final long serialVersionUID = 562913003709690030L;
	
	protected static Vector3f buff = new Vector3f();
	/** the enemy the foes follows/attacks, for now it's always ascio */
	protected Ascio target;
	protected int health;
	protected DynamicPhysicsNode node;		
	public boolean update;
	
	public Foe(DynamicPhysicsNode node) {
		this.node = node;
		health = 100;
		update = false;
	}

	// well a very basic and necessary ability of enemys i guess
	// returns true if possible, else false
	abstract public boolean goTo(Vector3f destination);
	
	// well, i don't know if i really want to realize attacks this way but, well, let's try it for now
	// returns true if possible/successful
	abstract public boolean attack();
	
	public void setNode(DynamicPhysicsNode node) {
		this.node = node;
	}
	public DynamicPhysicsNode getNode() {
		return node;
	}
	
	public Ascio getTarget() {
		return target;
	}
	public void setTarget(Ascio target) {
		this.target = target;
		System.out.println("TARGET set");
	}
}
