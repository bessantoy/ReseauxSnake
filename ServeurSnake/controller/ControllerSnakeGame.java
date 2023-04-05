package controller;

import java.util.List;

import client.Human;
import model.InputMap;
import model.SnakeGame;
import utils.GameFeatures;

public class ControllerSnakeGame extends AbstractController {

	SnakeGame snakeGame;
	List<Human> players;
	InputMap inputMap = null;

	String levelAI;

	public ControllerSnakeGame(String layoutName, List<Human> players, String levelAI) {
		this.players = players;
		try {
			this.inputMap = new InputMap(layoutName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.levelAI = levelAI;
	}

	public void initGame() {
		this.snakeGame = new SnakeGame(10000, inputMap, players, levelAI);
		this.snakeGame.init();

		this.game = snakeGame;
	}

	public GameFeatures getGameFeatures() {
		return this.snakeGame.toGameFeatures();
	}

	public SnakeGame getGame() {
		return this.snakeGame;
	}

	public List<Human> getPlayers() {
		return players;
	}

	public InputMap getInputMap() {
		return inputMap;
	}

	public void setPlayers(List<Human> players) {
		this.players = players;
	}

	public String getLevelAI() {
		return levelAI;
	}

	public void setLevelAI(String levelAI) {
		this.levelAI = levelAI;
	}

	public int getNumberOfPlayers() {
		if (inputMap != null) {
			return inputMap.getStart_snakes().size();
		} else {
			return -1;
		}
	}

	public double getSpeed() {
		return this.game.getTime();
	}

}
