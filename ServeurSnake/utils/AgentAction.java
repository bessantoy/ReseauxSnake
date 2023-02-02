package utils;

public enum AgentAction {
	MOVE_UP("up"), MOVE_DOWN("down"), MOVE_LEFT("left"), MOVE_RIGHT("right");

	private String action;

	private AgentAction(String action) {
		this.action = action;
	}
}
