package prealpha.weapon;

import java.util.Map;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;

import prealpha.character.Character;
import prealpha.core.Entity;
import prealpha.core.Updater;
import prealpha.util.Util;

public abstract class Weapon extends Entity {
		
	private float fireRate;
	private float fireCounter;	
	
	public Weapon( final Updater updater ) {
		super( updater );
		
		fireCounter = 0;
	}

	public void setFireRate( int shotsPerSecond ) {
		fireRate = 1f/shotsPerSecond;
	}
	
	public abstract boolean attack();
	
	protected boolean mayFire() {
		if ( fireCounter < 0 ) {
			fireCounter = fireRate;
			return true;
		} return false;
	}
	
	public void update( float time ) {
		fireCounter -= time;
	}
	
	private final Vector3f calcV = new Vector3f();
	
	public Vector3f getDirection( Vector3f store ) {
		if ( store == null ) {
			store = new Vector3f();
		}
		return getWorldRotation().getRotationColumn(0, store);
	}
	
	public Vector3f getLeft( Vector3f store ) {
		if ( store == null ) {
			store = calcV;
		}
		return getWorldRotation().getRotationColumn(2, store);
	}
	
	public Vector3f getUp( Vector3f store ) {
		if ( store == null ) {
			store = calcV;
		}
		return getWorldRotation().getRotationColumn(1, store);
	}
}
