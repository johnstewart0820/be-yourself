package fr.be.your.self.exception;

import fr.be.your.self.common.ErrorStatusCode;

public class InvalidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4522548717124837250L;

	private final ErrorStatusCode code;
	private final String parameter;

	public InvalidException(ErrorStatusCode code, String parameter) {
		this.code = code;
		this.parameter = parameter;
	}

	public ErrorStatusCode getCode() {
		return code;
	}

	public String getParameter() {
		return parameter;
	}

}
