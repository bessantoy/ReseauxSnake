package utils;

import java.util.ArrayList;
import java.util.List;

public class LobbyFeatures {
  private int id;
  private ArrayList<HumanFeatures> players;
  private ArrayList<Integer> lobbies;
  private GameInstanceFeatures gameInstanceFeatures;

  public LobbyFeatures(int id, List<HumanFeatures> players, List<Integer> lobbies,
      GameInstanceFeatures gameInstanceFeatures) {
    this.id = id;
    this.players = (ArrayList<HumanFeatures>) players;
    this.lobbies = (ArrayList<Integer>) lobbies;
    this.gameInstanceFeatures = gameInstanceFeatures;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public int getId() {
    return id;
  }

  public List<HumanFeatures> getPlayers() {
    return players;
  }

  public GameInstanceFeatures getGameInstanceFeatures() {
    return gameInstanceFeatures;
  }

  public ArrayList<Integer> getLobbies() {
    return lobbies;
  }

  public void setPlayers(List<HumanFeatures> players) {
    this.players = (ArrayList<HumanFeatures>) players;
  }

  public boolean isGameInitialised() {
    if (this.gameInstanceFeatures == null)
      return false;
    return this.gameInstanceFeatures.getMap() != null;
  }

  public boolean isClientInLobby(int id) {
    for (HumanFeatures client : players) {
      if (client.getId() == id) {
        return true;
      }
    }
    return false;
  }
}