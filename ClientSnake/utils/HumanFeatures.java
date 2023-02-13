package utils;

import java.net.InetAddress;

public class HumanFeatures {
  private String username;
  private InetAddress clientAdress;

  public HumanFeatures(String username, InetAddress clientAdress) {
    this.username = username;
    this.clientAdress = clientAdress;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public InetAddress getClientAdress() {
    return clientAdress;
  }

  public void setClientAdress(InetAddress clientAdress) {
    this.clientAdress = clientAdress;
  }

}
