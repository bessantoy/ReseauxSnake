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
        walls = new boolean[jsonSplit5.length][jsonSplit5[0].split(",").length];
        for (int j = 0; j < jsonSplit5.length; j++) {
          String[] jsonSplit6 = jsonSplit5[j].split(",");
          for (int k = 0; k < jsonSplit6.length; k++) {
            walls[j][k] = Boolean.parseBoolean(jsonSplit6[k].split(":")[1]);
          }
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
        featuresSnakes = new ArrayList<FeaturesSnake>();
        for (int j = 0; j < jsonSplit5.length; j++) {
          featuresSnakes.add(new FeaturesSnake(jsonSplit5[j]));
        }
      } else if (jsonSplit[i].contains("featuresItems")) {
        String[] jsonSplit2 = jsonSplit[i].split(":");
        String[] jsonSplit3 = jsonSplit2[1].split("}");
        String[] jsonSplit4 = jsonSplit3[0].split("\\[");
        String[] jsonSplit5 = jsonSplit4[1].split("\\}");
        featuresItems = new ArrayList<FeaturesItem>();
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
        System.out.println("Error in Parsing GameFeatures");
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

  public String toJson() {
    String wallsString = "[";
    for (int i = 0; i < sizeX; i++) {
      for (int j = 0; j < sizeY; j++) {
        if (walls[i][j]) {
          wallsString += "{\"x\":" + i + ",\"y\":" + j + "},";
        }
      }
    }
    wallsString = wallsString.substring(0, wallsString.length() - 1);
    wallsString += "]";
    String itemsString = "[";
    for (int i = 0; i < this.featuresItems.size(); i++) {
      itemsString += this.featuresItems.get(i).toJson() + ",";
    }
    itemsString = itemsString.substring(0, itemsString.length() - 1);
    itemsString += "]";
    String snakesString = "[";
    for (int i = 0; i < this.featuresSnakes.size(); i++) {
      snakesString += this.featuresSnakes.get(i).toJson() + ",";
    }
    snakesString = snakesString.substring(0, snakesString.length() - 1);
    snakesString += "]";

    return "{\"walls\":" + wallsString + ",\"sizeX\":" + sizeX + ",\"sizeY\":" + sizeY + ",\"featuresSnakes\":"
        + snakesString
        + ",\"featuresItems\":" + itemsString + ",\"state\":" + state + ",\"turn\":" + turn + ",\"speed\":" + speed
        + "}";
  }

}
