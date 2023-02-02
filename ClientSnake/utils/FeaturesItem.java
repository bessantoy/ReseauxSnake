package utils;

public class FeaturesItem {

	private int x;
	private int y;
	private ItemType itemType;

	public FeaturesItem(int x, int y, ItemType itemType) {

		this.x = x;
		this.y = y;
		this.itemType = itemType;

	}

	public FeaturesItem(String json) {
		String[] parts = json.split(",");
		for (String part : parts) {
			String[] keyValue = part.split(":");
			if (keyValue[0].equals("\"x\"")) {
				x = Integer.parseInt(keyValue[1]);
			} else if (keyValue[0].equals("\"y\"")) {
				y = Integer.parseInt(keyValue[1]);
			} else if (keyValue[0].equals("\"type\"")) {
				itemType = ItemType.valueOf(keyValue[1].replace("\"", ""));
			}
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public String toJson() {
		return "{\"x\":" + x + ",\"y\":" + y + ",\"type\":\"" + itemType + "\"}";
	}

}
