package controller;

import java.util.ArrayList;

import agent.Snake;
import item.Item;
import model.SnakeGame;
import model.InputMap;
import utils.AgentAction;
import view.PanelSnakeGame;
import view.ViewSnakeGame;
import view.ViewCommand;



public class ControllerSnakeGame extends AbstractController {

	
	SnakeGame snakeGame;
	
	
	public ControllerSnakeGame() {
		

		String layoutName = "layouts/smallArena.lay";
		
		
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
		

		
		PanelSnakeGame panelSnakeGame = new PanelSnakeGame(inputMap.getSizeX(), inputMap.getSizeY(), inputMap.get_walls(), inputMap.getStart_snakes(), inputMap.getStart_items());
		
		
		ViewSnakeGame viewSnakeGame = new ViewSnakeGame(this, snakeGame, panelSnakeGame);
		
		ViewCommand viewCommand = new ViewCommand(this, snakeGame);
		
		
	}
	

	public void goUp(){
        this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_UP);
	}
	
	public void goDown(){
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_DOWN);
	}	
	
	public void goLeft(){
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_LEFT);
	}	
	
	public void goRight(){
		this.snakeGame.setInputMoveHuman1(AgentAction.MOVE_RIGHT);
	}	



}
