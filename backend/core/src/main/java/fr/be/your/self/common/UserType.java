package fr.be.your.self.common;

public enum UserType {
	//SUPER_ADMIN("SAM"),
	ADMIN("ADM"),
	B2B("B2B"),
	B2C("B2C"),
	PROFESSIONAL("PRO"),
	USER("USR");
	
	private final String value;
	
	UserType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public boolean isValid() {
		return this.value != USER.value;
	}
	
	public static final UserType parse(String value) {
		if (value == null || value.isEmpty()) {
			return USER;
		}
		
		final UserType[] values = UserType.values();
		for (int i = 0; i < values.length; i++) {
			if (value.equalsIgnoreCase(values[i].value)) {
				return values[i];
			}
		}
		
		return USER;
	}

	public static String getStatusDescription(String val) {
		switch (val) {
			//case "SAM": return "SUPER ADMIN";
			case "ADM": return "ADMIN";
			case "B2B": return "B2B";
			case "B2C": return "B2C";
			case "PRO": return "PRO";
			
			default: return "UNKNOWN";		
		}
	}
}
