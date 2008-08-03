package prealpha.input;

import com.jme.input.action.InputAction;
import com.jme.math.Vector3f;

import prealpha.ascio.Ascio;
import prealpha.enums.GameType;

public abstract class MoveAction extends InputAction {
	static protected GameType type = GameType.thirdPerson;
	protected Ascio target;
	protected Vector3f turnLeft = new Vector3f(0,200,0);
	protected Vector3f turnRight = new Vector3f(0,-400,0);
	protected Vector3f jumpForce = new Vector3f(0,5000,0);
	protected float forwardSpeed = 400;
	protected float backwardSpeed = 350;
	protected float strafeSpeed = 350;
	
	protected Vector3f buff;
	
	public MoveAction(Ascio target) {
			this.target = target;
			buff = new Vector3f();
	}
}
