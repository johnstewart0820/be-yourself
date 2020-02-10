package fr.be.your.self.common;

public enum AuthenticationStrategy {
	JWT("JWT"),
	//BEARER("Bearer")
	;
	
	private final String value;

	private AuthenticationStrategy(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
