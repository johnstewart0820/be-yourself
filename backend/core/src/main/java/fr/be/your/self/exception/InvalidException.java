package fr.be.your.self.exception;

import fr.be.your.self.common.StatusCode;

public class InvalidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4522548717124837250L;

	private final StatusCode code;
	private final String parameter;

	public InvalidException(StatusCode code, String parameter) {
		this.code = code;
		this.parameter = parameter;
	}

	public StatusCode getCode() {
		return code;
	}

	public String getParameter() {
		return parameter;
	}

}
