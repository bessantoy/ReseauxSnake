package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import utils.AgentAction;
import utils.FeaturesItem;
import utils.FeaturesSnake;
import utils.GameFeatures;
import utils.GameState;
import utils.LobbyFeatures;
import view.PanelSnakeGame;
import view.ViewClient;
import view.ViewCommand;
import view.ViewSnakeGame;

public class Network extends Thread {

  private AgentAction inputMoveHuman;
  private GameFeatures gameFeatures;
  private LobbyFeatures lobbyFeatures;

  private PrintWriter out;
  private int id;
  private ViewClient viewClient;
  private ViewCommand viewCommand;
  private ViewSnakeGame viewSnakeGame;
  private boolean gameOpen;

  public Network() {
    this.inputMoveHuman = null;
    this.gameFeatures = null;
    this.gameOpen = false;
  }

  public void run() {
    try {
      Socket clientSocket = new Socket("localhost", 5556);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      System.out.println("Server reached successfully");
      new ServerListener(this, in).start();
      requestConnection();
    } catch (ConnectException e) {
      System.out.println("Server is not running");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void requestConnection() {
    String email;
    String password;
    Scanner sc = new Scanner(System.in);

    System.out.println("\nadresse email :");
    try {
      email = sc.nextLine();
      System.out.println("mot de passe :");
      password = sc.nextLine();
      sendCliSignal("CONNECTION#" + email + "#" + password);
    } catch (Exception e) {
      System.out.println("An error occured");
    } finally {
      sc.close();
    }

  }

  public void stopConnection() {
    sendCliSignal("EXIT");
  }

  public void handleServerSignal(String signal) {
    if (signal.startsWith("?")) {
      System.out.println(signal.substring(1));
    } else if (signal.startsWith("CLI#")) {
      this.handleCliServerSignal(signal.substring(4));
    } else if (signal.startsWith("LBY#")) {
      this.handleLobbyServerSignal(signal.substring(4));
    } else if (signal.startsWith("GME#")) {
      this.handleGameServerSignal(signal.substring(4));
    }
  }

  public void handleCliServerSignal(String signal) {
    if (signal.startsWith("CONNECTION#")) {
      this.handleConnectionResponse(signal.substring(11));
    } else if (signal.startsWith("UPDATE#")) {
      this.handleCliUpdate(signal.substring(7));
    }
  }

  public void handleLobbyServerSignal(String signal) {
    if (signal.startsWith("UPDATE#")) {
      this.handleLobbyUpdate(signal.substring(7));
    } else if (signal.startsWith("LEAVE")) {
      this.handleLeaveLobby();
    }
  }

  public void handleGameServerSignal(String signal) {
    if (signal.startsWith("INITIALISED#")) {
      this.handleGameInitalised(signal.substring(12));
    } else if (signal.startsWith("UPDATE#")) {
      if (this.gameOpen)
        this.handleGameUpdate(signal.substring(7));
      else
        this.handleLaunchGame(signal.substring(7));
    } else if (signal.equals("RESTART")) {
      this.askForGameViewUpdate();
    } else if (signal.equals("RESUME")) {
      this.askForGameViewUpdate();
    } else if (signal.equals("STEP")) {
      this.askForGameViewUpdate();
    } else if (signal.equals("LAUNCH")) {
      askForGameViewUpdate();
    } else if (signal.startsWith("SPEED#")) {
      this.handleSpeedChange(signal.substring(6));
    } else if (signal.startsWith("FINISHED")) {
      this.handleGameFinished();
    }
  }

  public void handleConnectionResponse(String signal) {
    if (signal.startsWith("OK")) {
      this.id = Integer.parseInt(signal.substring(3));
      System.out.println("Connection to the server successful");
    } else {
      System.out.println("Connection to the server failed");
      this.stopConnection();
    }
  }

  public void handleCliUpdate(String signal) {
    System.out.println("Client update received");
    ArrayList<Integer> lobbies;
    if (!signal.equals("EMPTY")) {
      Gson gson = new Gson();
      lobbies = gson.fromJson(signal, new TypeToken<ArrayList<Integer>>() {
      }.getType());
    } else {
      lobbies = new ArrayList<>();
    }
    if (this.viewClient == null) {
      this.viewClient = new ViewClient(this, lobbies);
    } else {
      this.viewClient.update(lobbies);
    }
  }

  public void handleLobbyUpdate(String signal) {
    System.out.println("Lobby update received");
    if (!signal.equals("NULL")) {
      Gson gson = new Gson();
      this.lobbyFeatures = gson.fromJson(signal, LobbyFeatures.class);
      if (this.viewClient == null) {
        this.viewClient = new ViewClient(this, lobbyFeatures.getLobbies());
      } else {
        this.viewClient.update(lobbyFeatures, id);
      }
    } else {
      System.out.println("Server couldn't send lobby infos");
    }
  }

  public void handleLeaveLobby() {
    this.lobbyFeatures = null;
    this.askForCliUpdate();
  }

  public void handleGameInitalised(String signal) {
    this.lobbyFeatures.getGameInstanceFeatures().setMap(signal);
    this.viewClient.update(lobbyFeatures, id);
  }

  public void handleGameUpdate(String signal) {
    if (!signal.equals("-1")) {
      this.readGameFeatures(signal);
      this.viewSnakeGame.update(this.gameFeatures);
      this.viewCommand.update(this.gameFeatures);
      if (this.gameFeatures.getState() == GameState.PLAYING)
        this.play();
    }
  }

  public void handleLaunchGame(String signal) {
    if (!signal.equals("-1")) {
      this.readGameFeatures(signal);
      this.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
          this.getGameFeatures().getSizeX(),
          this.getGameFeatures().getSizeY(), this.getGameFeatures().getWalls(),
          (ArrayList<FeaturesSnake>) this.getGameFeatures().getFeaturesSnakes(),
          (ArrayList<FeaturesItem>) this.getGameFeatures().getFeaturesItems()), this);
      this.viewCommand = new ViewCommand(this, this.viewSnakeGame.getjFrame());
      play();
      this.sendViewCommandSignal("JOINED");
      this.gameOpen = true;
    }
  }

