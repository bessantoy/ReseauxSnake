package server;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import instance.Connection;
import instance.Lobby;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> clients;
  private ArrayList<Lobby> lobbies;
  private int idGeneratorClient = 0;
  private int idGeneratorLobby = 0;

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Server is running");
      clients = new ArrayList<>();
      lobbies = new ArrayList<>();

      while (true) {
        Connection connection = new Connection(serverSocket.accept(), this, idGeneratorClient++);
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

  public void sendCliDataToClients(String msg) {
    for (Connection connection : clients) {
      connection.sendCliDataToClient(msg);
    }
  }

  public void sendCliStatusToClient(Connection client) {
    Gson gson = new Gson();
    if (client.isInLobby()) {
      getClientLobby(client).sendLobbyStatusToClient(client);
    } else {
      if (this.isLobbiesEmpty()) {
        client.sendCliDataToClient("UPDATE#EMPTY");
      } else {
        client.sendCliDataToClient("UPDATE#" + gson.toJson(this.getLobbiesIds()));
      }
    }
  }

  public void sendCliStatusToClients() {
    for (Connection connection : clients) {
      sendCliStatusToClient(connection);
    }
  }

  public List<Connection> getClients() {
    return this.clients;
  }

  public Lobby getClientLobby(Connection client) {
    for (Lobby lobby : lobbies) {
      if (lobby.isInLobby(client))
        return lobby;
    }
    return null;
  }

  public void removeClient(Connection client) {
    clients.remove(client);
    if (client.isInLobby()) {
      Lobby lobby = getClientLobby(client);
      if (lobby != null) {
        lobby.removePlayer(client);
      } else {
        System.out.println("Error: client is in lobby but his lobby can't be found");
      }
    }
  }

  public List<Integer> getLobbiesIds() {
    List<Integer> ids = new ArrayList<>();
    for (Lobby lobby : lobbies) {
      ids.add(lobby.getLobbyId());
    }
    return ids;
  }

  public Lobby getLobby(int id) {
    for (Lobby lobby : lobbies) {
      if (lobby.getLobbyId() == id)
        return lobby;
    }
    return null;
  }

  public boolean isLobbiesEmpty() {
    return lobbies.isEmpty();
  }

  public void removeLobby(Lobby lobby) {
    lobbies.remove(lobby);
  }

  public int addLobby() {
    Lobby lobby = new Lobby(this, idGeneratorLobby++);
    lobby.start();
    lobbies.add(lobby);
    return lobby.getLobbyId();
  }

}
