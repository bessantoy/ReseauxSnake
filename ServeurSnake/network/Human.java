package network;

import java.net.Socket;

import utils.AgentAction;
import utils.HumanFeatures;

public class Human extends Player {
  private Connection client;
  private int id;
  AgentAction lastInput;

  public Human(Connection client, int id, String username) {
    super(username);
    this.client = client;
    this.id = id;
    this.lastInput = AgentAction.MOVE_DOWN;
  }

  public Connection getClient() {
    return client;
  }

  public void setClient(Connection client) {
    this.client = client;
  }

  public boolean isClient(Connection client) {
    return this.client.equals(client);
  }

  public boolean isClientFromSocket(Socket client) {
    return this.client.getClient().equals(client);
  }

  public AgentAction getLastInput() {
    return lastInput;
  }

  public void setLastInput(AgentAction lastInput) {
    this.lastInput = lastInput;
  }

  public HumanFeatures toHumanFeatures() {
    return new HumanFeatures(getUsername(), id);
  }

  @Override
  public boolean isHuman() {
    return true;
  }
}
