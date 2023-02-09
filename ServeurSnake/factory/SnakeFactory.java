package factory;

import agent.Snake;
import strategy.StrategyHuman;
import strategy.StrategyRandom;
import strategy.StrategyAdvanced;
import strategy.StrategyDown;
import network.Human;
import utils.FeaturesSnake;
import utils.Position;

public class SnakeFactory {

	public Snake createSnake(FeaturesSnake featuresSnake, int id, String levelAI) {

		int x = featuresSnake.getPositions().get(0).getX();
		int y = featuresSnake.getPositions().get(0).getY();

		Snake snake = new Snake(new Position(x, y), featuresSnake.getLastAction(), id, featuresSnake.getColorSnake());

		switch (levelAI) {
			case "Random":
				snake.setStrategy(new StrategyRandom());
				break;
			case "Advanced":
				snake.setStrategy(new StrategyAdvanced());
				break;
			default:
				snake.setStrategy(new StrategyDown());
				break;
		}

		return snake;

	}

	public Snake createSnake(FeaturesSnake featuresSnake, int id) {

		int x = featuresSnake.getPositions().get(0).getX();
		int y = featuresSnake.getPositions().get(0).getY();

		Snake snake = new Snake(new Position(x, y), featuresSnake.getLastAction(), id, featuresSnake.getColorSnake());

		snake.setStrategy(new StrategyHuman());

		return snake;

	}

}