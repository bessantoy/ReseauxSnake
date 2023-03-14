package model;

import java.util.ArrayList;

import network.Human;
import utils.HumanFeatures;
import utils.LobbyFeatures;

public class Lobby {
  private ArrayList<Human> players;
  private String map;
  private boolean gameLaunched;

  public Lobby(ArrayList<Human> players, String map) {
    this.players = players;
    this.map = map;
    this.gameLaunched = false;
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
    return new LobbyFeatures(humanFeatures, map);
  }

  public String getMap() {
    return this.map;
  }

  public boolean isGameInitialised() {
    return map != null;
  }

  public void setMap(String map) {
    this.map = map;
  }

  public boolean isGameLaunched() {
    return gameLaunched;
  }

  public void setGameLaunched(boolean gameLaunched) {
    this.gameLaunched = gameLaunched;
  }

}