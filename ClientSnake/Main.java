
import java.net.*;
import java.io.*;
import java.util.Scanner;

import model.Network;
import view.PanelSnakeGame;
import view.ViewCommand;
import view.ViewSnakeGame;

public class Main {
    private Socket clientSocket;
    private PrintWriter sortie;
    private BufferedReader entree;
    private Network network;
    private ViewCommand viewCommand;
    private ViewSnakeGame viewSnakeGame;

    public void printServerMessage(String msg) {
        System.out.println("Server : " + msg + "\n");
    }

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            sortie = new PrintWriter(clientSocket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connected to server \n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void newGame() {
        this.network = new Network();
    }

    public void stopConnection() {
        try {
            entree.close();
            sortie.close();
            clientSocket.close();
            System.out.println("Connection to the server closed \n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Main client = new Main();
        client.startConnection("localhost", 5556);
        // client.newGame();
        try {

            client.sortie.println("hello");
            String response;
            while ((response = client.entree.readLine()) != "good bye") {
                if (response.equals("new game initialized")) {
                    client.newGame();
                }
                if (response.startsWith("#JSON#")) {
                    client.newGame();
                    String JSON = response.substring(6);
                    System.out.print(JSON);
                    client.network.readGameFeatures(JSON);
                    client.viewSnakeGame = new ViewSnakeGame(new PanelSnakeGame(
                            client.network.getGameFeatures().getSizeX(),
                            client.network.getGameFeatures().getSizeY(), client.network.getGameFeatures().getWalls(),
                            client.network.getGameFeatures().getFeaturesSnakes(),
                            client.network.getGameFeatures().getFeaturesItems()));
                    client.viewCommand = new ViewCommand(client.network);

                }
                client.printServerMessage(response);
                Scanner sc = new Scanner(System.in);
                System.out.println("commande :");
                String str = sc.nextLine();
                client.sortie.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}