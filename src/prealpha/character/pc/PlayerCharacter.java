package prealpha.character.pc;

import java.util.Map;
import java.util.NoSuchElementException;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jmex.physics.PhysicsSpace;

import prealpha.character.Character;
import prealpha.character.Faction;
import prealpha.core.DynamicPhysicsSpatial;
import prealpha.core.Updater;
import prealpha.weapon.Weapon;

public abstract class PlayerCharacter extends Character implements DynamicPhysicsSpatial {

	Weapon weapon;
	Camera cam;
	
	public PlayerCharacter( final Updater updater, final PhysicsSpace space, final Node scene ) {
		super(updater, space, scene, Faction.Ascio);						
		
		cam = DisplaySystem.getDisplaySystem().getRenderer().getCamera();
	}

	public abstract boolean attack();
	
	public Weapon getWeapon() {
		return weapon;
	}

	public void update( float time ) {
		super.update(time);
		
		weapon.update(time);
		
	}
}
