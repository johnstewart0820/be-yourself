package fr.be.your.self.common;

public enum UserPermission {
	DENIED(0),
	READONLY(1),
	WRITE(2);
	
	private final int value;
	
	UserPermission(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != DENIED.value;
	}
	
	public static final UserPermission parse(int value) {
		final UserPermission[] values = UserPermission.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return DENIED;
	}
}
