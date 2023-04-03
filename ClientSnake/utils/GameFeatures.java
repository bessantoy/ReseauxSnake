package utils;

import java.util.ArrayList;
import java.util.List;

public class GameFeatures {
  private boolean[][] walls;
  private int sizeX;
  private int sizeY;
  private ArrayList<FeaturesSnake> featuresSnakes;
  private ArrayList<FeaturesItem> featuresItems;
  private GameState state;
  private int turn;
  private long speed;

  public GameFeatures(boolean[][] walls, int sizeX, int sizeY, List<FeaturesSnake> featuresSnakes,
      List<FeaturesItem> featuresItems, GameState state, int turn, long speed) {
    this.walls = walls;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.featuresSnakes = (ArrayList<FeaturesSnake>) featuresSnakes;
    this.featuresItems = (ArrayList<FeaturesItem>) featuresItems;
    this.state = state;
    this.turn = turn;
    this.speed = speed;
  }

  public boolean[][] getWalls() {
    return walls;
  }

  public int getSizeX() {
    return sizeX;
  }

  public int getSizeY() {
    return sizeY;
  }

  public List<FeaturesSnake> getFeaturesSnakes() {
    return featuresSnakes;
  }

  public List<FeaturesItem> getFeaturesItems() {
    return featuresItems;
  }

  public GameState getState() {
    return state;
  }

  public int getTurn() {
    return turn;
  }

  public long getSpeed() {
    return speed;
  }

  public void setWalls(boolean[][] walls) {
    this.walls = walls;
  }

  public void setSizeX(int sizeX) {
    this.sizeX = sizeX;
  }

  public void setSizeY(int sizeY) {
    this.sizeY = sizeY;
  }

  public void setFeaturesSnakes(List<FeaturesSnake> featuresSnakes) {
    this.featuresSnakes = (ArrayList<FeaturesSnake>) featuresSnakes;
  }

  public void setFeaturesItems(List<FeaturesItem> featuresItems) {
    this.featuresItems = (ArrayList<FeaturesItem>) featuresItems;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public void setSpeed(long speed) {
    this.speed = speed;
  }

  public int getPlayerScore(int id) {
    for (FeaturesSnake featuresSnake : featuresSnakes) {
      if (featuresSnake.getId() == id)
        return featuresSnake.getScore();
    }
    return -1;
  }

}
