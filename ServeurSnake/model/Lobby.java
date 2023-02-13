package model;

import java.util.ArrayList;

import network.Human;
import utils.HumanFeatures;
import utils.LobbyFeatures;

public class Lobby {
  private ArrayList<Human> players;

  public Lobby(ArrayList<Human> players) {
    this.players = players;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public ArrayList<Human> getPlayers() {
    return players;
  }

  public void removePlayer(Human player) {
    players.remove(player);
  }

  public void addPlayer(Human player) {
    players.add(player);
  }

  public void clear() {
    players.clear();
  }

  public LobbyFeatures toLobbyFeatures() {
    ArrayList<HumanFeatures> humanFeatures = new ArrayList<>();
    for (Human human : players) {
      humanFeatures.add(human.toHumanFeatures());
    }
    return new LobbyFeatures(humanFeatures);
  }

}