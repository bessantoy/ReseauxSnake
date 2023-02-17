package utils;

import java.net.InetAddress;
import java.util.ArrayList;

public class LobbyFeatures {
  private ArrayList<HumanFeatures> players;
  private String map;

  public LobbyFeatures(ArrayList<HumanFeatures> players, String map) {
    this.players = players;
    this.map = map;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public ArrayList<HumanFeatures> getPlayers() {
    return players;
  }

  public void setPlayers(ArrayList<HumanFeatures> players) {
    this.players = players;
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