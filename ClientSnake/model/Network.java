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

  public void run() {
    try {
      clientSocket = new Socket("localhost", 5556);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      System.out.println("Connected to server");
      new ServerListener(this, in).start();
      Scanner scan = new Scanner(System.in);
      String input;
      while (!(input = scan.nextLine()).equals("exit")) {
        this.out.println(input);
      }
      this.out.println(input);
      scan.close();
      this.stopConnection();
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
    this.updateView();
    try {
      Thread.sleep(this.gameFeatures.getSpeed());
    } catch (InterruptedException e) {
      e.printStackTrace();
      this.interrupt();
    }
  }

  private void updateView() {
    System.out.println("Update view");
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
      response = response.substring(8);
      this.handleUpdate(response);
    } else if (response.startsWith("#INIT#")) {
      response = response.substring(6);
      this.handleInit(response);
    }
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

  public void handleUpdate(String response) {
    if (!response.equals("-1")) {
      this.readGameFeatures(response);
      this.viewSnakeGame.update(this.gameFeatures);
      this.viewCommand.update(this.gameFeatures);
      if (this.gameFeatures.getState() == GameState.PLAYING)
        this.play();
    }
  }

  public void handleInit(String response) {
    if (!response.equals("-1")) {
      this.readGameFeatures(response);
      this.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
          this.getGameFeatures().getSizeX(),
          this.getGameFeatures().getSizeY(), this.getGameFeatures().getWalls(),
          this.getGameFeatures().getFeaturesSnakes(),
          this.getGameFeatures().getFeaturesItems()), this);
      this.viewCommand = new ViewCommand(this, this.viewSnakeGame.getjFrame());
      play();
    }
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
