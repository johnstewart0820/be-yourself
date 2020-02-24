package fr.be.your.self.backend.dto;

import java.io.Serializable;
import java.util.List;

import fr.be.your.self.model.Permission;
import fr.be.your.self.model.User;

public class UserDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4192799688425161368L;
	private int id;
	private int status;
	private String firstName;
	private String lastName;
	private String title;
	private String email;
	private int loginType;
	private String referralCode;
	private String userType;
	private List<Permission> permissions;
	private String activateCode;
	private long activateTimeout;
	
	public UserDto() {
	}
	public UserDto(User domain) {
		this();
	
		if (domain != null) {
			this.activateCode = domain.getActivateCode();
			this.activateTimeout = domain.getActivateTimeout();
			this.email = domain.getEmail();
			this.firstName = domain.getFirstName();
			this.id = domain.getId();
			this.lastName = domain.getLastName();
			this.loginType = domain.getLoginType();
			this.permissions = domain.getPermissions();
			this.referralCode = domain.getReferralCode();
			this.status = domain.getStatus();
			this.title = domain.getTitle();
			this.userType = domain.getUserType();		
		
		}
	}
	
	
	public void copyToDomain(User domain) {
		domain.setActivateCode(this.activateCode);
		domain.setActivateTimeout(this.activateTimeout);
		domain.setEmail(this.email);
		domain.setFirstName(this.firstName);
		domain.setId(this.id);
		domain.setLastName(this.lastName);
		domain.setLoginType(this.loginType);
		domain.setPermissions(this.permissions);
		domain.setReferralCode(this.referralCode);
		domain.setStatus(this.status);
		domain.setTitle(this.title);
		domain.setUserType(this.userType);
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getLoginType() {
		return loginType;
	}
	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}
	public String getReferralCode() {
		return referralCode;
	}
	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	public String getActivateCode() {
		return activateCode;
	}
	public void setActivateCode(String activateCode) {
		this.activateCode = activateCode;
	}
	public long getActivateTimeout() {
		return activateTimeout;
	}
	public void setActivateTimeout(long activateTimeout) {
		this.activateTimeout = activateTimeout;
	}
	
	
}
