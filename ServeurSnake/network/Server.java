package network;

import java.net.*;
import java.util.ArrayList;

import controller.ControllerSnakeGame;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> clients;
  private ArrayList<Player> players;
  private ControllerSnakeGame controller;

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Starting server");
      clients = new ArrayList<>();
      players = new ArrayList<>();
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
    for (Player player : players) {
      player.getClient().sendDataToClient(msg);
    }
  }

  public boolean isInLobby(Connection client) {
    for (int i = 0; i < this.players.size(); ++i) {
      if (this.players.get(i).isClient(client))
        return true;
    }
    return false;
  }

  public ControllerSnakeGame getController() {
    return this.controller;
  }

  public ArrayList<Player> getPlayers() {
    return this.players;
  }

  public Player getPlayer(Connection client) {
    for (int i = 0; i < this.getPlayers().size(); ++i) {
      if (this.getPlayers().get(i).isClient(client))
        return this.getPlayers().get(i);
    }
    return null;
  }

  public ArrayList<Connection> getClients() {
    return this.clients;
  }

  public void setController(ControllerSnakeGame controller) {
    this.controller = controller;
  }

}
