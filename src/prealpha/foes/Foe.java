package prealpha.foes;

import prealpha.ascio.Ascio;
import prealpha.ascio.CharacterNode;
import prealpha.interfaces.Destructible;
import prealpha.interfaces.Updateable;
import prealpha.util.Util;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsSpatial;

public abstract class Foe extends CharacterNode implements Destructible, Updateable {
	private static final long serialVersionUID = 562913003709690030L;
	
	/** the enemy the foes follows/attacks, for now it's always ascio */
	protected Ascio target;
	protected int health;	
	protected boolean updateEnabled;
	
	public Foe( PhysicsSpace space ) {
		this(null, space);
	}
	
	public Foe( String name, PhysicsSpace space ) {
		this( name, null, space );
	}
	
	public Foe( String name, Vector3f location, PhysicsSpace space ) {
		super(name, location, space);
		
		health = 100;
		updateEnabled = false;
	}	

	// well a very basic and necessary ability of enemys i guess
	// returns true if possible, else false
	abstract public boolean goTo(Vector3f destination);
	
	// well, i don't know if i really want to realize attacks this way but, well, let's try it for now
	/** 
	 * attacks the target
	 * @return true if possible, false is not possible/legal
	 */
	abstract public boolean attack();
	
	public Ascio getTarget() {
		return target;
	}
	public void setTarget(Ascio target) {
		this.target = target;
		this.updateEnabled = true;
	}
}
