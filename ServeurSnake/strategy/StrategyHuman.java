package strategy;

import agent.Snake;
import model.SnakeGame;
import utils.AgentAction;

public class StrategyHuman implements Strategy {

    @Override
    public AgentAction chooseAction(Snake snake, SnakeGame snakeGame) {

        return snakeGame.getPlayers().get(snake.getId()).getLastInput();

    }

}