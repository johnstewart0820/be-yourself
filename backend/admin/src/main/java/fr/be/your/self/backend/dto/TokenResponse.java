package fr.be.your.self.backend.dto;

import java.io.Serializable;

public class TokenResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;

	private final AuthenticatedUserResponse user;
	private final String strategy;
	private final String token;
	
	public TokenResponse(AuthenticatedUserResponse user, String strategy, String token) {
		this.user = user;
		this.strategy = strategy;
		this.token = token;
	}

	public AuthenticatedUserResponse getUser() {
		return this.user;
	}
	
	public String getStrategy() {
		return strategy;
	}

	public String getToken() {
		return this.token;
	}
}