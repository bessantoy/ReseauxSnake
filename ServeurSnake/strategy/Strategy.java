package strategy;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;

public interface Strategy {

	public AgentAction chooseAction(Snake snake, SnakeGame snakeGame);

}
