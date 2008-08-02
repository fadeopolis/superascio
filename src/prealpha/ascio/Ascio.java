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

public abstract class Ascio implements Destructible {

	// the node for handling all physical interactions
	protected DynamicPhysicsNode node;
	
	protected int health;
	// what heroes are made of
	protected Material material;
	
	protected Weapon weapon;
	
	public Ascio(DynamicPhysicsNode target) {
		node = target;
		
		//TODO : should health be measured in percent? meaning a float clamped between [0,1]
		health = 100;

	}

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

	//alas, ascio is mortal, thus one may harm him with these methods
	public void damage(int damage) {
		// TODO Auto-generated method stub
		health -= damage;
		if (health == 0) this.destroy();
	}
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}