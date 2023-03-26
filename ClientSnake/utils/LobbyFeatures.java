package utils;

import java.util.ArrayList;
import java.util.List;

public class LobbyFeatures {
  private ArrayList<HumanFeatures> players;
  private ArrayList<Integer> lobbies;
  private GameInstanceFeatures gameInstanceFeatures;

  public LobbyFeatures(List<HumanFeatures> players, List<Integer> lobbies, GameInstanceFeatures gameInstanceFeatures) {
    this.players = (ArrayList<HumanFeatures>) players;
    this.lobbies = (ArrayList<Integer>) lobbies;
    this.gameInstanceFeatures = gameInstanceFeatures;
  }

  public boolean isEmpty() {
    return players.isEmpty();
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