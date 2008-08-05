package prealpha.input.action;

import prealpha.ascio.Ascio;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class ForwardAction extends MoveAction {
	
	public ForwardAction( Ascio target ) {
		super(target);
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		target.getPhysicsNode().addForce(target.getDirection().mult(forwardSpeed));			
	}
}
