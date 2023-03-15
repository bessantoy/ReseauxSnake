package client;

public class AI extends Player {
  private String level;

  public AI(String username, String level) {
    super(username);
    this.level = level;
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
