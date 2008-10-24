package prealpha.input.action;

import prealpha.character.Character;

import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

public class MoveBackwardAction extends MovementAction {

	Quaternion q;
	
	public MoveBackwardAction(Character target) {
		super(target, target.getDirection(null).multLocal(-1));
		
		q = new Quaternion();
		q.fromAngleAxis(180*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
		
		offset.set(1,0,0);
	}

	@Override
	public void performAction(InputActionEvent evt) {
//		target.getActionPermitter().setFacingForward(false);
		
		target.getPhysicsNode().addForce(direction.mult(force*multiplier), Vector3f.ZERO);
	}
}
