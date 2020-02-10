package fr.be.your.self.exception;

import fr.be.your.self.common.StatusCode;

public class ValidationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7684733153159019176L;
	
	private final StatusCode code;
	private final String message;
	
	private String data;
	
    public ValidationException(StatusCode code, String message) {
    	this.code = code;
        this.message = message;
    }

    public ValidationException(StatusCode code, String message, String data) {
    	this(code, message);
    	
    	this.data = data;
    }
    
    public StatusCode getCode() {
		return code;
	}

	public String getMessage() {
        return message;
    }

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}	
}
