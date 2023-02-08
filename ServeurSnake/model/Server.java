package model;

import java.net.*;
import java.util.ArrayList;

import controller.ControllerSnakeGame;

import com.google.gson.*;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> connections;
  private ArrayList<Connection> players;
  private ControllerSnakeGame controller;

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Starting server");
      connections = new ArrayList<>();
      players = new ArrayList<>();
      while (true) {
        Connection connection = new Connection(serverSocket.accept(), this);
        connections.add(connection);
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
    for (Connection connection : connections) {
      connection.sendInfoToClient(msg);
    }
  }

  public void sendMessageToClients(String msg) {
    for (Connection connection : connections) {
      connection.sendMessageToClient(msg);
    }
  }

  public void sendMessageToPlayers(String msg) {
    for (Connection player : players) {
      player.sendMessageToClient(msg);
    }
  }

  public ControllerSnakeGame getController() {
    return this.controller;
  }

  public static class Connection extends Thread {
    private Server server;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    public Connection(Socket socket, Server server) {
      this.client = socket;
      this.server = server;
    }

    public void sendMessageToClient(String msg) {
      this.out.println(msg);
    }

    public void sendInfoToClient(String msg) {
      this.out.println("Server : " + msg);
    }

    public void sendJSON(String json) {
      out.println("#JSON#" + json);
    }

    public void sendGameUpdate() {
      Gson gson = new Gson();

      String json = gson.toJson(this.server.getController().getGameFeatures());
      sendJSON(json);
    }

    public void run() {
      try {
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        System.out.println("new client connected");

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
          if (inputLine.startsWith("#VC#")) {
            handleViewCommandSignals(inputLine);
          } else if (inputLine.startsWith("#UPDATE#")) {
            if (this.server.getController() != null) {
              sendGameUpdate();
            } else {
              this.sendMessageToClient("No game started");
            }
          } else if (inputLine.startsWith("init")) {
            String layout = inputLine.substring(5);
            initGame(layout);
          } else if (inputLine.equals("join")) {
            joinLobby();
          } else if (inputLine.equals("launch")) {
            if (this.server.getController() != null) {
              if (this.server.players.size() > 1) {
                this.server.getController().play();
                this.server.sendMessageToPlayers("#LAUNCH#");
              } else {
                this.sendMessageToClient("Not enough players");
              }
            } else {
              this.sendMessageToClient("No game initialised");
            }
          } else if (inputLine.equals("stop")) {
            if (this.server.getController() != null) {
              this.server.getController().pause();
              this.server.sendMessageToPlayers("#PAUSE#");
            } else {
              this.sendMessageToClient("No game started");
            }
          } else if (inputLine.equals("quit")) {
            this.server.connections.remove(this);
            this.server.players.remove(this);
            this.server.sendMessageToClients("Client disconnected");
            break;
          } else {
            this.sendMessageToClient("Unknown command : " + inputLine);
          }
        }
        in.close();
        out.close();
        client.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void initGame(String layout) {
      if (new File("./layouts/" + layout + ".lay").exists()) {
        this.server.controller = new ControllerSnakeGame("layouts/" + layout + ".lay");
        System.out.println("Game started with layout : " + layout);
        if (this.server.getController().getGameFeatures().getFeaturesSnakes().size() < this.server.players
            .size()) {
          this.server.players.clear();
          this.server.sendMessageToClients("Lobby reseted, please join again");
        }
        this.server.sendInfoToClients("Game initialised with layout : " + layout);
      } else {
        this.sendMessageToClient("Unknown layout : " + layout);
      }
    }

    private void joinLobby() {
      if (this.server.getController() != null) {
        if (this.server.getController().getGameFeatures().getFeaturesSnakes().size() >= this.server.players.size()) {
          this.server.players.add(this);
          this.sendInfoToClient("You joined the lobby");
        } else {
          this.sendMessageToClient("Game is full");
        }
      } else {
        this.sendMessageToClient("No game started");
      }

    }

    public void handleViewCommandSignals(String signal) {
      signal = signal.substring(4);
      System.out.println("Signal received : " + signal);
      switch (signal) {
        case "PAUSE":
          this.server.getController().pause();
          break;
        case "RESUME":
          this.server.sendMessageToPlayers("#RESUME#");
          this.server.getController().play();
          break;
        case "STEP":
          this.server.getController().step();
          this.server.sendMessageToPlayers("#STEP#");
          break;
        case "RESTART":
          this.server.sendMessageToPlayers("#RESUME#");
          this.server.getController().restart();
          break;
        case "SPEED":
          try {
            String response = in.readLine();
            response = response.substring(4);
            this.server.getController().setSpeed(Double.parseDouble(response));
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        case "UP":
          int index = this.server.players.indexOf(this);
          if (index > 0) {
            this.server.getController().setDirectionPlayerUp(index);
          }
          break;
        case "DOWN":
          index = this.server.players.indexOf(this);
          if (index > 0) {
            this.server.getController().setDirectionPlayerDown(index);
          }
          break;
        case "LEFT":
          index = this.server.players.indexOf(this);
          if (index > 0) {
            this.server.getController().setDirectionPlayerLeft(index);
          }
          break;
        case "RIGHT":
          index = this.server.players.indexOf(this);
          if (index > 0) {
            this.server.getController().setDirectionPlayerRight(index);
          }
          break;
        default:
          System.out.println("Unknown signal");
          break;
      }
    }
  }
}
