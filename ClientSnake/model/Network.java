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
    String response;
    while (true) {
      this.updateView();
      try {
        if (this.getGameFeatures().getState() != GameState.PLAYING) {
          while (!(response = this.in.readLine()).equals("#RESUME#")) {
            handleServerSignal(response);
          }
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        Thread.sleep(this.gameFeatures.getSpeed());
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private String getUpdate() {
    String response = "";
    try {
      this.out.println("#UPDATE#");
      response = this.in.readLine();
      if (response.startsWith("#JSON#")) {
        response = response.substring(6);
      } else {
        System.out.println(response);
        return "-1";
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }

  private void updateView() {
    String JSON = this.getUpdate();
    if (!JSON.equals("-1")) {
      this.readGameFeatures(JSON);
      this.viewSnakeGame.update(this.gameFeatures);
      this.viewCommand.update(this.gameFeatures);
    }

  }

  private void initView() {
    String JSON = this.getUpdate();
    if (!JSON.equals("-1")) {
      this.readGameFeatures(JSON);
      this.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
          this.getGameFeatures().getSizeX(),
          this.getGameFeatures().getSizeY(), this.getGameFeatures().getWalls(),
          this.getGameFeatures().getFeaturesSnakes(),
          this.getGameFeatures().getFeaturesItems()), this);
      this.viewCommand = new ViewCommand(this);
      play();
    }
  }

  private void handleServerSignal(String response) {
    if (response.equals("#RESTART#")) {
      this.updateView();
    }
    if (response.equals("#STEP#")) {
      this.updateView();
    }
    if (response.equals("#LAUNCH#")) {
      initView();
    }
  }

  public void run() {
    this.startConnection("localhost", 5556);
    new ListenServer(in).start();
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

  public static class ListenServer extends Thread {

    private BufferedReader in;

    public ListenServer(BufferedReader in) {
      this.in = in;
    }

    public void run() {
      String response;
      try {
        while ((response = this.in.readLine()) != null) {
          if (response.startsWith("Server :"))
            System.out.println(response);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
