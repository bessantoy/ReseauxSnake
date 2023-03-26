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

  public GameFeatures(boolean[][] walls, int sizeX, int sizeY, ArrayList<FeaturesSnake> featuresSnakes,
      ArrayList<FeaturesItem> featuresItems, GameState state, int turn, long speed) {
    this.walls = walls;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.featuresSnakes = featuresSnakes;
    this.featuresItems = featuresItems;
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

}
