package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.util.Util;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class JumpAction extends MoveAction {
    /**
      @param direction direction this action instance will move
    */ 
    public JumpAction( Ascio target) {
        super(target);
    }
    
    public void performAction( InputActionEvent evt ) {
    	Util.shout(target.getNode().getMass());
    	target.getNode().addForce(Vector3f.UNIT_Y.mult(target.getNode().getMass()*jumpForce));
//    	target.getNode().setLinearVelocity(jumpForce);
   }
}
