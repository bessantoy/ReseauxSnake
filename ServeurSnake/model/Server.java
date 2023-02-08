package model;

import java.net.*;
import java.util.ArrayList;

import controller.ControllerSnakeGame;

import com.google.gson.*;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> connections;
  private ControllerSnakeGame controller;

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      controller = new ControllerSnakeGame();
      System.out.println("Starting server");
      connections = new ArrayList<>();
      while (true) {
        Connection connection = new Connection(serverSocket.accept(), this);
        connections.add(connection);
        connection.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendMessageToClients(String msg) {
    for (Connection connection : connections) {
      connection.printClientMessage(msg);
    }
  }

  public ControllerSnakeGame getController() {
    return this.controller;
  }

  public static class Connection extends Thread {
    private Server server;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    public Connection(Socket socket, Server server) {
      this.client = socket;
      this.server = server;
    }

    public void printClientMessage(String msg) {
      this.out.println(msg);
    }

    public void sendJSON(String json) {
      out.println("#JSON#" + json);
    }

    public void sendGameUpdate() {
      Gson gson = new Gson();

      String json = gson.toJson(this.server.getController().getGameFeatures());
      sendJSON(json);
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
            sendGameUpdate();
          } else {
            out.println("Unknown command : " + inputLine);
          }
        }
        in.close();
        out.close();
        client.close();
      } catch (IOException e) {
        e.printStackTrace();
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
          this.server.sendMessageToClients("#RESUME#");
          this.server.getController().play();
          break;
        case "STEP":
          this.server.getController().step();
          this.server.sendMessageToClients("#STEP#");
          break;
        case "RESTART":
          this.server.sendMessageToClients("#RESUME#");
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
    }
  }
}
