package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.input.MoveAction;

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
    	target.getNode().addForce(jumpForce);
//    	target.getNode().setLinearVelocity(jumpForce);
   }
}
