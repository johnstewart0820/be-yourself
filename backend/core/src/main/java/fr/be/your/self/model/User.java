package fr.be.your.self.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.be.your.self.common.SocialType;
import fr.be.your.self.common.UserStatus;
import fr.be.your.self.common.UserType;
import fr.be.your.self.util.StringUtils;

@Entity
@Table(name = "User")
public class User extends PO<Integer> {

	@Id
	@Column(name = "UserID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;

	/**
	 * {@link UserType#getValue() }
	 **/
	@Column(name = "UserType", length = 3)
	private String userType;
	
	@Column(name = "Email", length = 255)
	private String email;

	@JsonIgnore
	@Column(name = "Password", length = 120)
	private String password;

	@Column(name = "FirstName", length = 60)
	private String firstName;

	@Column(name = "LastName", length = 60)
	private String lastName;

	@Column(name = "Civility", length = 60)
	private String civility;

	@Column(name = "SocialLogin", length = 64)
	private String socialLogin;

	/**
	 * {@link SocialType#getValue() }
	 **/
	@Column(name = "SocialType")
	private int socialType;

	@Column(name = "ReferralCode", length = 60)
	private String referralCode;
	
	/**
	 * {@link UserStatus#getValue() }
	 **/
	@Column(name = "UserStatus")
	private int userStatus;
	
	@Column(name = "Avatar", length = 255)
	private String avatar;
	
	public User() {
	}

	public User(String email, String password, UserType userType, 
			String firstName, String lastName, 
			SocialType socialType, String socialLogin) {
		this.email = email;
		this.password = password;
		this.userType = userType.getValue();
		this.firstName = firstName;
		this.lastName = lastName;
		this.socialLogin = socialLogin;
		this.socialType = socialType.getValue();
		this.userStatus = UserStatus.DRAFT.getValue();
	}
	
	public User(String email, String password, UserType userType, String firstName, String lastName) {
		this(email, password, userType, firstName, lastName, SocialType.INTERNAL, null);
	}

	@Override
	public Integer getId() {
		return this.userId;
	}

	public String getFullname() {
		String fullname = StringUtils.isNullOrSpace(this.firstName) ? "" : this.firstName.trim();
		
		if (!StringUtils.isNullOrSpace(this.lastName)) {
			fullname += (fullname.isEmpty() ? "" : " ") + this.lastName.trim();	
		}
		
		return fullname;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public int getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
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

	public String getCivility() {
		return civility;
	}

	public void setCivility(String civility) {
		this.civility = civility;
	}

	public String getSocialLogin() {
		return socialLogin;
	}

	public void setSocialLogin(String socialLogin) {
		this.socialLogin = socialLogin;
	}

	public int getSocialType() {
		return socialType;
	}

	public void setSocialType(int socialType) {
		this.socialType = socialType;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
}