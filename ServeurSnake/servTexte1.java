import java.net.*;

import controller.ControllerSnakeGame;

import java.io.*;
public class servTexte1 {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter sortie;
    private BufferedReader entree;

    public void start(int port) {
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Starting server");
            clientSocket = serverSocket.accept();
            sortie = new PrintWriter(clientSocket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;

            while ((inputLine = entree.readLine()) != null){
                if(inputLine.equals("exit")){
                    sortie.println("good bye");
                    break;
                }
                else if(inputLine.equals("play")){
                    ControllerSnakeGame controller = new ControllerSnakeGame(sortie);
                    break;
                }
                sortie.println(inputLine);
            }
            
        }catch(IOException e){e.printStackTrace();}
        
    }

    public void stop() {
        try{
            entree.close();
            sortie.close();
            clientSocket.close();
            serverSocket.close();
            System.out.println("Server Stopped");
        }catch(IOException e){e.printStackTrace();}
        
    }
    public static void main(String[] args) {
        servTexte1 server=new servTexte1();
        server.start(5556);
    }
}

