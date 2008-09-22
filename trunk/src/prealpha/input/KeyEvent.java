package prealpha.input;

public class KeyEvent {
	ActionType action;
	
	int keyCode;

	public ActionType getAction() {
		return action;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	void setAction(ActionType action) {
		this.action = action;
	}
 
	void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
}
