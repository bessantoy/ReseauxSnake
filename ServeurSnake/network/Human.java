package network;

import utils.AgentAction;

public class Human implements Player {
  private Connection client;
  private String username;
  AgentAction lastInput;

  public Human(Connection client, String username) {
    this.client = client;
    this.username = username;
    this.lastInput = AgentAction.MOVE_DOWN;
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

  public AgentAction getLastInput() {
    return lastInput;
  }

  public void setLastInput(AgentAction lastInput) {
    this.lastInput = lastInput;
  }

  @Override
  public boolean isHuman() {
    return true;
  }
}
