package fr.be.your.self.exception;

import fr.be.your.self.common.StatusCode;

public class BusinessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 631660168741702860L;
	
	private final StatusCode code;
	
	private String message;
	private String data;
	
	public BusinessException(StatusCode code, String message) {
		this.code = code;
		this.message = message;
	}

	public BusinessException(StatusCode code) {
		this(code, null);
	}
	
	public StatusCode getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}