package utils;

import java.util.ArrayList;
import java.util.Observable;

public class GameFeatures extends Observable {
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

  public ArrayList<FeaturesSnake> getFeaturesSnakes() {
    return featuresSnakes;
  }

  public ArrayList<FeaturesItem> getFeaturesItems() {
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

  public void setFeaturesSnakes(ArrayList<FeaturesSnake> featuresSnakes) {
    this.featuresSnakes = featuresSnakes;
  }

  public void setFeaturesItems(ArrayList<FeaturesItem> featuresItems) {
    this.featuresItems = featuresItems;
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

}
