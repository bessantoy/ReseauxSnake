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

public class Connection extends Thread {
  private Server server;
  private Socket client;
  private PrintWriter out;
  private BufferedReader in;

  public Connection(Socket socket, Server server) {
    this.client = socket;
    this.server = server;
  }

  public void sendDataToClient(String msg) {
    this.out.println(msg);
  }

  public void sendInfoToClient(String msg) {
    this.out.println("Server : " + msg);
  }

  public String getUpdate() {
    Gson gson = new Gson();
    return gson.toJson(this.server.getController().getGameFeatures());
  }

  public void run() {
    try {
      out = new PrintWriter(client.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      System.out.println("new client connected");

      String inputLine;
      boolean connected = true;
      while (connected) {
        inputLine = in.readLine();
        if (inputLine.startsWith("#")) {
          this.handleCommand(inputLine);
        } else {
          connected = this.handleMessage(inputLine);
        }
      }
      in.close();
      out.close();
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleCommand(String inputLine) {
    if (inputLine.startsWith("#VC#")) {
      handleViewCommandSignals(inputLine);
    } else if (inputLine.startsWith("#MV#")) {
      handleMovementSignal(inputLine);
    } else if (inputLine.startsWith("#UPDATE#")) {
      System.out.println("Update view");
      if (this.server.isGameInitialised()) {
        sendDataToClient("#UPDATE#" + getUpdate());
      } else {
        this.sendInfoToClient("No game started");
      }
    } else if (inputLine.startsWith("#INIT#")) {
      this.server.loadGame();
      this.server.getController().initGame();
      sendDataToClient("#INIT#" + getUpdate());
    }
  }

  public void handleViewCommandSignals(String signal) {
    signal = signal.substring(4);
    System.out.println("Signal received : " + signal);
    if (this.server.isGameInitialised()) {
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
          this.server.sendMessageToPlayers("#RESTART#");
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
        default:
          System.out.println("Unknown signal");
          break;
      }
    } else {
      System.out.println("Game not initialised");
    }
  }

  private void handleMovementSignal(String signal) {
    signal = signal.substring(4);
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

  private boolean handleMessage(String inputLine) {
    if (inputLine.startsWith("init ")) {
      String layout = inputLine.substring(5);
      initGame(layout);
    } else if (inputLine.equals("join")) {
      joinLobby("Anonym");
    } else if (inputLine.startsWith("join ")) {
      String name = inputLine.substring(5);
      joinLobby(name);
    } else if (inputLine.equals("launch")) {
      this.handleLaunch();
    } else if (inputLine.equals("leave lobby")) {
      this.server.getPlayers().remove(this.server.getPlayer(this));
    } else if (inputLine.equals("reset lobby")) {
      this.server.getPlayers().clear();
    } else if (inputLine.startsWith("level ")) {
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
    } else if (inputLine.equals("lobby")) {
      this.sendLobbyInfoToClient();
    } else if (inputLine.equals("exit")) {
      this.handleClientExit();
      return false;
    } else {
      this.sendInfoToClient("Unknown command : " + inputLine);
    }
    return true;
  }

  private void handleLaunch() {
    if (this.server.isGameInitialised()) {
      if (!this.server.getPlayers().isEmpty()) {
        this.server.sendMessageToPlayers("#LAUNCH#");
      } else {
        this.sendInfoToClient("Not enough players");
      }
    } else {
      this.sendInfoToClient("No game initialised");
    }
  }

  private void handleClientExit() {
    this.server.getClients().remove(this);
    this.server.getPlayers().remove(this.server.getPlayer(this));
    System.out.println("Client disconnected");
    this.server.sendInfoToClients("Client disconnected");
  }

  private void initGame(String layout) {
    if (new File("./layouts/" + layout + ".lay").exists()) {
      this.server.setController(new ControllerSnakeGame(layout));
      System.out.println("Game started with layout : " + layout);
      this.server.sendInfoToClients("Game initialised with layout : " + layout);
      if (this.server.getController().getNumberOfPlayers() < this.server.getPlayers()
          .size()) {
        this.server.getPlayers().clear();
        this.server.sendDataToClients("Lobby reseted, please join again");
      }
    } else {
      this.sendDataToClient("Unknown layout : " + layout);
    }
  }

  private void joinLobby(String name) {
    if (this.server.isGameInitialised()) {
      if (!this.server.isInLobby(this)) {
        if (this.server.getController().getNumberOfPlayers() >= this.server.getPlayers()
            .size()) {
          this.server.getPlayers().add(new Human(this, name));
          this.sendInfoToClient("You joined the lobby");
          this.sendLobbyInfoToClient();
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

  private void sendLobbyInfoToClient() {
    this.sendInfoToClient("Lobby Infos : ");
    if (this.server.isGameInitialised()) {
      if (!this.server.getPlayers().isEmpty()) {
        for (int i = 0; i < this.server.getPlayers().size(); ++i) {
          if (this.server.getPlayers().get(i).getClient() == this)
            this.sendDataToClient("    " + this.server.getPlayers().get(i).getUsername() + " (you)");
          else
            this.sendDataToClient("    " + this.server.getPlayers().get(i).getUsername());
        }
      } else {
        this.sendDataToClient("Lobby is empty");
      }
    } else {
      this.sendDataToClient("No game started");
    }

  }

  private void sendLobbyInfoToClients() {
    this.server.sendInfoToClients("Lobby Infos : ");
    if (!this.server.getPlayers().isEmpty()) {
      for (int i = 0; i < this.server.getPlayers().size(); ++i) {
        this.server.sendDataToClients("    " + this.server.getPlayers().get(i).getUsername());
      }
    } else {
      this.server.sendDataToClients("You seem to be alone in this lobby");
    }
  }
}