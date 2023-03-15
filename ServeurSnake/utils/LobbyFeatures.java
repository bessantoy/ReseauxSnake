package utils;

import java.util.ArrayList;
import java.util.List;

public class LobbyFeatures {
  private ArrayList<HumanFeatures> players;
  private String map;

  public LobbyFeatures(List<HumanFeatures> players, String map) {
    this.players = (ArrayList<HumanFeatures>) players;
    this.map = map;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public List<HumanFeatures> getPlayers() {
    return players;
  }

  public void setPlayers(List<HumanFeatures> players) {
    this.players = (ArrayList<HumanFeatures>) players;
  }

  public boolean isGameInitialised() {
    return map != null;
  }

  public boolean isClientInLobby(int id) {
    for (HumanFeatures client : players) {
      if (client.getId() == id) {
        return true;
      }
    }
    return false;
  }

  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }

}