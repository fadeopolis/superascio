package prealpha.input.action;

import prealpha.character.Character;
import prealpha.input.ActionType;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jmex.physics.DynamicPhysicsNode;

public abstract class CharacterAction implements InputActionInterface {

	protected Character target;
	protected ActionType type;

	public CharacterAction( Character target ) {
		this.target = target;
	}
	
	@Override
	public abstract void performAction(InputActionEvent evt);

}
