package fr.be.your.self.exception;

import fr.be.your.self.common.ErrorStatusCode;

public class ValidationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7684733153159019176L;
	
	private final ErrorStatusCode code;
	private final String message;
	
	private String data;
	
    public ValidationException(ErrorStatusCode code, String message) {
    	this.code = code;
        this.message = message;
    }

    public ValidationException(ErrorStatusCode code, String message, String data) {
    	this(code, message);
    	
    	this.data = data;
    }
    
    public ErrorStatusCode getCode() {
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
