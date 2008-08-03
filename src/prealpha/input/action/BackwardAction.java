package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.input.MoveAction;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class BackwardAction extends MoveAction {
	
	public BackwardAction( Ascio target) {
		super(target);
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		// TODO Auto-generated method stub
		target.getNode().addForce(target.getDirection().mult(backwardSpeed));			
	}
}
