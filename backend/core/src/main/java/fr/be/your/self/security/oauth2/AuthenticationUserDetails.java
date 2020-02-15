package fr.be.your.self.security.oauth2;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class AuthenticationUserDetails extends org.springframework.security.core.userdetails.User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4236086521519216718L;
	
	private final Integer userId;
	private String fullName;
	private String avatar;
	
	public AuthenticationUserDetails(Integer userId, String username, 
			String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
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
}
