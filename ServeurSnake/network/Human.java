package network;

import java.net.Socket;

import utils.AgentAction;
import utils.HumanFeatures;

public class Human extends Player {
  private Connection client;
  AgentAction lastInput;

  public Human(Connection client, String username) {
    super(username);
    this.client = client;
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
    return new HumanFeatures(getUsername(), client.getClient().getInetAddress());
  }

  @Override
  public boolean isHuman() {
    return true;
  }
}
