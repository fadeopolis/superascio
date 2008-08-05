package prealpha.input.action;

import prealpha.ascio.Ascio;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jme.renderer.Camera;
import com.jmex.physics.*;

public class TurnRightAction extends MoveAction {
    /**
      @param direction direction this action instance will move
    */ 
    public TurnRightAction( Ascio target ) {
    	super(target);
    }
    
    public void performAction( InputActionEvent evt ) {
		switch (type) {
		case thirdPerson :
			target.getPhysicsNode().addTorque(turnRight);
			break;
		case isometric :
			target.getPhysicsNode().addTorque(turnRight);
			break;
		default :
			break;
		}
    }
}
