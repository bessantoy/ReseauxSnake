package network;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerListener extends Thread {

  private BufferedReader in;
  private Network network;

  public ServerListener(Network network, BufferedReader in) {
    this.in = in;
    this.network = network;
  }

  public void run() {
    String response;
    try {
      while ((response = this.in.readLine()) != null && !response.equals("EXIT")) {
        this.network.handleServerSignal(response);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}