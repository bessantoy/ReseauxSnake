package strategy;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;

public class StrategyDown implements Strategy {

	public AgentAction chooseAction(Snake snake, SnakeGame snakeGame) {

		return AgentAction.MOVE_DOWN;

	}

}
