package fr.be.your.self.common;

public enum BusinessCodeStatus {
	DRAFT(0),
	ACTIVE(1),
	INACTIVE(2),
	UNKNOWN(-1);
	
	private final int value;
	
	BusinessCodeStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final BusinessCodeStatus parse(int value) {
		final BusinessCodeStatus[] values = BusinessCodeStatus.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
}
