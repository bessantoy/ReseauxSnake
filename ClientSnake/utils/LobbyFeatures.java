package utils;

import java.util.ArrayList;

import network.Human;

public class LobbyFeatures {
  private ArrayList<Human> players;

  public LobbyFeatures(ArrayList<Human> players) {
    this.players = players;
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public ArrayList<Human> getPlayers() {
    return players;
  }

  public void setPlayers(ArrayList<Human> players) {
    this.players = players;
  }

}