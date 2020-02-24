package fr.be.your.self.exception;

import fr.be.your.self.common.ErrorStatusCode;

public class BusinessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 631660168741702860L;
	
	private final ErrorStatusCode code;
	
	private String message;
	private String data;
	
	public BusinessException(ErrorStatusCode code, String message) {
		this.code = code;
		this.message = message;
	}

	public BusinessException(ErrorStatusCode code) {
		this(code, null);
	}
	
	public ErrorStatusCode getCode() {
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