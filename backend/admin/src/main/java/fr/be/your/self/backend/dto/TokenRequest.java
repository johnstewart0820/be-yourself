package fr.be.your.self.backend.dto;

import java.io.Serializable;

public class TokenRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8085128823890214663L;

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
