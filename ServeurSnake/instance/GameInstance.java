package instance;

import java.util.ArrayList;
import java.util.List;

import client.Human;
import controller.ControllerSnakeGame;
import utils.GameInstanceFeatures;
import utils.HumanFeatures;

public class GameInstance {
  private boolean gameIsLaunched;
  private ArrayList<Human> playersInGame;
  private ControllerSnakeGame controller;
  private String map;
  private String levelAI;
  private Lobby lobby;

  public GameInstance(Lobby lobby) {
    this.lobby = lobby;
    playersInGame = new ArrayList<>();
    gameIsLaunched = false;
  }

  public boolean isGameInitialised() {
    return this.controller != null;
  }

  public void loadGame() {
    this.controller.setPlayers(lobby.getPlayers());
    this.controller.setLevelAI(levelAI);
  }

  public void launchGame() {
    this.gameIsLaunched = true;
  }

  public boolean isGameLaunched() {
    return gameIsLaunched;
  }

  public ControllerSnakeGame getController() {
    return this.controller;
  }

  public Lobby getLobby() {
    return this.lobby;
  }

  public Human getPlayer(Connection client) {
    for (int i = 0; i < this.lobby.getPlayers().size(); ++i) {
      if (this.lobby.getPlayers().get(i).isClient(client))
        return this.lobby.getPlayers().get(i);
    }
    return null;
  }

  public List<Human> getPlayersInGame() {
    return playersInGame;
  }

  public void addPlayerInGame(Human player) {
    playersInGame.add(player);
  }

  public void removePlayerInGame(Human player) {
    playersInGame.remove(player);
    if (this.playersInGame.isEmpty()) {
      System.out.println("Every player left the game, the game terminated");
      stopGame();
    }
  }

  public void stopGame() {
    this.controller = null;
    gameIsLaunched = false;
    this.init(map, levelAI);
    this.lobby.sendLobbyStatusToPlayers();
  }

  public void updateLobby() {
    this.setMap(this.controller != null ? this.controller.getInputMap().getFilename() : null);
  }

  public void init(String layout, String levelAI) {
    this.map = layout;
    this.levelAI = levelAI;
    this.controller = new ControllerSnakeGame(layout, playersInGame, levelAI);
    this.loadGame();
  }

  public String getLevelAI() {
    return levelAI;
  }

  public void setLevelAI(String levelAI) {
    this.levelAI = levelAI;
  }

  public void sendInfoToPlayers(String msg) {
    for (Human player : lobby.getPlayers()) {
      player.getClient().sendInfoToClient(msg);
    }
  }

  public void sendDataToPlayers(String msg) {
    for (Human player : lobby.getPlayers()) {
      player.getClient().sendGameDataToClient(msg);
    }
  }

  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }

  public List<HumanFeatures> getPlayersFeatures() {
    List<HumanFeatures> playersFeatures = new ArrayList<>();
    for (Human player : lobby.getPlayers()) {
      playersFeatures.add(player.getHumanFeatures());
    }
    return playersFeatures;
  }

  public GameInstanceFeatures getGameInstanceFeatures() {
    return new GameInstanceFeatures(this.getPlayersFeatures(), this.getMap(), this.getLevelAI());
  }

}
