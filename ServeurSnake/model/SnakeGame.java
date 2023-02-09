package model;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import agent.Snake;
import factory.SnakeFactory;

import item.Item;
import network.Human;
import network.AI;
import utils.AgentAction;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.GameFeatures;
import utils.ItemType;
import utils.Position;

public class SnakeGame extends Game {

	/// A revoir

	public static final int TIME_INVINCIBLE = 20;
	public static final int TIME_SICK = 20;

	double probSpecialItem = 1;

	private ArrayList<Snake> snakes;
	private ArrayList<Item> items;

	InputMap inputMap;

	private int sizeX;
	private int sizeY;

	private String layout;

	List<Human> players;
	List<AI> aiList;

	private String levelAI;

	Random rand = new Random();

	public SnakeGame(int maxTurn, InputMap inputMap, List<Human> players, String levelAI) {

		super(maxTurn);
		this.inputMap = inputMap;
		this.players = players;
		this.levelAI = levelAI;
		this.aiList = new ArrayList<>();

	}

	@Override
	public void initializeGame() {
		this.walls = inputMap.get_walls().clone();

		SnakeFactory snakeFactory = new SnakeFactory();

		ArrayList<FeaturesSnake> startSnakes = inputMap.getStart_snakes();
		ArrayList<FeaturesItem> startItems = inputMap.getStart_items();

		this.sizeX = inputMap.getSizeX();
		this.sizeY = inputMap.getSizeY();

		snakes = new ArrayList<>();
		items = new ArrayList<>();

		int iaCount = 0;

		for (int i = 0; i < startSnakes.size(); ++i) {
			FeaturesSnake featuresSnake = startSnakes.get(i);
			if (i < players.size()) {
				snakes.add(snakeFactory.createSnake(featuresSnake, i));
			} else {
				aiList.add(new AI("IA" + iaCount++, levelAI));
				snakes.add(snakeFactory.createSnake(featuresSnake, i, levelAI));
			}
		}

		for (FeaturesItem featuresItem : startItems) {
			items.add(new Item(featuresItem.getX(), featuresItem.getY(), featuresItem.getItemType()));
		}
	}

	@Override
	public void takeTurn() {

		ListIterator<Snake> iterSnakes = snakes.listIterator();

		while (iterSnakes.hasNext()) {

			Snake snake = iterSnakes.next();
			AgentAction agentAction = playSnake(snake);

			if (isLegalMove(snake, agentAction)) {
				moveSnake(agentAction, snake);
			} else {
				moveSnake(snake.getLastAction(), snake);
			}
		}

		checkSnakeEaten();
		checkWalls();

		boolean isAppleEaten = checkItemFound();

		if (isAppleEaten) {
			addRandomApple();
			double r = rand.nextDouble();

			if (r < probSpecialItem) {
				System.out.println("add random item");
				addRandomItem();
			}
		}
		removeSnake();

		updateSnakeTimers();
	}

	public boolean isLegalMove(Snake snake, AgentAction action) {

		return !(snake.getSize() > 1
				&& ((snake.getLastAction() == AgentAction.MOVE_DOWN && action == AgentAction.MOVE_UP) ||
						(snake.getLastAction() == AgentAction.MOVE_UP && action == AgentAction.MOVE_DOWN) ||
						(snake.getLastAction() == AgentAction.MOVE_LEFT && action == AgentAction.MOVE_RIGHT) ||
						(snake.getLastAction() == AgentAction.MOVE_RIGHT && action == AgentAction.MOVE_LEFT)));
	}

	@Override
	public boolean gameContinue() {
		return !snakes.isEmpty();
	}

	@Override
	public void gameOver() {

		System.out.println("Game over");
	}

	public void addRandomApple() {

		boolean notPlaced = true;

		while (notPlaced) {

			int x = rand.nextInt(this.inputMap.getSizeX());
			int y = rand.nextInt(this.inputMap.getSizeY());

			if (!this.walls[x][y] && !isSnake(x, y) && !isItem(x, y)) {

				this.items.add(new Item(x, y, ItemType.APPLE));
				notPlaced = false;
			}
		}
	}

	public void addRandomItem() {

		int r = rand.nextInt(3);

		ItemType itemType = null;

		if (r == 0) {
			itemType = ItemType.BOX;
		} else if (r == 1) {
			itemType = ItemType.SICK_BALL;
		} else if (r == 2) {
			itemType = ItemType.INVINCIBILITY_BALL;
		}

		boolean notPlaced = true;

		while (notPlaced) {
			int x = rand.nextInt(this.inputMap.getSizeX());
			int y = rand.nextInt(this.inputMap.getSizeY());

			if (!this.walls[x][y] && !isSnake(x, y) && !isItem(x, y)) {

				this.items.add(new Item(x, y, itemType));
				notPlaced = false;
			}
		}
	}

