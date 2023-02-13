package network;

public abstract class Player {
  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  protected Player(String username) {
    this.username = username;
  }

  abstract public boolean isHuman();
}
