package factory;

import agent.Snake;
import strategy.StrategyHuman;
import strategy.StrategyRandom;
import strategy.StrategyAdvanced;
import strategy.StrategyDown;
import utils.FeaturesSnake;
import utils.Position;

public class SnakeFactory {

	static int id = 0;

	public Snake createSnake(FeaturesSnake featuresSnake, String levelAI) {

		int x = featuresSnake.getPositions().get(0).getX();
		int y = featuresSnake.getPositions().get(0).getY();

		Snake snake = new Snake(new Position(x, y), featuresSnake.getLastAction(), id, featuresSnake.getColorSnake());
		id++;

		switch (levelAI) {
			case "Random":
				snake.setStrategy(new StrategyRandom());

				break;
			case "Human":
				snake.setStrategy(new StrategyHuman());

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

}