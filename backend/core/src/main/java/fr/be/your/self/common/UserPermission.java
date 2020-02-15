package fr.be.your.self.common;

public enum UserPermission {
	READONLY(0),
	WRITE(1),
	UNKNOWN(-1);
	
	private final int value;
	
	UserPermission(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final UserPermission parse(int value) {
		final UserPermission[] values = UserPermission.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
}