  public void handleSpeedChange(String signal) {
    System.out.println(signal);
    viewCommand.updateSlider(Double.parseDouble(signal));
  }

  public void handleGameFinished() {
    askForLobbyUpdate();
  }

  public void handleLeaveGame() {
    this.sendViewCommandSignal("EXIT");
    this.gameOpen = false;
  }

  public void play() {
    try {
      Thread.sleep(this.gameFeatures.getSpeed());
    } catch (InterruptedException e) {
      e.printStackTrace();
      this.interrupt();
    }
    this.askForGameViewUpdate();
  }

  private void askForGameViewUpdate() {
    sendViewCommandSignal("UPDATE");
  }

  private void askForCliUpdate() {
    sendCliSignal("UPDATE");
  }

  private void askForLobbyUpdate() {
    sendLobbySignal("UPDATE");
  }

  public void sendViewCommandSignal(String signal) {
    this.out.println("VC#" + signal);
  }

  public void sendLobbySignal(String signal) {
    this.out.println("LBY#" + signal);
  }

  public void sendCliSignal(String signal) {
    this.out.println("CLI#" + signal);
  }

  public void sendMovementSignal(String signal) {
    this.out.println("MV#" + signal);
  }

  public AgentAction getInputMoveHuman() {
    return inputMoveHuman;
  }

  public void setInputMoveHuman(AgentAction inputMoveHuman) {
    this.inputMoveHuman = inputMoveHuman;
  }

  public GameFeatures getGameFeatures() {
    return gameFeatures;
  }

  public void setGameFeatures(GameFeatures gameFeatures) {
    this.gameFeatures = gameFeatures;
  }

  public LobbyFeatures getLobbyFeatures() {
    return lobbyFeatures;
  }

  public void readGameFeatures(String json) {
    Gson gson = new Gson();
    this.gameFeatures = gson.fromJson(json, GameFeatures.class);
  }
}