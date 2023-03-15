package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import com.google.gson.Gson;

import utils.AgentAction;
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
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
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
      clientSocket = new Socket("localhost", 5556);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      System.out.println("Connected to server");
      new ServerListener(this, in).start();
      this.updateClientView();
    } catch (ConnectException e) {
      System.out.println("Server is not running");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateClientView() {
    sendLobbySignal("UPDATE");
  }

  public void stopConnection() {
    try {
      sendLobbySignal("EXIT");
      in.close();
      out.close();
      clientSocket.close();
      System.out.println("Connection to the server closed \n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void play() {
    try {
      Thread.sleep(this.gameFeatures.getSpeed());
    } catch (InterruptedException e) {
      e.printStackTrace();
      this.interrupt();
    }
    this.askForViewUpdate();
  }

  private void askForViewUpdate() {
    System.out.println("Update view");
    sendViewCommandSignal("UPDATE");
  }

  private void askForLobbyUpdate() {
    sendLobbySignal("UPDATE");
  }

  public void handleServerSignal(String signal) {
    if (signal.startsWith("CONNECTION#")) {
      this.handleInitConnection(signal);
    } else if (signal.equals("RESTART")) {
      this.askForViewUpdate();
    } else if (signal.equals("RESUME")) {
      this.askForViewUpdate();
    } else if (signal.equals("STEP")) {
      this.askForViewUpdate();
    } else if (signal.equals("LAUNCH")) {
      askForViewUpdate();
    } else if (signal.startsWith("GAMEUPDATE#")) {
      if (this.gameOpen)
        this.handleGameUpdate(signal);
      else
        this.handleLaunchGame(signal);
    } else if (signal.startsWith("LBYUPDATE#")) {
      this.handleLobbyUpdate(signal);
    } else if (signal.startsWith("SPEED#")) {
      this.handleSpeedChange(signal);
    } else if (signal.startsWith("INITIALISED#")) {
      this.handleGameInitalised(signal);
    } else if (signal.startsWith("FINISHED")) {
      this.handleGameFinished();
    }
  }

  public AgentAction getInputMoveHuman() {
    return inputMoveHuman;
  }

  public GameFeatures getGameFeatures() {
    return gameFeatures;
  }

  public LobbyFeatures getLobbyFeatures() {
    return lobbyFeatures;
  }

  public void setInputMoveHuman(AgentAction inputMoveHuman) {
    this.inputMoveHuman = inputMoveHuman;
  }

  public void setGameFeatures(GameFeatures gameFeatures) {
    this.gameFeatures = gameFeatures;
  }

  public void handleInitConnection(String signal) {
    signal = signal.substring(11);
    this.id = Integer.parseInt(signal);
  }

  public void handleLaunchGame(String signal) {
    signal = signal.substring(11);
    if (!signal.equals("-1")) {
      this.readGameFeatures(signal);
      this.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
          this.getGameFeatures().getSizeX(),
          this.getGameFeatures().getSizeY(), this.getGameFeatures().getWalls(),
          this.getGameFeatures().getFeaturesSnakes(),
          this.getGameFeatures().getFeaturesItems()), this);
      this.viewCommand = new ViewCommand(this, this.viewSnakeGame.getjFrame());
      play();
      this.sendViewCommandSignal("JOINED");
      this.gameOpen = true;
    }
  }

  public void handleGameUpdate(String signal) {
    signal = signal.substring(11);
    if (!signal.equals("-1")) {
      this.readGameFeatures(signal);
      this.viewSnakeGame.update(this.gameFeatures);
      this.viewCommand.update(this.gameFeatures);
      if (this.gameFeatures.getState() == GameState.PLAYING)
        this.play();
    }
  }

  public void handleLeaveGame() {
    this.sendViewCommandSignal("EXIT");
    this.gameOpen = false;
  }

  public void handleSpeedChange(String signal) {
    signal = signal.substring(6);
    System.out.println(signal);
    viewCommand.updateSlider(Double.parseDouble(signal));
  }

  public void handleLobbyUpdate(String signal) {
    signal = signal.substring(10);
    if (!signal.equals("NULL")) {
      Gson gson = new Gson();
      this.lobbyFeatures = gson.fromJson(signal, LobbyFeatures.class);
      if (this.viewClient == null) {
        this.viewClient = new ViewClient(this, id);
      } else {
        this.viewClient.update(lobbyFeatures, id);
      }
    } else {
      System.out.println("Server couldn't send lobby infos");
    }
  }

  public void handleGameInitalised(String signal) {
    signal = signal.substring(12);
    this.lobbyFeatures.setMap(signal);
    this.viewClient.update(lobbyFeatures, id);
  }

  public void handleGameFinished() {
    askForLobbyUpdate();
  }

  public void readGameFeatures(String json) {
    Gson gson = new Gson();
    this.gameFeatures = gson.fromJson(json, GameFeatures.class);
  }

  public void sendViewCommandSignal(String signal) {
    this.out.println("VC#" + signal);
  }

  public void sendLobbySignal(String signal) {
    this.out.println("LBY#" + signal);
  }

  public void sendMovementSignal(String signal) {
    this.out.println("MV#" + signal);
  }
}
