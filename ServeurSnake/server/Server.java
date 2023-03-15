package server;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import client.Human;
import controller.ControllerSnakeGame;
import instance.Connection;
import instance.Lobby;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> clients;
  private int idGenerator = 0;
  private Lobby lobby;
  private boolean gameIsLaunched;
  private ArrayList<Human> playersInGame;
  private ControllerSnakeGame controller;
  private String levelAI = "Advanced";

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Starting server");
      clients = new ArrayList<>();
      playersInGame = new ArrayList<>();
      gameIsLaunched = false;
      if (this.isGameInitialised()) {
        lobby = new Lobby(new ArrayList<>(), this.controller.getGame().getLayout());
      } else {
        lobby = new Lobby(new ArrayList<>(), null);
      }
      while (true) {
        Connection connection = new Connection(serverSocket.accept(), this, idGenerator++);
        clients.add(connection);
        connection.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendInfoToClients(String msg) {
    for (Connection connection : clients) {
      connection.sendInfoToClient(msg);
    }
  }

  public void sendDataToClients(String msg) {
    for (Connection connection : clients) {
      connection.sendDataToClient(msg);
    }
  }

  public void sendInfoToPlayers(String msg) {
    for (Human player : lobby.getPlayers()) {
      player.getClient().sendInfoToClient(msg);
    }
  }

  public void sendDataToPlayers(String msg) {
    for (Human player : lobby.getPlayers()) {
      player.getClient().sendDataToClient(msg);
    }
  }

  public boolean isInLobby(Connection client) {
    for (int i = 0; i < this.lobby.getPlayers().size(); ++i) {
      if (this.lobby.getPlayers().get(i).isClient(client))
        return true;
    }
    return false;
  }

  public void sendLobbyInfoToClients() {
    for (Connection client : clients) {
      client.sendLobbyInfoToClient();
    }
  }

  public boolean isGameInitialised() {
    return this.controller != null;
  }

  public void loadGame() {
    this.controller.setPlayers(lobby.getPlayers());
    this.controller.setLevelAI(levelAI);
    this.lobby.setMap(this.controller.getInputMap().getFilename());
  }

  public void launchGame() {
    this.gameIsLaunched = true;
  }

  public boolean isGameLaunched() {
    return gameIsLaunched;
  }

  public ControllerSnakeGame getController() {
    return this.controller;
  }

  public Lobby getLobby() {
    return this.lobby;
  }

  public Human getPlayer(Connection client) {
    for (int i = 0; i < this.lobby.getPlayers().size(); ++i) {
      if (this.lobby.getPlayers().get(i).isClient(client))
        return this.lobby.getPlayers().get(i);
    }
    return null;
  }

  public List<Connection> getClients() {
    return this.clients;
  }

  public void removeClient(Connection client) {
    clients.remove(client);
    lobby.removePlayer(getPlayer(client));
    if (clients.isEmpty()) {
      this.controller = null;
      this.lobby.setMap(null);
    }
  }

  public List<Human> getPlayersInGame() {
    return playersInGame;
  }

  public void addPlayerInGame(Human player) {
    playersInGame.add(player);
  }

  public void removePlayerInGame(Human player) {
    playersInGame.remove(player);
    if (this.playersInGame.isEmpty()) {
      System.out.println("Every player left the game, the game terminated");
      stopGame();
    }
  }

  public void stopGame() {
    this.controller = null;
    gameIsLaunched = false;
    this.lobby.reset();
    sendLobbyInfoToClients();
  }

  public void updateLobby() {
    this.lobby.setMap(this.controller != null ? this.controller.getInputMap().getFilename() : null);
  }

  public void setController(ControllerSnakeGame controller) {
    this.controller = controller;
  }

  public String getLevelAI() {
    return levelAI;
  }

  public void setLevelAI(String levelAI) {
    this.levelAI = levelAI;
  }

}
