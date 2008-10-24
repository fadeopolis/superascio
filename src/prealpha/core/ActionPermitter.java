package prealpha.core;

import prealpha.input.ActionType;
import prealpha.util.Util;

public class ActionPermitter {
	
	private boolean targetOnFloor;
	private boolean facingForward;
	private boolean hasTurned;
	
	public ActionPermitter() {
		this.targetOnFloor = true;
		this.facingForward = true;
		this.hasTurned = false;
	}
	
	public void update() {
		targetOnFloor = false;
		hasTurned = false;
	}

	public boolean getPermit( ActionType type ) {
		switch ( type ) {
		case MOVE_FORWARD :
			return targetOnFloor;
		case FIRE :
			return true;
		default :
			return false;
		}
	}
	
	public boolean isOnFloor() {
		return targetOnFloor;
	}
	
	public void setOnFloor() {
		targetOnFloor = true;
	}

	public boolean hasTurned() {
		return hasTurned;
	}
	
	public boolean isFacingForward() {
		return this.facingForward;
	}
	
	public void setFacingForward( boolean value ) {
		this.hasTurned = ( value != facingForward);
		this.facingForward = value;
	}

}
