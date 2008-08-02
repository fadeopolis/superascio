package prealpha.input;

import com.jme.input.action.InputAction;
import com.jme.math.Vector3f;

import prealpha.ascio.Ascio;
import prealpha.enums.GameType;

public abstract class MoveAction extends InputAction {
	static protected GameType type = GameType.thirdPerson;
	protected Ascio target;
	protected Vector3f turnLeft = new Vector3f(0,250,0);
	protected Vector3f turnRight = new Vector3f(0,-250,0);
	protected Vector3f jumpForce = new Vector3f(0,10,0);
	protected float forwardSpeed = 250;
	protected float backwardSpeed = 250;

	public MoveAction () {
		
	}
	public MoveAction(Ascio target) {
			this.target = target;
	}
}
