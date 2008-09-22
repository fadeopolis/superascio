package prealpha.ascio;

import prealpha.physics.DynamicPhysicsNodeWrapper;
import prealpha.util.*;
import prealpha.ascio.weapon.Weapon;
import prealpha.input.InputListener;
import prealpha.input.MovementPermitter;
import prealpha.input.MovementPermitter.MovementType;
import prealpha.interfaces.*;

import com.jme.input.InputHandler;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.MaterialState;
import com.jmex.physics.*;
import com.jmex.physics.material.Material;

public abstract class Ascio<E extends DynamicPhysicsNode> extends DynamicPhysicsNodeWrapper implements InputListener, PhysicsSpatial, Updateable, Destructible {
	protected int health;
	
	/** Determines physical properties */
	protected Material material;
	
	/** Weapon which ascio is wielding right now */
	protected Weapon weapon;
	
	/** Tells you which movements Ascio is allowed to make */
	public MovementPermitter mp;
	
	/**
	 * 
	 * @param target The node to which the Ascio is attached
	 */
	public Ascio( E e ) {
		this(null, e);
	}
	
	public Ascio( String name, E e ) {
		this(name, e, null);
	}
	
	@SuppressWarnings("unchecked")
	public Ascio( String name, E e, Vector3f location) {
		super(name , e );

		mp = new MovementPermitter();
		mp.addMovementType(MovementType.Forward);
		mp.addMovementType(MovementType.Backward);
		mp.addMovementType(MovementType.Jump);
		
		if (location != null) this.setLocalTranslation(location);
		//TODO : should health be measured in percent? meaning a float clamped between [0,1]
		health = 100;
	}
	
	/**
	 * @return a vector that tells you which direction the player is facing
	 */
	public Vector3f getDirection() {
		return getLocalRotation().getRotationColumn(2);
	}
	
	public Vector3f getLeft() {
		return getLocalRotation().getRotationColumn(0);
	} 

	public int getHealth() {
		return health;
	}

	public Weapon getWeapon() {
		return weapon;
	}
	public Material getMaterial() {
		return material;
	}

	public abstract void attack();
	
	//alas, ascio is mortal, thus one may harm him with these methods
	public void damage(int damage) {
		// TODO Auto-generated method stub
		health -= damage;
		if (health == 0) this.destroy();
	}
	public abstract void destroy();
	
}