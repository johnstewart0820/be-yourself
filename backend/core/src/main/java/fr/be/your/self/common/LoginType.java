package fr.be.your.self.common;

public enum LoginType {
	PASSWORD(0),
	GOOGLE(1),
	FACEBOOK(2),
	UNKNOWN(-1);
	
	private final int value;
	
	LoginType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final LoginType parse(int value) {
		final LoginType[] values = LoginType.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
	
	public static String toStrValue(int value) {
		switch (value) {
			case 0: return "PASSWORD";
			case 1: return "GOOGLE";
			case 2: return "FACEBOOK";
		}
		return "UNKNOWN";
	}
}
