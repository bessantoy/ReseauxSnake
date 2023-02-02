package model;

import utils.AgentAction;
import utils.GameFeatures;

public class Network {

  private AgentAction inputMoveHuman;

  private GameFeatures gameFeatures;

  public Network() {
    this.inputMoveHuman = null;
    this.gameFeatures = null;
  }

  public AgentAction getInputMoveHuman() {
    return inputMoveHuman;
  }

  public GameFeatures getGameFeatures() {
    return gameFeatures;
  }

  public void setInputMoveHuman(AgentAction inputMoveHuman) {
    this.inputMoveHuman = inputMoveHuman;
  }

  public void setGameFeatures(GameFeatures gameFeatures) {
    this.gameFeatures = gameFeatures;
  }

  public void readGameFeatures(String Json) {
    this.gameFeatures = new GameFeatures(Json);
  }

  public void sendClientSignal(String signal) {
    // TODO
  }
}
