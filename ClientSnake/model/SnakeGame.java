package model;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import agent.Snake;
import factory.SnakeFactory;

import item.Item;
import utils.AgentAction;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.ItemType;
import utils.Position;



public class SnakeGame extends Game{


	/// A revoir

	public static int timeInvincible = 20;
	public static int timeSick = 20;

	double probSpecialItem = 1;


	private ArrayList<FeaturesSnake> start_snakes ;
	private ArrayList<FeaturesItem> start_items ;


	private ArrayList<Snake> snakes;
	private ArrayList<Item> items;




	InputMap inputMap;


	private AgentAction inputMoveHuman1;


	private int sizeX;
	private int sizeY;




	public SnakeGame(int maxTurn, InputMap inputMap) {

		super(maxTurn);

		this.inputMap = inputMap;
		this.inputMoveHuman1 = AgentAction.MOVE_DOWN;


	}

	@Override
	public void initializeGame() {

		this.walls = inputMap.get_walls().clone();

		String levelAISnake = "Advanced";
		SnakeFactory snakeFactory = new SnakeFactory();

		start_snakes = inputMap.getStart_snakes();
		start_items = inputMap.getStart_items();

		this.sizeX = inputMap.getSizeX();
		this.sizeY = inputMap.getSizeY();
		
		
		snakes = new ArrayList<Snake>();
		items = new ArrayList<Item>();

		for(FeaturesSnake featuresSnake : start_snakes) {
			snakes.add(snakeFactory.createSnake(featuresSnake, levelAISnake));
		}
		
		

		for(FeaturesItem featuresItem : start_items) {	
			items.add(new Item(featuresItem.getX(),featuresItem.getY(), featuresItem.getItemType()));
		}
	}

	@Override
	public void takeTurn() {


		ListIterator<Snake> iterSnakes = snakes.listIterator();

		while(iterSnakes.hasNext()){

			Snake snake = iterSnakes.next();
			AgentAction agentAction = snake.play(this);

			if(isLegalMove(snake, agentAction)) {
				snake.move(agentAction, this);
			} else {
				snake.move(snake.getLastAction(), this);
			}

		}
		
		




		checkSnakeEaten();


		checkWalls();



		boolean isAppleEaten = checkItemFound();


		if(isAppleEaten) {

			addRandomApple();

			Random rand = new Random();

			double r = rand.nextDouble();

			if(r < probSpecialItem) {
				
				System.out.println("add random item");
				addRandomItem();
			}

		}


		removeSnake();

		updateSnakeTimers();


	}

	public boolean isLegalMove(Snake snake, AgentAction action) {
		
		if(snake.getSize() > 1) {
			if(snake.getLastAction() == AgentAction.MOVE_DOWN && action == AgentAction.MOVE_UP) {
				
				return false;
				
			} else if(snake.getLastAction() == AgentAction.MOVE_UP && action == AgentAction.MOVE_DOWN) {
				
				return false;
				
			} else if(snake.getLastAction() == AgentAction.MOVE_LEFT && action == AgentAction.MOVE_RIGHT) {
				
				return false;
				
			} else if(snake.getLastAction() == AgentAction.MOVE_RIGHT && action == AgentAction.MOVE_LEFT) {
				
				return false;
				
			}
		}
		return true;
		
	}

