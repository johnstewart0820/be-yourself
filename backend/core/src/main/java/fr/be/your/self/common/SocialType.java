package fr.be.your.self.common;

public enum SocialType {
	INTERNAL(0),
	GOOGLE(1),
	FACEBOOK(2),
	UNKNOWN(-1);
	
	private final int value;
	
	SocialType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final SocialType parse(int value) {
		final SocialType[] values = SocialType.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
}
