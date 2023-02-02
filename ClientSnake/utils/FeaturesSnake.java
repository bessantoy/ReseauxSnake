package utils;

import java.util.ArrayList;

public class FeaturesSnake {

	ArrayList<Position> positions;

	private AgentAction lastAction;

	ColorSnake colorSnake;

	boolean isInvincible;
	boolean isSick;

	public FeaturesSnake(ArrayList<Position> positions, AgentAction lastAction, ColorSnake colorSnake,
			boolean isInvincible, boolean isSick) {

		this.positions = positions;
		this.colorSnake = colorSnake;
		this.lastAction = lastAction;

		this.isInvincible = isInvincible;

		this.isSick = isSick;

	}

	public FeaturesSnake(String json) {

		positions = new ArrayList<Position>();

		String[] jsonSplit = json.split(",");

		for (int i = 0; i < jsonSplit.length; i++) {

			if (jsonSplit[i].contains("positions")) {

				String[] jsonSplit2 = jsonSplit[i].split(":");

				String[] jsonSplit3 = jsonSplit2[1].split("}");

				String[] jsonSplit4 = jsonSplit3[0].split("\\[");

				String[] jsonSplit5 = jsonSplit4[1].split("\\}");

				for (int j = 0; j < jsonSplit5.length; j++) {

					String[] jsonSplit6 = jsonSplit5[j].split(",");

					int x = Integer.parseInt(jsonSplit6[0].split(":")[1]);
					int y = Integer.parseInt(jsonSplit6[1].split(":")[1]);

					positions.add(new Position(x, y));

				}

			} else if (jsonSplit[i].contains("colorSnake")) {

				String[] jsonSplit2 = jsonSplit[i].split(":");

				String[] jsonSplit3 = jsonSplit2[1].split("}");

				String[] jsonSplit4 = jsonSplit3[0].split("\"");

				colorSnake = ColorSnake.valueOf(jsonSplit4[1]);

			} else if (jsonSplit[i].contains("lastAction")) {

				String[] jsonSplit2 = jsonSplit[i].split(":");

				String[] jsonSplit3 = jsonSplit2[1].split("}");

				String[] jsonSplit4 = jsonSplit3[0].split("\"");

				lastAction = AgentAction.valueOf(jsonSplit4[1]);

			} else if (jsonSplit[i].contains("isInvincible")) {

				String[] jsonSplit2 = jsonSplit[i].split(":");

				String[] jsonSplit3 = jsonSplit2[1].split("}");

				isInvincible = Boolean.parseBoolean(jsonSplit3[0]);

			} else if (jsonSplit[i].contains("isSick")) {

				String[] jsonSplit2 = jsonSplit[i].split(":");

				String[] jsonSplit3 = jsonSplit2[1].split("}");

				isSick = Boolean.parseBoolean(jsonSplit3[0]);

			}

		}
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}

	public ColorSnake getColorSnake() {
		return colorSnake;
	}

	public void setColorSnake(ColorSnake colorSnake) {
		this.colorSnake = colorSnake;
	}

	public boolean isInvincible() {
		return isInvincible;
	}

	public void setInvincible(boolean isInvincible) {
		this.isInvincible = isInvincible;
	}

	public boolean isSick() {
		return isSick;
	}

	public void setSick(boolean isSick) {
		this.isSick = isSick;
	}

	public AgentAction getLastAction() {
		return lastAction;
	}

	public void setLastAction(AgentAction lastAction) {
		this.lastAction = lastAction;
	}

	public String toJson() {

		String json = "{\"positions\":[";

		for (Position position : positions) {
			json += position.toJson() + ",";
		}

		json = json.substring(0, json.length() - 1);

		json += "],\"colorSnake\":" + colorSnake.toJson() + ",\"lastAction\":" + lastAction.toJson() + ",\"isInvincible\":"
				+ isInvincible + ",\"isSick\":" + isSick + "}";

		return json;
	}

}
