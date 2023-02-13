package utils;

import java.util.ArrayList;

public class LobbyFeatures {
  private ArrayList<HumanFeatures> players;

  public LobbyFeatures(ArrayList<HumanFeatures> players) {
    this.players = players;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public ArrayList<HumanFeatures> getPlayers() {
    return players;
  }

}