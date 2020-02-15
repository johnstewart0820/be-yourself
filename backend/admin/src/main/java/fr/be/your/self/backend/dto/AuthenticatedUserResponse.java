package fr.be.your.self.backend.dto;

import java.io.Serializable;

public class AuthenticatedUserResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1782443355025075836L;

	private String email;
	private String fullName;
	private String avatar;
	private String role;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
