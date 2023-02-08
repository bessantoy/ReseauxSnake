package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import controller.ControllerSnakeGame;

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

      while ((inputLine = in.readLine()) != null) {
        if (inputLine.startsWith("#VC#")) {
          handleViewCommandSignals(inputLine);
        } else if (inputLine.startsWith("#UPDATE#")) {
          if (this.server.getController() != null) {
            out.println("#UPDATE#" + getUpdate());
          } else {
            this.sendDataToClient("No game started");
          }
        } else if (inputLine.startsWith("#INIT#")) {
          out.println("#INIT#" + getUpdate());
        } else if (inputLine.startsWith("init")) {
          String layout = inputLine.substring(5);
          initGame(layout);
        } else if (inputLine.equals("join")) {
          joinLobby("Anonym");
        } else if (inputLine.startsWith("join ")) {
          String name = inputLine.substring(5);
          joinLobby(name);
        } else if (inputLine.equals("launch")) {
          if (this.server.getController() != null) {
            if (!this.server.getPlayers().isEmpty()) {
              this.server.sendMessageToPlayers("#LAUNCH#");
            } else {
              this.sendInfoToClient("Not enough players");
            }
          } else {
            this.sendInfoToClient("No game initialised");
          }
        } else if (inputLine.equals("leave lobby")) {
          this.server.getPlayers().remove(this.server.getPlayer(this));
        } else if (inputLine.equals("quit")) {
          this.server.getClients().remove(this);
          this.server.getPlayers().remove(this.server.getPlayer(this));
          this.server.sendInfoToClients("Client disconnected");
          break;
        } else {
          this.sendInfoToClient("Unknown command : " + inputLine);
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
      this.server.setController(new ControllerSnakeGame(layout));
      System.out.println("Game started with layout : " + layout);
      this.server.sendInfoToClients("Game initialised with layout : " + layout);
      if (this.server.getController().getGameFeatures().getFeaturesSnakes().size() < this.server.getPlayers()
          .size()) {
        this.server.getPlayers().clear();
        this.server.sendDataToClients("Lobby reseted, please join again");
      }
    } else {
      this.sendDataToClient("Unknown layout : " + layout);
    }
  }

  private void joinLobby(String name) {
    if (this.server.getController() != null) {
      if (!this.server.isInLobby(this)) {
        if (this.server.getController().getGameFeatures().getFeaturesSnakes().size() >= this.server.getPlayers()
            .size()) {
          this.server.getPlayers().add(new Player(this, name));
          this.sendInfoToClient("You joined the lobby");
          this.sendLobbyInfo();
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

  private void sendLobbyInfo() {
    this.sendInfoToClient("Lobby Infos : ");
    if (!this.server.getPlayers().isEmpty()) {
      for (int i = 0; i < this.server.getPlayers().size(); ++i) {
        this.sendDataToClient("    " + this.server.getPlayers().get(i).getUsername());
      }
    } else {
      this.sendDataToClient("You seem to be alone in this lobby");
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
      case "UP":
        break;
      case "DOWN":
        break;
      case "LEFT":
        break;
      case "RIGHT":
        break;
      default:
        System.out.println("Unknown signal");
        break;
    }
  }
}