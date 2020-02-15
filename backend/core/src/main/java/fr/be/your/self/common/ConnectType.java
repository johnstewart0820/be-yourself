package fr.be.your.self.common;

public enum ConnectType {
	PASSWORD(0),
	GOOGLE(1),
	FACEBOOK(2),
	UNKNOWN(-1);
	
	private final int value;
	
	ConnectType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final ConnectType parse(int value) {
		final ConnectType[] values = ConnectType.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
}
