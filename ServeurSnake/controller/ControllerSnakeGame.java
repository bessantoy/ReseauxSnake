package controller;

import model.SnakeGame;
import model.InputMap;
import utils.AgentAction;
import utils.GameFeatures;

public class ControllerSnakeGame extends AbstractController {

	SnakeGame snakeGame;

	public ControllerSnakeGame(String layoutName) {

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

	public void setDirectionPlayerUp(int indexPlayer) {
		this.snakeGame.setInputMoves(indexPlayer, AgentAction.MOVE_UP);
	}

	public void setDirectionPlayerDown(int indexPlayer) {
		this.snakeGame.setInputMoves(indexPlayer, AgentAction.MOVE_DOWN);
	}

	public void setDirectionPlayerLeft(int indexPlayer) {
		this.snakeGame.setInputMoves(indexPlayer, AgentAction.MOVE_LEFT);
	}

	public void setDirectionPlayerRight(int indexPlayer) {
		this.snakeGame.setInputMoves(indexPlayer, AgentAction.MOVE_RIGHT);
	}

	public GameFeatures getGameFeatures() {
		return this.snakeGame.toGameFeatures();
	}

}
