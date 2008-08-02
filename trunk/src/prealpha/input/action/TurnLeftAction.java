package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.input.MoveAction;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jme.renderer.Camera;
import com.jmex.physics.*;

public class TurnLeftAction extends MoveAction {
    /**
      @param direction direction this action instance will move
    */ 
    public TurnLeftAction( Ascio target ) {
    	super(target);
    }
    
    public void performAction( InputActionEvent evt ) {
		switch (type) {
		case thirdPerson :
			target.getNode().addTorque(turnLeft);
			break;
		case isometric :
			target.getNode().addTorque(turnLeft);
			break;
		default :
			break;
		}
    }
}
