package agent;

import java.util.ArrayList;
import java.util.List;

import strategy.Strategy;
import utils.AgentAction;
import utils.ColorSnake;
import utils.FeaturesSnake;
import utils.Position;

public class Snake {

	ArrayList<Position> positions;

	private AgentAction lastAction;

	private int invincibleTimer;
	private int sickTimer;

	Strategy strategy;

	int oldTailX = -1;
	int oldTailY = -1;

	private int id;

	private int score;

	boolean isAlive;

	ColorSnake colorSnake;

	public Snake(Position position, AgentAction lastAction, int id, ColorSnake colorSnake) {

		this.positions = new ArrayList<>();

		this.positions.add(position);

		this.setId(id);

		this.setInvincibleTimer(-1);
		this.setSickTimer(-1);

		this.isAlive = true;

		this.colorSnake = colorSnake;

		this.setLastAction(lastAction);

	}

	public FeaturesSnake toFeaturesSnake() {
		return new FeaturesSnake(positions, lastAction, colorSnake, invincibleTimer > 0, sickTimer > 0, isAlive, id, score);
	}

	public int getSize() {
		return this.positions.size();
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = (ArrayList<Position>) positions;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public int getX() {
		return this.positions.get(0).getX();
	}

	public int getY() {
		return this.positions.get(0).getY();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public void setOldTailX(int oldTailX) {
		this.oldTailX = oldTailX;
	}

	public void setOldTailY(int oldTailY) {
		this.oldTailY = oldTailY;
	}

	public int getOldTailX() {
		return oldTailX;
	}

	public int getOldTailY() {
		return oldTailY;
	}

	public ColorSnake getColorSnake() {
		return colorSnake;
	}

	public void setColorSnake(ColorSnake colorSnake) {
		this.colorSnake = colorSnake;
	}

	public int getInvincibleTimer() {
		return invincibleTimer;
	}

	public void setInvincibleTimer(int invincibleTimer) {
		this.invincibleTimer = invincibleTimer;
	}

	public int getSickTimer() {
		return sickTimer;
	}

	public void setSickTimer(int sickTimer) {
		this.sickTimer = sickTimer;
	}

	public AgentAction getLastAction() {
		return lastAction;
	}

	public void setLastAction(AgentAction lastAction) {
		this.lastAction = lastAction;
	}

	public int getScore() {
		return score;
	}

	public void increaseScore(int score) {
		this.score += score;
	}
}
