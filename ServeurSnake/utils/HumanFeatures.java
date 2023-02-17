package utils;

import java.net.InetAddress;

public class HumanFeatures {
  private String username;
  private int id;

  public HumanFeatures(String username, int id) {
    this.username = username;
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getId() {
    return id;
  }

}
