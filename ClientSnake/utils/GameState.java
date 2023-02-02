package utils;

public enum GameState {
  PLAYING("playing"), PAUSED("paused"), STARTING("starting"), OVER("over");

  private String state;

  private GameState(String state) {
    this.state = state;
  }

  public String toJson() {
    return "{\"state\":\"" + state + "\"}";
  }
}
