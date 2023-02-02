package controller;

import model.SnakeGame;
import model.InputMap;
import utils.AgentAction;
import utils.GameFeatures;

import java.io.*;

public class ControllerSnakeGame extends AbstractController {

	SnakeGame snakeGame;

	public ControllerSnakeGame() {

		String layoutName = "ServeurSnake/layouts/smallArena.lay";

		InputMap inputMap = null;

		try {
			inputMap = new InputMap(layoutName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.snakeGame = new SnakeGame(10000, inputMap);
		this.snakeGame.init();

		this.game = snakeGame;

	}

	public void goUp() {
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_UP);
	}

	public void goDown() {
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_DOWN);
	}

	public void goLeft() {
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_LEFT);
	}

	public void goRight() {
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_RIGHT);
	}

	public String gameFeaturesToJson() {
		GameFeatures gameFeatures = this.snakeGame.toGameFeatures();
		return gameFeatures.toJson();
	}

}
