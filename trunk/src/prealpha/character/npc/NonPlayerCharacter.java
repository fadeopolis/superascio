package prealpha.character.npc;

import java.util.Map;

import prealpha.character.Character;
import prealpha.character.Faction;
import prealpha.core.Updater;

import com.jme.scene.Node;
import com.jme.util.export.Savable;
import com.jmex.physics.PhysicsSpace;

public abstract class NonPlayerCharacter extends Character {

	public NonPlayerCharacter( final Updater updater, final PhysicsSpace space, final Node scene, Faction faction ) {
		super( updater, space, scene, faction );
	}

}
