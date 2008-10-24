package prealpha.input.action;

import prealpha.character.Character;

import com.jme.input.action.InputActionEvent;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class MoveForwardAction extends MovementAction {

	Quaternion q;
	
	public MoveForwardAction(Character target) {
		super(target, target.getDirection(null));
		
		q = new Quaternion();
		q.fromAngleAxis(0, Vector3f.UNIT_Y);
		
		offset.set(1,0,0);
	}

	@Override
	public void performAction(InputActionEvent evt) {
//		target.getPhysicsNode().getLocalRotation().set(q);
//		target.getActionPermitter().setFacingForward(true);
		target.getPhysicsNode().addForce(direction.mult(force*multiplier), Vector3f.ZERO);
	}
	
}
