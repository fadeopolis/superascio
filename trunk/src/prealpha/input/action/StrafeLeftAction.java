package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.enums.*;
import prealpha.input.MoveAction;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class StrafeLeftAction extends MoveAction {
	
	public StrafeLeftAction( Ascio target) {
		super(target);
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		// TODO Auto-generated method stub
		switch (type) {
		case thirdPerson :
			target.getNode().addForce(target.getLeft().mult(forwardSpeed));
			break;
		case isometric :
			target.getNode().addForce(target.getLeft().mult(forwardSpeed));
			break;
		default :
			break;
		}
	}
}
