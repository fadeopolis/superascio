package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.input.PAHandler;
import prealpha.input.MovementPermitter.MovementType;
import prealpha.util.Util;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class JumpAction extends MoveAction {
	
	float targetMass;
	
    /**
      @param direction direction this action instance will move
    */ 
    public JumpAction( PAHandler handler, Ascio target) {
        super(handler, target);
        
        targetMass = target.getMass();
    }
    
    public void performAction( InputActionEvent evt ) {
   // 	Util.shout(target.getMass());
		if (target.mp.requestPermit(MovementType.Forward)) {
//        	buff = target.getDirection();
//        	System.out.println(buff.length());
        	buff.set(0, 1, 0);
        	buff.multLocal(jumpForce*targetMass);
        	target.addForce(buff);
    	}
    }
}
