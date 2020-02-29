package fr.be.your.self.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserType;

@Entity
public class User extends PO<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int status;

	private String firstName;

	private String lastName;

	private String title;
	
	private String email;

	@JsonIgnore
	private String password;

	private String socialLogin;

	private int loginType;

	private String referralCode;

	private String userType;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true )
	private List<Permission> permissions;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true )
	private List<Subscription> subscriptions;
	
	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	private String activateCode;

	private long activateTimeout;

	public User() {
	}

	public User(String email, String encodedPassword, String userType, String firstName, String lastName) {
		this.email = email;
		this.password = encodedPassword;
		this.userType = userType;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public Integer getId() {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSocialLogin() {
		return socialLogin;
	}

	public void setSocialLogin(String socialLogin) {
		this.socialLogin = socialLogin;
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

	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
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

	@Override
	public String getDisplay() {
		return this.getFullName();
	}
	
	public String getStatusDescription() {
		return UserStatus.getStatusDescription(status);
	}
	
	public String getUserTypeDescription() {
		return UserType.getStatusDescription(userType);
	}
}