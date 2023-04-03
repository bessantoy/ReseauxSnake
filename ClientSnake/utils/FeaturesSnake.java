package utils;

import java.util.ArrayList;
import java.util.List;

public class FeaturesSnake {

	ArrayList<Position> positions;

	private AgentAction lastAction;

	ColorSnake colorSnake;

	boolean isInvincible;
	boolean isSick;

	boolean isAlive;

	int id;
	int score;

	public FeaturesSnake(List<Position> positions, AgentAction lastAction, ColorSnake colorSnake,
			boolean isInvincible, boolean isSick, boolean isAlive, int id, int score) {

		this.positions = (ArrayList<Position>) positions;
		this.colorSnake = colorSnake;
		this.lastAction = lastAction;

		this.isInvincible = isInvincible;
		this.isSick = isSick;

		this.isAlive = isAlive;

		this.id = id;
		this.score = score;
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = (ArrayList<Position>) positions;
	}

	public ColorSnake getColorSnake() {
		return colorSnake;
	}

	public void setColorSnake(ColorSnake colorSnake) {
		this.colorSnake = colorSnake;
	}

	public boolean isInvincible() {
		return isInvincible;
	}

	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}

	public boolean isSick() {
		return isSick;
	}

	public void setSick(boolean isSick) {
		this.isSick = isSick;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public AgentAction getLastAction() {
		return lastAction;
	}

	public void setLastAction(AgentAction lastAction) {
		this.lastAction = lastAction;
	}

	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

}
