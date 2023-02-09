package controller;

import model.SnakeGame;

import java.util.List;

import model.InputMap;
import network.Human;
import utils.GameFeatures;

public class ControllerSnakeGame extends AbstractController {

	SnakeGame snakeGame;
	List<Human> players;
	InputMap inputMap = null;
	String levelAI;

	public ControllerSnakeGame(String layoutName) {
		try {
			this.inputMap = new InputMap("layouts/" + layoutName + ".lay");
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

}
