package prealpha.input.action;

import com.jme.input.action.InputAction;
import com.jme.math.Vector3f;

import prealpha.ascio.Ascio;
import prealpha.input.PAHandler;
import prealpha.input.PAHandler.GameType;

public abstract class MoveAction extends InputAction {
	static public GameType type = GameType.thirdPerson;
	protected Ascio target;
	protected PAHandler handler;
	protected Vector3f turnLeft = new Vector3f(0,200,0);
	protected Vector3f turnRight = new Vector3f(0,-400,0);
	protected Vector3f offset = new Vector3f(0, 0, 1.5f);
	protected float jumpForce = 1500;
	protected float forwardSpeed = 500;
	protected float backwardSpeed = -400;
	protected float strafeSpeed = 350;
	
	protected Vector3f buff;
	
	public MoveAction(PAHandler handler, Ascio target) {
			if (this.target == null) this.target = target;
			if (this.handler == null) this.handler = handler;
			buff = new Vector3f();
	}
}
