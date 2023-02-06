import java.net.*;

import controller.ControllerSnakeGame;

import com.google.gson.*;

import java.io.*;

public class Main {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter sortie;
    private BufferedReader entree;
    private ControllerSnakeGame controller;

    public void printClientMessage(String msg) {
        System.out.println("Server : " + msg + "\n");
    }

    public void sendJSON(String json) {
        sortie.println("#JSON#" + json);
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Starting server");
            clientSocket = serverSocket.accept();
            System.out.println("new client connected");
            sortie = new PrintWriter(clientSocket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;

            this.controller = new ControllerSnakeGame();
            Gson gson = new Gson();
            String json = gson.toJson(this.controller.getGameFeatures());
            System.out.println("GF sent");
            sendJSON(json);

            while ((inputLine = entree.readLine()) != null) {
                if (inputLine.equals("exit")) {
                    sortie.println("good bye");
                } else if (inputLine.equals("hello")) {
                    sortie.println("hello client");
                } else if (inputLine.equals("pause")) {
                    sortie.println("game paused");
                    this.controller.pause();
                } else if (inputLine.equals("run")) {
                    sortie.println("game running");
                    this.controller.play();
                } else if (inputLine.equals("step")) {
                    sortie.println("step");
                    this.controller.step();
                } else if (inputLine.startsWith("#VC#")) {
                    handleViewCommandSignals(inputLine);
                }
            }
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

    public void stop() {
        try {
            entree.close();
            sortie.close();
            clientSocket.close();
            serverSocket.close();
            System.out.println("Server Stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Main server = new Main();
        server.start(5556);
    }
}
