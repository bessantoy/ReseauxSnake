package agent;

import java.util.ArrayList;

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

	boolean toRemove;

	ColorSnake colorSnake;

	public Snake(Position position, AgentAction lastAction, int id, ColorSnake colorSnake) {

		this.positions = new ArrayList<Position>();

		this.positions.add(position);

		this.setId(id);

		this.setInvincibleTimer(-1);
		this.setSickTimer(-1);

		this.toRemove = false;

		this.colorSnake = colorSnake;

		this.setLastAction(lastAction);

	}

	public FeaturesSnake toFeaturesSnake() {
		return new FeaturesSnake(positions, lastAction, colorSnake, invincibleTimer > 0, sickTimer > 0);
	}

	public int getSize() {
		return this.positions.size();
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
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

	public boolean isToRemove() {
		return toRemove;
	}

	public void setToRemove(boolean toRemove) {
		this.toRemove = toRemove;
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
}
