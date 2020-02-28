package fr.be.your.self.common;

public enum BusinessCodeType {
	B2B_MULTIPLE(0),
	B2B_UNIQUE(1),
	B2C_DISCOUNT_100(2),
	B2C_DISCOUNT(3),
	GIFT_CARD(4),
	UNKNOWN(-1);
	
	private final int value;
	
	BusinessCodeType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != UNKNOWN.value;
	}
	
	public static final BusinessCodeType parse(int value) {
		final BusinessCodeType[] values = BusinessCodeType.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
}
