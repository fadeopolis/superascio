package prealpha.ascio;

import prealpha.util.*;
import prealpha.interfaces.*;

import com.jme.input.InputHandler;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.MaterialState;
import com.jmex.physics.*;
import com.jmex.physics.material.Material;

public abstract class Ascio extends Node implements Updateable, Destructible {

	/** the node for handling all physical interactions */
	protected DynamicPhysicsNode node;
	
	protected int health;
	
	/** Determines physical properties */
	protected Material material;
	
	/** Weapon which ascio is wielding right now */
	protected Weapon weapon;
	
	/**
	 * 
	 * @param target The node to which the Ascio is attached
	 */
	public Ascio(PhysicsSpace space) {
		node = space.createDynamicNode();
		this.attachChild(node);
		
		//TODO : should health be measured in percent? meaning a float clamped between [0,1]
		health = 100;

	}

	/**
	 * 
	 * @return the 
	 */
	public DynamicPhysicsNode getNode() {
		return node;
	}

	/**
	 * @return a vector that tells you which direction the player is facing
	 */
	public Vector3f getDirection() {
		return node.getLocalRotation().getRotationColumn(2);
	}
	
	public Vector3f getLeft() {
		return node.getLocalRotation().getRotationColumn(0);
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