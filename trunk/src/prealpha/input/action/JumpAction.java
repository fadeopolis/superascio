package prealpha.input.action;

import prealpha.character.Character;

import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;

public class JumpAction extends MovementAction {

	public JumpAction(Character target) {
		super(target, Vector3f.UNIT_Y.mult(25));
	}

	public void performAction(InputActionEvent evt) {
		if ( target.getActionPermitter().getPermit(type)) {
			target.getPhysicsNode().addForce(direction.mult(force*multiplier), Vector3f.ZERO);
		}		
	}

}
