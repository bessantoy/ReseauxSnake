package instance;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import client.Human;
import server.Server;
import utils.HumanFeatures;
import utils.LobbyFeatures;

public class Lobby extends Thread {
  private ArrayList<Human> players;
  private GameInstance gameInstance;
  private Server server;

  private int id;

  public Lobby(Server server, int id) {
    this.server = server;
    this.players = new ArrayList<>();
    gameInstance = new GameInstance(this);
    this.id = id;
  }

  public int getLobbyId() {
    return id;
  }

  public void run() {
    if (gameInstance == null) {
      gameInstance = new GameInstance(this);
    }
  }

  public boolean isEmpty() {
    return players.isEmpty();
  }

  public boolean isGameInitialised() {
    return this.gameInstance.isGameInitialised();
  }

  public List<Human> getPlayers() {
    return players;
  }

  public Human getPlayer(Connection client) {
    for (int i = 0; i < players.size(); ++i) {
      if (players.get(i).isClient(client))
        return players.get(i);
    }
    return null;
  }

  public boolean isInLobby(Connection client) {
    for (int i = 0; i < getPlayers().size(); ++i) {
      if (getPlayers().get(i).isClient(client))
        return true;
    }
    return false;
  }

  public void addPlayer(Human player) {
    players.add(player);
  }

  public void removePlayer(Human player) {
    players.remove(player);
  }

  public void removePlayer(Connection client) {
    players.remove(getPlayer(client));
  }

  public void removeAllPlayers() {
    players.clear();
  }

  public void reset() {
    removeAllPlayers();
  }

  public void sendLobbyStatusToPlayers() {
    for (Human player : players) {
      sendLobbyStatusToClient(player.getClient());
    }
  }

  public void sendLobbyStatusToClient(Connection client) {
    Gson gson = new Gson();
    client.sendLobbyDataToClient("UPDATE#" + gson.toJson(this.getLobbyFeatures()));
  }

  public void sendInfoToPlayers(String msg) {
    for (Human player : players) {
      player.getClient().sendInfoToClient(msg);
    }
  }

  public void sendGameDataToPlayers(String msg) {
    for (Human player : players) {
      player.getClient().sendGameDataToClient(msg);
    }
  }

  public List<HumanFeatures> getHumanFeatures() {
    List<HumanFeatures> humanFeatures = new ArrayList<>();
    for (Human player : players) {
      humanFeatures.add(player.getHumanFeatures());
    }
    return humanFeatures;
  }

  public LobbyFeatures getLobbyFeatures() {
    if (gameInstance == null)
      return new LobbyFeatures(this.id, getHumanFeatures(), this.server.getLobbiesIds(), null);
    return new LobbyFeatures(this.id, getHumanFeatures(), this.server.getLobbiesIds(),
        gameInstance.getGameInstanceFeatures());
  }

  public Server getServer() {
    return server;
  }

  public GameInstance getGameInstance() {
    return gameInstance;
  }
}