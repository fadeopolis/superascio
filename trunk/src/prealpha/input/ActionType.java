package prealpha.input;

public enum ActionType {
		MOVE_FORWARD, MOVE_BACKWARD, STRAFE_LEFT, STRAFE_RIGHT, TURN_LEFT, TURN_RIGHT, JUMP,
		FIRE;
			
		private static final ActionType[] actions = ActionType.values();
		
		public static ActionType valueOf(int i) {
			return actions[i];
		}
}
