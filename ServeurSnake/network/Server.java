package network;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import controller.ControllerSnakeGame;
import model.Lobby;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> clients;
  private Lobby lobby;
  private ControllerSnakeGame controller;
  private String levelAI = "Advanced";

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Starting server");
      clients = new ArrayList<>();
      lobby = new Lobby(new ArrayList<>());
      while (true) {
        Connection connection = new Connection(serverSocket.accept(), this);
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

  public void sendMessageToPlayers(String msg) {
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
