package utils;

import java.util.ArrayList;

public class GameFeatures {
  private boolean[][] walls;
  private int sizeX;
  private int sizeY;
  private ArrayList<FeaturesSnake> featuresSnakes;
  private ArrayList<FeaturesItem> featuresItems;
  private GameState state;
  private int turn;
  private long speed;

  public GameFeatures(boolean[][] walls, int sizeX, int sizeY, ArrayList<FeaturesSnake> featuresSnakes,
      ArrayList<FeaturesItem> featuresItems, GameState state, int turn, long speed) {
    this.walls = walls;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.featuresSnakes = featuresSnakes;
    this.featuresItems = featuresItems;
    this.state = state;
    this.turn = turn;
    this.speed = speed;
  }

  public GameFeatures(String Json) {
    String[] jsonSplit = Json.split(",");
    for (int i = 0; i < jsonSplit.length; i++) {
      if (jsonSplit[i].contains("walls")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        String[] jsonSplit3 = jsonSplit2[1].split("}");
        String[] jsonSplit4 = jsonSplit3[0].split("\\[");
        String[] jsonSplit5 = jsonSplit4[1].split("\\}");
        for (int j = 0; j < jsonSplit5.length; j++) {
          String[] jsonSplit6 = jsonSplit5[j].split(",");
          int x = Integer.parseInt(jsonSplit6[0].split(":")[1]);
          int y = Integer.parseInt(jsonSplit6[1].split(":")[1]);
          walls[x][y] = true;
        }
      } else if (jsonSplit[i].contains("sizeX")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        sizeX = Integer.parseInt(jsonSplit2[1]);
      } else if (jsonSplit[i].contains("sizeY")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        sizeY = Integer.parseInt(jsonSplit2[1]);
      } else if (jsonSplit[i].contains("featuresSnakes")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        String[] jsonSplit3 = jsonSplit2[1].split("}");
        String[] jsonSplit4 = jsonSplit3[0].split("\\[");
        String[] jsonSplit5 = jsonSplit4[1].split("\\}");
        for (int j = 0; j < jsonSplit5.length; j++) {
          featuresSnakes.add(new FeaturesSnake(jsonSplit5[j]));
        }
      } else if (jsonSplit[i].contains("featuresItems")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        String[] jsonSplit3 = jsonSplit2[1].split("}");
        String[] jsonSplit4 = jsonSplit3[0].split("\\[");
        String[] jsonSplit5 = jsonSplit4[1].split("\\}");
        for (int j = 0; j < jsonSplit5.length; j++) {
          featuresItems.add(new FeaturesItem(jsonSplit5[j]));
        }
      } else if (jsonSplit[i].contains("state")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        state = GameState.valueOf(jsonSplit2[1]);
      } else if (jsonSplit[i].contains("turn")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        turn = Integer.parseInt(jsonSplit2[1]);
      } else if (jsonSplit[i].contains("speed")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        speed = Long.parseLong(jsonSplit2[1]);
      } else {
        System.out.println("Error");
      }
    }
  }

  public boolean[][] getWalls() {
    return walls;
  }

  public int getSizeX() {
    return sizeX;
  }

  public int getSizeY() {
    return sizeY;
  }

  public ArrayList<FeaturesSnake> getFeaturesSnakes() {
    return featuresSnakes;
  }

  public ArrayList<FeaturesItem> getFeaturesItems() {
    return featuresItems;
  }

  public GameState getState() {
    return state;
  }

  public int getTurn() {
    return turn;
  }

  public long getSpeed() {
    return speed;
  }

  public void setWalls(boolean[][] walls) {
    this.walls = walls;
  }

  public void setSizeX(int sizeX) {
    this.sizeX = sizeX;
  }

  public void setSizeY(int sizeY) {
    this.sizeY = sizeY;
  }

  public void setFeaturesSnakes(ArrayList<FeaturesSnake> featuresSnakes) {
    this.featuresSnakes = featuresSnakes;
  }

  public void setFeaturesItems(ArrayList<FeaturesItem> featuresItems) {
    this.featuresItems = featuresItems;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public void setSpeed(long speed) {
    this.speed = speed;
  }

  public void toJson() {
    String json = "{\"walls\":" + walls + ",\"sizeX\":" + sizeX + ",\"sizeY\":" + sizeY + ",\"featuresSnakes\":"
        + featuresSnakes
        + ",\"featuresItems\":" + featuresItems + ",\"state\":" + state + ",\"turn\":" + turn + ",\"speed\":" + speed
        + "}";
  }

}
