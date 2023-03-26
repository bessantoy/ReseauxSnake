package utils;

import java.util.ArrayList;
import java.util.List;

public class GameInstanceFeatures {
  private ArrayList<HumanFeatures> players;
  private String map;
  private String levelAI;

  public GameInstanceFeatures(List<HumanFeatures> players, String map, String levelAI) {
    this.players = (ArrayList<HumanFeatures>) players;
    this.map = map;
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

  public String getLevelAI() {
    return levelAI;
  }

}
