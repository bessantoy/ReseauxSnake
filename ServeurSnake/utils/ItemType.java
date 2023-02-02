package utils;

public enum ItemType {
	APPLE("apple"), BOX("box"), SICK_BALL("sick"), INVINCIBILITY_BALL("invincibility");

	private String type;

	private ItemType(String type) {
		this.type = type;
	}

}