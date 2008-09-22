package prealpha.input;

public enum ActionType {
		MOVE_FORWARD, MOVE_BACKWARD, STRAFE_LEFT, STRAFE_RIGHT, TURN_LEFT, TURN_RIGHT, JUMP,
		FIRE;
		
		private ActionType() {
			this.pressed = false;
		}
		
		private boolean pressed;
		
		public boolean isPressed() {
			return pressed;
		}
		
		void setPressed( boolean pressed ) {
			this.pressed = pressed;
		}
		
		private static final ActionType[] actions = ActionType.values();
		public static final int maxOrdinal = actions.length;
		
		public static ActionType valueOf(int i) {
			return actions[i];
		}
}
