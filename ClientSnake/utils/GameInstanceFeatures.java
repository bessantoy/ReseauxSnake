package utils;

import java.util.ArrayList;
import java.util.List;

public class GameInstanceFeatures {
  private ArrayList<HumanFeatures> players;
  private String map;
  private String levelAI;
  private int playerCapacity;

  public GameInstanceFeatures(List<HumanFeatures> players, String map, int capacity, String levelAI) {
    this.players = (ArrayList<HumanFeatures>) players;
    this.map = map;
    this.playerCapacity = capacity;
    this.levelAI = levelAI;
  }

  public boolean isGameInitialised() {
    return map != null;
  }

  public List<HumanFeatures> getPlayers() {
    return players;
  }

  public void setMap(String map) {
    this.map = map;
  }

  public String getMap() {
    return map;
  }

  public int getPlayerCapacity() {
    return playerCapacity;
  }

  public String getLevelAI() {
    return levelAI;
  }

}
