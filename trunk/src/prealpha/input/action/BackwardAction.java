package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.input.PAHandler;
import prealpha.input.MovementPermitter.MovementType;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class BackwardAction extends MoveAction {
	
	public BackwardAction( PAHandler handler, Ascio<?> target) {
		super(handler, target);
	}
	
	@Override
	public void performAction(InputActionEvent evt) {
		/*
		if ( target.mp.requestPermit(MovementType.Backward) ) {
			buff = handler.advance(-1);
			target.getLocalTranslation();
			buff.subtractLocal(target.getLocalTranslation());
			buff.multLocal(-backwardSpeed);
			target.addForce(buff, offset);
		}*/
		buff = target.getDirection();
		if (target.mp.requestPermit(MovementType.Forward)) {
			buff.multLocal(backwardSpeed);
			
		} else {
			buff.multLocal(backwardSpeed/5);
		}
		target.addForce(buff);
	}
}
