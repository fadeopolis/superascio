package prealpha.input.action;

import prealpha.character.Character;
import prealpha.input.ActionType;
import prealpha.util.Util;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.math.Vector3f;
import com.jme.util.Timer;
import com.jmex.physics.DynamicPhysicsNode;

public abstract class MovementAction extends CharacterAction {

	protected final Vector3f direction;
	protected final float multiplier;
	protected float force;
	protected final Vector3f offset;
	
	private float time;
	
	public MovementAction(Character target, Vector3f direction) {
		super(target);
		this.direction = direction;
		this.multiplier = target.getPhysicsNode().getMass();
		this.force = 50;
		
		offset = new Vector3f(Vector3f.ZERO); 
		
		type = ActionType.MOVE_FORWARD;
	}

}
