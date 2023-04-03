package instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import api.API_Handler;
import client.Human;
import server.Server;
import utils.AgentAction;
import utils.GameFeatures;
import utils.GameState;

public class Connection extends Thread {
  private Server server;
  private Socket client;
  private Human player;
  private API_Handler api;
  private Lobby lobby;
  private int id;

  private PrintWriter out;
  private BufferedReader in;

  public Connection(Socket socket, Server server, int id) {
    this.client = socket;
    this.server = server;
    this.id = id;
    this.player = null;
    this.lobby = null;
  }

  public void run() {
    try {
      out = new PrintWriter(client.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      System.out.println("new client connection request");
      boolean connected = true;
      String inputLine;
      while (connected) {
        inputLine = in.readLine();
        if (inputLine == null) {
          connected = false;
        } else {
          connected = this.handleCommand(inputLine);
        }
      }
      in.close();
      out.close();
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean handleCommand(String inputLine) {
    if (inputLine.startsWith("CLI#")) {
      handleCliSignal(inputLine);
    } else if (inputLine.startsWith("LBY#")) {
      handleLobbySignal(inputLine);
    } else if (inputLine.startsWith("VC#")) {
      handleViewCommandSignals(inputLine);
    } else if (inputLine.startsWith("MV#")) {
      handleMovementSignal(inputLine);
    }
    return true;
  }

  private void handleViewCommandSignals(String signal) {
    signal = signal.substring(3);
    System.out.println("Signal received from " + this.player.getUsername() + " : " + signal);
    if (this.lobby.isGameInitialised()) {
      switch (signal) {
        case "PAUSE":
          this.lobby.getGameInstance().getController().pause();
          break;
        case "RESUME":
          this.lobby.getGameInstance().getController().play();
          this.lobby.sendGameDataToPlayers("RESUME");
          break;
        case "STEP":
          this.lobby.getGameInstance().getController().step();
          this.lobby.sendGameDataToPlayers("STEP");
          break;
        case "RESTART":
          this.lobby.getGameInstance().sendDataToPlayers("RESTART");
          this.lobby.getGameInstance().getController().restart();
          break;
        case "SPEED":
          handleSpeedChange();
          break;
        case "UPDATE":
          this.sendGameUpdate();
          break;
        case "JOINED":
          this.lobby.getGameInstance().addPlayerInGame(this.player);
          break;
        case "EXIT":
          this.lobby.getGameInstance().removePlayerInGame(this.player);
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
        this.lobby.getPlayer(this).setLastInput(AgentAction.MOVE_UP);
        break;
      case "DOWN":
        this.lobby.getPlayer(this).setLastInput(AgentAction.MOVE_DOWN);
        break;
      case "LEFT":
        this.lobby.getPlayer(this).setLastInput(AgentAction.MOVE_LEFT);
        break;
      case "RIGHT":
        this.lobby.getPlayer(this).setLastInput(AgentAction.MOVE_RIGHT);
        break;
      default:
        System.out.println("Wrong movement");
    }
  }

  private void handleLobbySignal(String signal) {
    signal = signal.substring(4);
    if (signal.equals("INIT")) {
      handleInitGame("arena#advanced");
    } else if (signal.startsWith("INIT#")) {
      handleInitGame(signal.substring(5));
    } else if (signal.startsWith("UPDATE")) {
      handleLobbyStatusRequest();
    } else if (signal.startsWith("JOIN#")) {
      handleJoinLobby(signal.substring(5));
    } else if (signal.equals("CREATE")) {
      handleCreateLobby();
    } else if (signal.equals("LAUNCH")) {
      handleLaunch();
    } else if (signal.equals("LEAVE")) {
      handleLeaveLobby();
    } else if (signal.startsWith("LEVEL#")) {
      handleLevelChange(signal);
    } else {
      sendInfoToClient("Unknown lobby command : " + signal);
    }
  }

  private boolean handleCliSignal(String signal) {
    signal = signal.substring(4);
    if (signal.startsWith("CONNECTION#")) {
      handleClientConnection(signal);
    } else if (signal.startsWith("UPDATE")) {
      this.server.sendCliStatusToClient(this);
    } else if (signal.equals("EXIT")) {
      handleClientExit();
      return false;
    } else {
      sendInfoToClient("Unknown cli command : " + signal);
    }
    return true;
  }

  private void handleLaunch() {
    if (this.lobby.isGameInitialised()) {
      if (!this.lobby.isEmpty()) {
        this.lobby.getGameInstance().getController().initGame();
        this.lobby.getGameInstance().launchGame();
        this.lobby.sendGameDataToPlayers("LAUNCH");
      } else {
        this.sendInfoToClient("No players in lobby");
      }
    } else {
      this.sendInfoToClient("No game initialised");
    }
  }

  private void handleClientConnection(String signal) {
    String[] signalArray = signal.split("#");
    if (signalArray.length == 3) {
      String email = signalArray[1];
      String password = signalArray[2];
      System.out.println("Connection request : " + email);
      this.api = new API_Handler(email, password);
      if (api.getUsername() != null) {
        this.player = new Human(this, this.id, api.getUsername());
        this.sendCliDataToClient("CONNECTION#OK#" + this.id);
        this.server.sendCliStatusToClient(this);
      } else {
        this.sendCliDataToClient("CONNECTION#F");
      }
    } else {
      this.sendInfoToClient("Wrong signal");
    }
  }

  private void handleClientExit() {
    this.server.removeClient(this);
    this.sendCliDataToClient("EXIT");
    System.out.println("Client disconnected");
    this.server.sendInfoToClients("Client disconnected");
  }

  private void handleSpeedChange() {
    try {
      String response = in.readLine();
      response = response.substring(3);
      Double speed = Double.parseDouble(response);
      if (this.lobby.getGameInstance().getController().getSpeed() != speed) {
        this.lobby.getGameInstance().getController().setSpeed(speed);
        this.lobby.sendGameDataToPlayers("SPEED#" + speed.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleLobbyStatusRequest() {
    if (lobby != null) {
      this.lobby.sendLobbyStatusToClient(this);
    } else {
      this.sendInfoToClient("Your requested update on lobby, but you aren't in one");
    }
  }

  private void handleInitGame(String signal) {
    String layout = signal.split("#")[0];
    String levelAI = signal.split("#")[1];
    if (this.lobby != null) {
      if (new File("./layouts/" + layout + ".lay").exists()) {
        this.lobby.getGameInstance().init(layout, levelAI);
        System.out.println("Game initialised (" + layout + " | " + this.lobby.getGameInstance()
            .getLevelAI() + ")");
        this.lobby.sendInfoToPlayers("Game initialised (" + layout + " | " + this.lobby.getGameInstance()
            .getLevelAI() + ")");
        this.lobby.sendGameDataToPlayers("INITIALISED#" + layout);
        if (this.lobby.getGameInstance().getController().getNumberOfPlayers() < this.lobby.getPlayers()
            .size()) {
          this.lobby.getPlayers().clear();
          this.lobby.sendInfoToPlayers("Lobby reseted, please join again");
        } else {
          if (this.lobby.getGameInstance().isGameLaunched()) {
            this.lobby.getGameInstance().getController().initGame();
            this.lobby.sendGameDataToPlayers("INITIALISED#" + layout);
            this.lobby.sendGameDataToPlayers("LAUNCH");
          }
        }
      } else {
        this.sendInfoToClient("Unknown layout : " + layout);
      }
    } else {
      this.sendInfoToClient("You are not in a lobby");
    }
  }

  private void handleJoinLobby(String signal) {
    int lobbyId = Integer.parseInt(signal);
    if (this.lobby == null) {
      this.lobby = this.server.getLobby(lobbyId);
      this.lobby.addPlayer(this.player);
      System.out.println("Player " + this.player.getUsername() + " joined the lobby");
      this.sendInfoToClient("You joined the lobby");
      this.lobby.sendLobbyStatusToPlayers();
    } else {
      this.sendInfoToClient("You are already in the lobby");
    }
  }

  private void handleLeaveLobby() {
    if (this.lobby != null) {
      if (this.lobby.isInLobby(this)) {
        this.lobby.removePlayer(this.lobby.getPlayer(this));
        System.out.println("Player left the lobby");
        if (this.lobby.isEmpty()) {
          this.server.removeLobby(this.lobby);
          System.out.println("Lobby removed cause empty");
        }
        this.lobby = null;
        this.server.sendCliStatusToClients();
        this.sendInfoToClient("You left the lobby");
        this.sendLobbyDataToClient("LEAVE");
      } else {
        this.sendInfoToClient("You are not in the lobby");
      }
    } else {
      this.sendInfoToClient("You are not in a lobby");
    }
  }

  private void handleCreateLobby() {
    System.out.println("Lobby created");
    int lobbyId = this.server.addLobby();
    handleJoinLobby(Integer.toString(lobbyId));
    Lobby l = this.server.getLobby(lobbyId);
    l.getGameInstance().init("alone", "Advanced");
    this.server.sendCliStatusToClients();
  }

  private void handleLevelChange(String inputLine) {
    String level = inputLine.substring(6);
    this.lobby.getGameInstance().setLevelAI(level);
    this.lobby.sendLobbyStatusToPlayers();
  }

  public void sendGameUpdate() {
    if (this.lobby != null && this.lobby.isGameInitialised()) {
      Gson gson = new Gson();
      GameFeatures gf = this.lobby.getGameInstance().getController().getGameFeatures();
      if (gf.getState() == GameState.OVER) {
        api.updateScore(gf.getPlayerScore(id));
        System.out.println("Score updated");
      }
      String update = gson.toJson(gf, GameFeatures.class);
      sendGameDataToClient("UPDATE#" + update);
    } else {
      this.sendInfoToClient("No game started");
    }
  }

  public void sendLobbyDataToClient(String data) {
    this.out.println("LBY#" + data);
  }

  public void sendGameDataToClient(String data) {
    this.out.println("GME#" + data);
  }

  public void sendCliDataToClient(String data) {
    this.out.println("CLI#" + data);
  }

  public void sendInfoToClient(String msg) {
    this.out.println("?Server : " + msg);
  }

  public Socket getClient() {
    return client;
  }

  public int getClientId() {
    return id;
  }

  public boolean isInLobby() {
    return this.lobby != null;
  }
}