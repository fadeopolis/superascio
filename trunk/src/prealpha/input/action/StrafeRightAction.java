package prealpha.input.action;

import prealpha.ascio.Ascio;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class StrafeRightAction extends MoveAction {
	
	public StrafeRightAction( Ascio target) {
		super(target);
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		// TODO Auto-generated method stub
		switch (type) {
		case thirdPerson :
			target.getPhysicsNode().addForce(target.getLeft().mult(-strafeSpeed));
			break;
		case isometric :
			target.getPhysicsNode().addForce(target.getLeft().mult(-strafeSpeed));
			break;
		default :
			break;
		}
	}
}
