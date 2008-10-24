package prealpha.character;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import prealpha.character.pc.Ascio;
import prealpha.character.pc.PlayerCharacter;
import prealpha.core.Core;

import com.jme.input.InputHandler;
import com.jme.scene.Spatial;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.PhysicsUpdateCallback;
import com.jmex.physics.contact.ContactCallback;
import com.jmex.physics.contact.PendingContact;

public class Factory {
	
	private PhysicsSpace physics;
	
	public Factory( PhysicsSpace physics ) {
		this.physics = physics;

	}
	
	public PlayerCharacter createAscio() {
//		Ascio pc = new Ascio( Core.get().getPhysics().createDynamicNode());
		
		
		return null;
	}
}

class CharacterStatusCallback implements PhysicsUpdateCallback, ContactCallback {

	private Collection<Character> targets;
	private Spatial scene;
	
	CharacterStatusCallback( Spatial scene ) {
		this.scene = scene;
		this.targets = new ConcurrentLinkedQueue<Character>();
	}
	
	@Override
	public void beforeStep(PhysicsSpace space, float time) {
		for ( Character c : targets ) {
		}
	}

	@Override
	public boolean adjustContact(PendingContact contact) {
//		for ( Character<PhysicsNode> c : targets ) {
//			c.getActionPermitter().update();
//		}
//		if ( contact.getNode1() == c)
		
		return false;
	}
	
	@Override
	public void afterStep(PhysicsSpace space, float time) {
		
	}

}