	public boolean isSnake(int x, int y) {
		for (Snake snake : snakes) {

			for (Position pos : snake.getPositions()) {

				if (pos.getX() == x && pos.getY() == y) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isItem(int x, int y) {

		for (Item item : items) {
			if (item.getX() == x && item.getY() == y) {
				return true;
			}
		}
		return false;
	}

	public boolean checkItemFound() {

		ListIterator<Item> iterItem = items.listIterator();
		boolean isAppleEaten = false;
		while (iterItem.hasNext()) {
			Item item = iterItem.next();
			for (Snake snake : snakes) {
				if (snake.getSickTimer() < 1) {

					int x = snake.getPositions().get(0).getX();
					int y = snake.getPositions().get(0).getY();

					if (item.getX() == x && item.getY() == y) {

						iterItem.remove();

						if (item.getItemType() == ItemType.APPLE) {
							increaseSizeSnake(snake);
							isAppleEaten = true;
						}

						if (item.getItemType() == ItemType.BOX) {
							double r = rand.nextDouble();
							if (r < 0.5) {
								snake.setInvincibleTimer(TIME_INVINCIBLE);

							} else {
								snake.setSickTimer(TIME_SICK);
							}
						}

						if (item.getItemType() == ItemType.SICK_BALL) {
							snake.setSickTimer(SnakeGame.TIME_SICK);
						}

						if (item.getItemType() == ItemType.INVINCIBILITY_BALL) {
							snake.setInvincibleTimer(SnakeGame.TIME_INVINCIBLE);
						}
					}
				}
			}
		}
		return isAppleEaten;
	}

	public void checkSnakeEaten() {

		for (Snake snake1 : snakes) {
			if (snake1.getInvincibleTimer() < 1) {
				for (Snake snake2 : snakes) {
					int x2 = snake2.getPositions().get(0).getX();
					int y2 = snake2.getPositions().get(0).getY();

					if ((snake1.getId() != snake2.getId())
							&& (x2 == snake1.getPositions().get(0).getX() && y2 == snake1.getPositions().get(0).getY())
							&& (snake1.getSize() <= snake2.getSize())) {
						snake1.setToRemove(true);
					}

					for (int i = 1; i < snake1.getPositions().size(); i++) {
						if (x2 == snake1.getPositions().get(i).getX() && y2 == snake1.getPositions().get(i).getY()) {
							snake1.setToRemove(true);
						}
					}
				}
			}
		}
	}

	public void checkWalls() {
		for (Snake snake1 : snakes) {
			if (snake1.getInvincibleTimer() < 1) {

				int x = snake1.getPositions().get(0).getX() % this.sizeX;
				int y = snake1.getPositions().get(0).getY() % this.sizeY;

				if (walls[x][y]) {
					snake1.setToRemove(true);
				}
			}
		}
	}

	public void removeSnake() {
		ListIterator<Snake> iterSnake = snakes.listIterator();

		while (iterSnake.hasNext()) {
			Snake snake = iterSnake.next();
			if (snake.isToRemove()) {
				iterSnake.remove();
			}
		}
	}

	public AgentAction playSnake(Snake snake) {
		return snake.getStrategy().chooseAction(snake, this);
	}

	public void moveSnake(AgentAction action, Snake snake) {

		List<Position> positions = snake.getPositions();
		Position head = positions.get(0);

		// Store old tail position
		snake.setOldTailX(positions.get(positions.size() - 1).getX());
		snake.setOldTailY(positions.get(positions.size() - 1).getY());

		// Move body
		if (positions.size() > 1) {
			for (int i = 1; i < positions.size(); i++) {

				positions.get(positions.size() - i).setX(positions.get(positions.size() - i - 1).getX());
				positions.get(positions.size() - i).setY(positions.get(positions.size() - i - 1).getY());

			}
		}

		// Move head
		switch (action) {
			case MOVE_UP:
				int y = positions.get(0).getY();
				if (y > 0) {
					head.setY(positions.get(0).getY() - 1);
				} else {
					head.setY(this.getSizeY() - 1);
				}
				break;
			case MOVE_DOWN:
				head.setY((positions.get(0).getY() + 1) % this.getSizeY());
				break;
			case MOVE_RIGHT:
				head.setX((positions.get(0).getX() + 1) % this.getSizeX());
				break;
			case MOVE_LEFT:
				int x = positions.get(0).getX();

				if (x > 0) {
					head.setX(positions.get(0).getX() - 1);
				} else {
					head.setX(this.getSizeX() - 1);
				}
				break;

			default:
				break;
		}
		snake.setLastAction(action);
	}

	public void increaseSizeSnake(Snake snake) {
		snake.getPositions().add(new Position(snake.getOldTailX(), snake.getOldTailY()));
	}

	public void updateSnakeTimers() {

		ListIterator<Snake> iter = snakes.listIterator();

		while (iter.hasNext()) {
			Snake snake = iter.next();

			if (snake.getInvincibleTimer() > 0) {
				snake.setInvincibleTimer(snake.getInvincibleTimer() - 1);
			}

			if (snake.getSickTimer() > 0) {
				snake.setSickTimer(snake.getSickTimer() - 1);
			}
		}
	}

	public GameFeatures toGameFeatures() {
		ArrayList<FeaturesSnake> snakesFeature = new ArrayList<>();
		ArrayList<FeaturesItem> itemsFeature = new ArrayList<>();
		for (Snake snake : this.snakes) {
			snakesFeature.add(snake.toFeaturesSnake());
		}
		for (Item item : this.items) {
			itemsFeature.add(item.toFeaturesItem());
		}
		return new GameFeatures(walls, sizeX, sizeY, snakesFeature, itemsFeature, getState(), getTurn(), getTime());
	}

	public List<Human> getPlayers() {
		return players;
	}

	public void setPlayers(List<Human> players) {
		this.players = players;
	}

	public List<Item> getItems() {
		return items;
	}

	private boolean[][] walls;

	public boolean[][] getWalls() {
		return walls;

	}

	public List<Snake> getSnakes() {
		return snakes;
	}

	public void setSnakes(List<Snake> snakes) {
		this.snakes = new ArrayList<>(snakes);
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

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

}
