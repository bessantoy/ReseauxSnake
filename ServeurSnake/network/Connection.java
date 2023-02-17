package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import controller.ControllerSnakeGame;
import utils.AgentAction;
import utils.GameFeatures;

public class Connection extends Thread {
  private Server server;
  private Socket client;
  private int id;

  private PrintWriter out;
  private BufferedReader in;

  public Connection(Socket socket, Server server, int id) {
    this.client = socket;
    this.server = server;
    this.id = id;
  }

  public void sendDataToClient(String msg) {
    this.out.println(msg);
  }

  public void sendInfoToClient(String msg) {
    this.out.println("Server : " + msg);
  }

  public void sendGameUpdate() {
    if (this.server.isGameInitialised()) {
      Gson gson = new Gson();
      String update = gson.toJson(this.server.getController().getGameFeatures(), GameFeatures.class);
      sendDataToClient("GAMEUPDATE#" + update);
    } else {
      this.sendInfoToClient("No game started");
    }
  }

  public void run() {
    try {
      out = new PrintWriter(client.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      System.out.println("new client connected");
      out.println("CONNECTION#" + id);
      boolean connected = true;
      String inputLine;
      while (connected) {
        inputLine = in.readLine();
        connected = this.handleCommand(inputLine);
      }
      in.close();
      out.close();
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean handleCommand(String inputLine) {
    if (inputLine.startsWith("LBY#")) {
      return handleLobbySignal(inputLine);
    } else if (inputLine.startsWith("VC#")) {
      handleViewCommandSignals(inputLine);
    } else if (inputLine.startsWith("MV#")) {
      handleMovementSignal(inputLine);
    } else if (inputLine.equals("GAMEUPDATE")) {
      System.out.println("Update view");
      this.sendGameUpdate();
    }
    return true;
  }

  private void handleViewCommandSignals(String signal) {
    signal = signal.substring(3);
    System.out.println("Signal received : " + signal);
    if (this.server.isGameInitialised()) {
      switch (signal) {
        case "PAUSE":
          this.server.getController().pause();
          break;
        case "RESUME":
          this.server.getController().play();
          this.server.sendMessageToPlayers("RESUME");

          break;
        case "STEP":
          this.server.getController().step();
          this.server.sendMessageToPlayers("STEP");
          break;
        case "RESTART":
          this.server.sendMessageToPlayers("RESTART");
          this.server.getController().restart();
          break;
        case "SPEED":
          try {
            String response = in.readLine();
            response = response.substring(3);
            Double speed = Double.parseDouble(response);
            if (this.server.getController().getSpeed() != speed) {
              this.server.getController().setSpeed(speed);
              this.server.sendMessageToPlayers("SPEED#" + speed.toString());
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          break;
        default:
          System.out.println("Unknown signal");
          break;
      }
    } else {
      System.out.println("Game not initialised");
    }
  }

  private void handleMovementSignal(String signal) {
    signal = signal.substring(3);
    System.out.println("Signal received : " + signal);
    switch (signal) {
      case "UP":
        this.server.getPlayer(this).setLastInput(AgentAction.MOVE_UP);
        break;
      case "DOWN":
        this.server.getPlayer(this).setLastInput(AgentAction.MOVE_DOWN);
        break;
      case "LEFT":
        this.server.getPlayer(this).setLastInput(AgentAction.MOVE_LEFT);
        break;
      case "RIGHT":
        this.server.getPlayer(this).setLastInput(AgentAction.MOVE_RIGHT);
        break;
      default:
        System.out.println("Wrong movement");
    }
  }

  private boolean handleLobbySignal(String signal) {
    signal = signal.substring(4);
    if (signal.equals("INIT")) {
      initGame("arena");
    } else if (signal.startsWith("INIT#")) {
      String layout = signal.substring(5);
      initGame(layout);
    } else if (signal.startsWith("UPDATE")) {
      this.sendLobbyInfoToClient();
    } else if (signal.startsWith("JOIN#")) {
      joinLobby(signal);
    } else if (signal.equals("LAUNCH")) {
      this.handleLaunch();
    } else if (signal.equals("ijl")) {
      initGame("arena");
      joinLobby("Anonym");
      this.handleLaunch();
    } else if (signal.equals("LEAVE")) {
      this.handleLeaveLobby();
    } else if (signal.equals("reset lobby")) {
      this.server.getLobby().clear();
    } else if (signal.startsWith("level ")) {
      handleLevelChange(signal);
    } else if (signal.equals("EXIT")) {
      this.handleClientExit();
      return false;
    } else {
      this.sendInfoToClient("Unknown command : " + signal);
    }
    return true;
  }

  private void handleLaunch() {
    if (this.server.isGameInitialised()) {
      if (!this.server.getLobby().isEmpty()) {
        this.server.getController().initGame();
        this.server.sendMessageToPlayers("LAUNCH");
      } else {
        this.sendInfoToClient("Not enough players");
      }
    } else {
      this.sendInfoToClient("No game initialised");
    }
  }

  private void handleClientExit() {
    this.server.removeClient(this);
    System.out.println("Client disconnected");
    this.server.sendInfoToClients("Client disconnected");
  }

  private void initGame(String layout) {
    if (new File("./layouts/" + layout + ".lay").exists()) {
      this.server.setController(new ControllerSnakeGame(layout));
      this.server.loadGame();
      System.out.println("Game started with layout : " + layout);
      this.server.sendDataToClients("INITIALISED#" + layout);
      if (this.server.getController().getNumberOfPlayers() < this.server.getLobby().getPlayers()
          .size()) {
        this.server.getLobby().getPlayers().clear();
        this.server.sendDataToClients("Lobby reseted, please join again");
      }
    } else {
      this.sendDataToClient("Unknown layout : " + layout);
    }
  }

  private void joinLobby(String signal) {
    String name = signal.substring(5);
    if (this.server.isGameInitialised()) {
      if (!this.server.isInLobby(this)) {
        if (this.server.getController().getNumberOfPlayers() >= this.server.getLobby().getPlayers().size()) {
          this.server.getLobby().addPlayer(new Human(this, id, name));
          System.out.println("Player " + name + " joined the lobby");
          this.sendInfoToClient("You joined the lobby");
          this.server.sendLobbyInfoToClients();
        } else {
          this.sendDataToClient("Game is full");
        }
      } else {
        this.sendInfoToClient("You are already in the lobby");
      }
    } else {
      this.sendDataToClient("No game started");
    }
  }

  private void handleLeaveLobby() {
    if (this.server.isInLobby(this)) {
      this.server.getLobby().removePlayer(this.server.getPlayer(this));
      System.out.println("Player left the lobby");
      this.sendInfoToClient("You left the lobby");
      this.server.sendLobbyInfoToClients();
    } else {
      this.sendInfoToClient("You are not in the lobby");
    }
  }

  public void sendLobbyInfoToClient() {
    Gson gson = new Gson();
    if (this.server.getLobby() == null) {
      this.sendDataToClient("LBYUPDATE#NULL");
    } else {
      this.sendDataToClient("LBYUPDATE#" + gson.toJson(this.server.getLobby().toLobbyFeatures()));
    }
  }

  private void handleLevelChange(String inputLine) {
    inputLine = inputLine.substring(6);
    int difficulty;
    try {
      difficulty = Integer.parseInt(inputLine);
      switch (difficulty) {
        case 1:
          this.server.setLevelAI("Random");
          this.server.sendInfoToClients("Difficulty set to " + difficulty);
          break;
        case 2:
          this.server.setLevelAI("Advanced");
          this.server.sendInfoToClients("Difficulty set to " + difficulty);
          break;
        default:
          sendInfoToClient(difficulty + " is not a difficulty (1-2)");
      }
    } catch (NumberFormatException e) {
      this.sendInfoToClient(inputLine + "is not a difficulty (1-2)");
    }
  }

  public Socket getClient() {
    return client;
  }

  public int getClientId() {
    return id;
  }
}