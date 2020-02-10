package fr.be.your.self.security.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class JwtUserDetails extends org.springframework.security.core.userdetails.User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4236086521519216718L;
	
	private final Integer userId;
	private String fullname;
	private String avatar;
	
	public JwtUserDetails(Integer userId, String username, 
			String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
