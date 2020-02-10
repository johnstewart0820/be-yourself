package fr.be.your.self.common;

public enum StatusCode {
	OK(""),
	
	UNAUTHORIZED("unauthorized"),
	INVALID_CREDENTIALS("invalid.credentials"),
	
	PROCESSING_ERROR("processing.error"),
	
	INVALID_ID("invalid.id"),
	INVALID_PARAMETER("invalid.parameter"),
	
	USERNAME_EXISTED("username.existed");
	
	private final String value;

	private StatusCode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
