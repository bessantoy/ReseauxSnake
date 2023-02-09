package network;

public class AI implements Player {
  private String username;
  private String level;

  public AI(String username, String level) {
    this.username = username;
    this.level = level;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  @Override
  public boolean isHuman() {
    return false;
  }
}
