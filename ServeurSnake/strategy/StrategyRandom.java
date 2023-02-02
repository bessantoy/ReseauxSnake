package strategy;

import java.util.Random;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;

public class StrategyRandom implements Strategy {

	@Override
	public AgentAction chooseAction(Snake snake, SnakeGame snakeGame) {

		AgentAction[] listActions = AgentAction.values();

		Random rand = new Random();

		int randomIndex = rand.nextInt(listActions.length);

		return listActions[randomIndex];

	}

}