	@Override
	public boolean gameContinue() {

		if(snakes.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void gameOver() {
		System.out.println("Game over");

	}




	public void addRandomApple() {

		boolean notPlaced = true;

		while(notPlaced) {
			Random rand = new Random();


			int x = rand.nextInt(this.inputMap.getSizeX());
			int y = rand.nextInt(this.inputMap.getSizeY());

			if(!this.walls[x][y] & !isSnake(x,y) & !isItem(x,y)) {

				this.items.add(new Item(x,y,ItemType.APPLE));
				notPlaced = false;  	
			}
		}


	}



	public void addRandomItem() {

		Random rand = new Random();

		int r = rand.nextInt(3);

		ItemType itemType = null;

		if(r == 0) {		
			itemType = ItemType.BOX;
		} else if(r == 1) {
			itemType = ItemType.SICK_BALL;
		} else if(r == 2) {
			itemType = ItemType.INVINCIBILITY_BALL;
		}


		boolean notPlaced = true;

		while(notPlaced) {



			int x = rand.nextInt(this.inputMap.getSizeX());
			int y = rand.nextInt(this.inputMap.getSizeY());

			if(!this.walls[x][y] & !isSnake(x,y) & !isItem(x,y)) {

				this.items.add(new Item(x,y,itemType));
				notPlaced = false;  	
			}
		}


	}


	public boolean isSnake(int x, int y) {

		for(Snake snake : snakes) {

			for(Position pos : snake.getPositions()) {

				if(pos.getX() == x & pos.getY() == y) {
					return true;
				}
			}
		}

		return false;
	}


	public boolean isItem(int x, int y) {

		for(Item item : items) {


			if(item.getX() == x & item.getY() == y) {
				return true;
			}

		}

		return false;
	}







	public boolean checkItemFound() {

		ListIterator<Item> iterItem = items.listIterator();

		boolean isAppleEaten = false;

		while(iterItem.hasNext()){

			Item item = iterItem.next();

			for(Snake snake : snakes) {


				if(snake.getSickTimer() < 1) {

					int x = snake.getPositions().get(0).getX();
					int y = snake.getPositions().get(0).getY();	

					if(item.getX() == x &  item.getY() == y ) {

						iterItem.remove();

						if(item.getItemType() == ItemType.APPLE) {
							snake.sizeIncrease();
							isAppleEaten = true;
						}

						if(item.getItemType() == ItemType.BOX) {
							Random rand = new Random();
							double r = rand.nextDouble();
							if(r < 0.5) {
								snake.setInvincibleTimer(this.timeInvincible);

							} else {
								snake.setSickTimer(this.timeSick);
							}

						}

						if(item.getItemType() == ItemType.SICK_BALL) {

							snake.setSickTimer(this.timeSick);
						}

						if(item.getItemType() == ItemType.INVINCIBILITY_BALL) {

							snake.setInvincibleTimer(this.timeInvincible);
						}

					}

				}

			}


		}


		return isAppleEaten;
	}



	public void checkSnakeEaten() {

		System.out.println("checkSnakeEaten");

		for(Snake snake1 : snakes) {


			if(snake1.getInvincibleTimer() < 1) {


				for(Snake snake2 : snakes) {


					int x2 = snake2.getPositions().get(0).getX();
					int y2 = snake2.getPositions().get(0).getY();		

					if(snake1.getId() != snake2.getId()) {

						if( x2 == snake1.getPositions().get(0).getX() &&  y2 == snake1.getPositions().get(0).getY() ) {

							if(snake1.getSize() <= snake2.getSize() ) {
								snake1.setToRemove(true);

							}

						}

					} 

					for(int i = 1; i < snake1.getPositions().size(); i++) {

						if( x2 == snake1.getPositions().get(i).getX() &&  y2 == snake1.getPositions().get(i).getY() ) {

							snake1.setToRemove(true);
						}
					}


				}

			}

		}

	}

	public void checkWalls() {

		for(Snake snake1 : snakes) {

			if(snake1.getInvincibleTimer() < 1) {
				
				int x = snake1.getPositions().get(0).getX()%this.sizeX;
				int y = snake1.getPositions().get(0).getY()%this.sizeY;
	
				if(walls[x][y]) {
	
					snake1.setToRemove(true);
	
				}
			}
		}


	}


	public void removeSnake() {

		ListIterator<Snake> iterSnake = snakes.listIterator();

		while(iterSnake.hasNext()){

			Snake snake = iterSnake.next();

			if(snake.isToRemove()) {

				iterSnake.remove();

			}

		}	

	}




	public void updateSnakeTimers() {

		ListIterator<Snake> iter = snakes.listIterator();

		while(iter.hasNext()){
			
			Snake snake = iter.next();

			if(snake.getInvincibleTimer() > 0) {

				snake.setInvincibleTimer(snake.getInvincibleTimer() - 1);
			}

			if(snake.getSickTimer() > 0) {

				snake.setSickTimer(snake.getSickTimer() - 1);
			}

		}

	}


	public ArrayList<Item> getItems() {
		return items;
	}

	private boolean walls[][];



	public boolean[][] getWalls() {
		return walls;

	}


	public AgentAction getInputMoveHuman1() {
		return inputMoveHuman1;
	}

	public void setInputMoveHuman1(AgentAction inputMoveHuman1) {
		this.inputMoveHuman1 = inputMoveHuman1;
	}

	public ArrayList<Snake> getSnakes() {
		return snakes;
	}

	public void setSnakes(ArrayList<Snake> snakes) {
		this.snakes = snakes;
	}

	public int getSizeX() {
		return sizeX;
	}

	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}

}
