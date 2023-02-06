package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
  private PrintWriter sortie;
  private BufferedReader entree;
  private ViewCommand viewCommand;
  private ViewSnakeGame viewSnakeGame;

  private void printServerMessage(String msg) {
    System.out.println("Server : " + msg + "\n");
  }

  private void startConnection(String ip, int port) {
    try {
      clientSocket = new Socket(ip, port);
      sortie = new PrintWriter(clientSocket.getOutputStream(), true);
      entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      System.out.println("Connected to server \n\n");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void stopConnection() {
    try {
      entree.close();
      sortie.close();
      clientSocket.close();
      System.out.println("Connection to the server closed \n");
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void initGame() {
    this.sortie.println("#UPDATE#");
    String response;
    try {
      response = this.entree.readLine();
      if (response.startsWith("#JSON#")) {
        String JSON = response.substring(6);
        this.readGameFeatures(JSON);
        this.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
            this.getGameFeatures().getSizeX(),
            this.getGameFeatures().getSizeY(), this.getGameFeatures().getWalls(),
            this.getGameFeatures().getFeaturesSnakes(),
            this.getGameFeatures().getFeaturesItems()), this);
        this.viewCommand = new ViewCommand(this);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    while (true) {
      try {
        this.sortie.println("#UPDATE#");
        response = this.entree.readLine();
        if (response.startsWith("#JSON#")) {
          String JSON = response.substring(6);
          this.readGameFeatures(JSON);
          if (this.getGameFeatures().getState() != GameState.PLAYING) {
            while (!(response = this.entree.readLine()).equals("#RESUME#")) {
              System.out.println(response);
            }
            System.out.println("Game resumed");
          }
          this.viewSnakeGame.update(this.gameFeatures);
        }
        Thread.sleep(this.gameFeatures.getSpeed());

      } catch (IOException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void run() {
    this.startConnection("localhost", 5556);
    this.initGame();
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
    this.sortie.println("#VC#" + signal);
  }

  public void sendMovementSignal(String signal) {
    this.sortie.println("#MV#" + signal);
  }
}
