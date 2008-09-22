package prealpha.input.action;

import prealpha.ascio.Ascio;
import prealpha.input.PAHandler;
import prealpha.input.MovementPermitter.MovementType;

import com.jme.input.*;
import com.jme.input.action.*;
import com.jme.math.*;
import com.jmex.physics.*;

public class ForwardAction extends MoveAction {
	
	public ForwardAction(PAHandler handler, Ascio target) {
		super(handler, target);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void performAction(InputActionEvent evt) {
		/*
		buff.addLocal( handler.advance(1) );
		target.getLocalTranslation();
		buff.subtractLocal(target.getLocalTranslation());
		buff.multLocal(forwardSpeed);
		target.addForce(buff, offset);
		*/
		buff = target.getDirection();
		if (target.mp.requestPermit(MovementType.Forward)) {
			buff.multLocal(forwardSpeed);
			
		} else {
			buff.multLocal(forwardSpeed/5);
		}
		target.addForce(buff);
	}
}
