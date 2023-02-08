package network;

public class Player {

  private Connection client;
  private String username;

  public Player(Connection client, String username) {
    this.client = client;
    this.username = username;
  }

  public Connection getClient() {
    return client;
  }

  public void setClient(Connection client) {
    this.client = client;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean isClient(Connection client) {
    return this.client.equals(client);
  }

}
