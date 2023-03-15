package instance;

import java.util.ArrayList;
import java.util.List;

import client.Human;
import utils.HumanFeatures;
import utils.LobbyFeatures;

public class Lobby {
  private ArrayList<Human> players;
  private String map;

  public Lobby(List<Human> players, String map) {
    this.players = (ArrayList<Human>) players;
    this.map = map;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public List<Human> getPlayers() {
    return players;
  }

  public void removePlayer(Human player) {
    players.remove(player);
  }

  public void addPlayer(Human player) {
    players.add(player);
  }

  public void removeAllPlayers() {
    players.clear();
  }

  public void reset() {
    removeAllPlayers();
    setMap(null);
  }

  public LobbyFeatures toLobbyFeatures() {
    ArrayList<HumanFeatures> humanFeatures = new ArrayList<>();
    for (Human human : players) {
      humanFeatures.add(human.toHumanFeatures());
    }
    return new LobbyFeatures(humanFeatures, map);
  }

  public String getMap() {
    return this.map;
  }

  public void setMap(String map) {
    this.map = map;
  }
}