package model;

import java.net.*;
import java.util.ArrayList;

import controller.ControllerSnakeGame;

import com.google.gson.*;

import java.io.*;

public class Server {
  private ServerSocket serverSocket;
  private ArrayList<Connection> connections;

  public void start(int port) {
    try {
      serverSocket = new ServerSocket(port);
      ControllerSnakeGame controller = new ControllerSnakeGame();
      System.out.println("Starting server");
      connections = new ArrayList<>();
      while (true) {
        Connection connection = new Connection(serverSocket.accept(), controller);
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

  public static class Connection extends Thread {
    private ControllerSnakeGame controller;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    public Connection(Socket socket, ControllerSnakeGame controller) {
      this.client = socket;
      this.controller = controller;
    }

    public void printClientMessage(String msg) {
      System.out.println("Server : " + msg + "\n");
    }

    public void sendJSON(String json) {
      out.println("#JSON#" + json);
    }

    public void run() {
      try {
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        System.out.println("new client connected");

        String inputLine;

        Gson gson = new Gson();
        String json = gson.toJson(this.controller.getGameFeatures());
        System.out.println("GF sent");
        sendJSON(json);

        while ((inputLine = in.readLine()) != null) {
          if (inputLine.equals("exit")) {
            out.println("good bye");
          } else if (inputLine.equals("hello")) {
            out.println("hello client");
          } else if (inputLine.equals("pause")) {
            out.println("game paused");
            this.controller.pause();
          } else if (inputLine.equals("run")) {
            out.println("game running");
            this.controller.play();
          } else if (inputLine.equals("step")) {
            out.println("step");
            this.controller.step();
          } else if (inputLine.startsWith("#VC#")) {
            handleViewCommandSignals(inputLine);
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
          this.controller.pause();
          break;
        case "RESUME":
          this.controller.play();
          break;
        case "STEP":
          this.controller.step();
          break;
        case "RESTART":
          this.controller.step();
          break;
        default:
          System.out.println("Unknown signal");
          break;
      }
    }
  }
}
