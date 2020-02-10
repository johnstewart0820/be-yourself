package fr.be.your.self.backend.dto;

import java.io.Serializable;

public class UserCreateRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4376270364434657974L;
	
	private String email;
	private String password;
	private String fullname;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

}
