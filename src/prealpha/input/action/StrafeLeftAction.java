package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.enums.*;
import prealpha.input.PAHandler;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class StrafeLeftAction extends MoveAction {
	
	public StrafeLeftAction( PAHandler handler, Ascio target) {
		super(handler, target);
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		// TODO Auto-generated method stub
		switch (type) {
		case thirdPerson :
			target.addForce(target.getLeft().mult(strafeSpeed));
			break;
		case isometric :
			target.addForce(target.getLeft().mult(strafeSpeed));
			break;
		default :
			break;
		}
	}
}
