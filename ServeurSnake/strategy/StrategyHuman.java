package strategy;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;

public class StrategyHuman implements Strategy {

    @Override
    public AgentAction chooseAction(Snake snake, SnakeGame snakeGame) {

        int index = snakeGame.getSnakes().indexOf(snake);
        return snakeGame.getInputMoves().get(index);

    }

}