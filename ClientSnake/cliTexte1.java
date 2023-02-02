
import java.net.*;
import java.io.*;

public class cliTexte1 {
    private Socket clientSocket;
    private PrintWriter sortie;
    private BufferedReader entree;

    public void startConnection(String ip, int port) {
        try{
            clientSocket = new Socket(ip, port);
            sortie = new PrintWriter(clientSocket.getOutputStream(), true);
            entree = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connected to server \n\n");
        }catch(IOException e){e.printStackTrace();}
        
    }

    public String sendMessage(String msg) {
        try{
            sortie.println(msg);
            String resp = entree.readLine();
            System.out.print("Send '" + msg + "' to the Server \n");
            return "Server answered : '" + resp + "'\n\n";
        }catch(IOException e){e.printStackTrace();}

        return "sending message failed";
        
    }

    public void play() {
        try{
            String response;
            sortie.println("play");
            while ((response = entree.readLine()) != null){
                if(response.equals("Game Over")){
                    break;
                }
                System.out.println(response);
            }
        }catch(IOException e){e.printStackTrace();}
    }

    public void stopConnection() {
        try{
            entree.close();
            sortie.close();
            clientSocket.close();
            System.out.println("Connection to the server closed \n");
        }catch(IOException e){e.printStackTrace();}
        
    }
    public static void main(String[] args) {
        cliTexte1 client = new cliTexte1();
        client.startConnection("localhost",5556);
        client.play();
        
    }
}