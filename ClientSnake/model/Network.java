package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

import utils.AgentAction;
import utils.GameFeatures;
import utils.GameState;
import view.PanelSnakeGame;
import view.ViewCommand;
import view.ViewSnakeGame;

public class Network extends Thread {

  private AgentAction inputMoveHuman;
  private GameFeatures gameFeatures;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
  private ViewCommand viewCommand;
  private ViewSnakeGame viewSnakeGame;
  private ServerListener serverListener;

  private void sendServerMessage(String msg) {
    System.out.println("Server : " + msg + "\n");
  }

  private void startConnection(String ip, int port) {
    try {
      clientSocket = new Socket(ip, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      System.out.println("Connected to server \n\n");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void stopConnection() {
    try {
      in.close();
      out.close();
      clientSocket.close();
      System.out.println("Connection to the server closed \n");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void play() {
    while (true) {
      this.updateView();
      if (this.getGameFeatures().getState() != GameState.PLAYING) {
        break;
      }
      try {
        Thread.sleep(this.gameFeatures.getSpeed());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void updateView() {
    this.out.println("#UPDATE#");
  }

  private void initView() {
    this.out.println("#INIT#");
  }

  public void handleServerSignal(String response) {
    if (response.equals("#RESTART#")) {
      this.updateView();
    } else if (response.equals("#RESUME#")) {
      this.play();
    } else if (response.equals("#STEP#")) {
      this.updateView();
    } else if (response.equals("#LAUNCH#")) {
      initView();
    } else if (response.startsWith("#UPDATE#")) {
      System.out.println("update");
      response = response.substring(8);
      if (!response.equals("-1")) {
        this.readGameFeatures(response);
        this.viewSnakeGame.update(this.gameFeatures);
        this.viewCommand.update(this.gameFeatures);
      }
    } else if (response.startsWith("#INIT#")) {
      response = response.substring(6);
      if (!response.equals("-1")) {
        this.readGameFeatures(response);
        this.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
            this.getGameFeatures().getSizeX(),
            this.getGameFeatures().getSizeY(), this.getGameFeatures().getWalls(),
            this.getGameFeatures().getFeaturesSnakes(),
            this.getGameFeatures().getFeaturesItems()), this);
        this.viewCommand = new ViewCommand(this);
        play();
      }
    }
  }

  public void run() {
    this.startConnection("localhost", 5556);
    this.serverListener = new ServerListener(this, in);
    this.serverListener.start();
    Scanner scan = new Scanner(System.in);
    String input;
    while (!(input = scan.nextLine()).equals("exit")) {
      this.out.println(input);
    }
    scan.close();
  }

  public Network() {
    this.inputMoveHuman = null;
    this.gameFeatures = null;
  }

  public AgentAction getInputMoveHuman() {
    return inputMoveHuman;
  }

  public GameFeatures getGameFeatures() {
    return gameFeatures;
  }

  public void setInputMoveHuman(AgentAction inputMoveHuman) {
    this.inputMoveHuman = inputMoveHuman;
  }

  public void setGameFeatures(GameFeatures gameFeatures) {
    this.gameFeatures = gameFeatures;
  }

  public void readGameFeatures(String json) {
    Gson gson = new Gson();
    this.gameFeatures = gson.fromJson(json, GameFeatures.class);
    if (this.gameFeatures == null) {
      System.out.println("Error: gameFeatures is null");
    }
  }

  public void sendCommandSignal(String signal) {
    this.out.println("#VC#" + signal);
  }

  public void sendMovementSignal(String signal) {
    this.out.println("#MV#" + signal);
  }
}
