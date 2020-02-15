package fr.be.your.self.common;

public enum UserStatus {
	DRAFT(0),
	ACTIVE(1),
	DENIED(2),
	UNKNOWN(-1);
	
	private final int value;
	
	UserStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final UserStatus parse(int value) {
		final UserStatus[] values = UserStatus.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
}
