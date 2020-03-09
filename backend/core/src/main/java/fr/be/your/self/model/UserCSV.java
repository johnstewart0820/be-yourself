package fr.be.your.self.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import fr.be.your.self.common.LoginType;
import fr.be.your.self.common.UserUtils;

public class UserCSV {
	@CsvBindByName(column = "Title")
    @CsvBindByPosition(position = 0)
	private String title;
	
	@CsvBindByName(column = "Last Name")
    @CsvBindByPosition(position = 1)
	private String lastName;
	
	@CsvBindByName(column = "First Name")
    @CsvBindByPosition(position = 2)
	private String firstName;
	
	@CsvBindByName(column = "Email")
    @CsvBindByPosition(position = 3)
	private String email;
	
	@CsvBindByName(column = "Login Type")	
    @CsvBindByPosition(position = 4)
	private String loginType;
	
	@CsvBindByName(column = "Status")
    @CsvBindByPosition(position = 5)
	private int status;
	
	@CsvBindByName(column = "Referral Code")	
    @CsvBindByPosition(position = 6)
	private String referralCode;
	
	@CsvBindByName(column = "Account Type")	
    @CsvBindByPosition(position = 7)
	private String userType;

	@CsvBindByName(column = "Subscription Type")	
    @CsvBindByPosition(position = 8)
	private String subscriptionType;


	public UserCSV() {
		
	}
	
	public UserCSV(User user) {
		this.title = user.getTitle();
		this.lastName = user.getLastName();
		this.firstName = user.getFirstName();
		this.email = user.getEmail();
		this.loginType = LoginType.toStrValue(user.getLoginType());
		this.status = user.getStatus();
		this.referralCode = user.getReferralCode();
		this.userType = user.getUserType();
		Subscription sub = UserUtils.findSubscriptionUser(user);
		if (sub != null && sub.getSubtype() != null) {
			this.subscriptionType = sub.getSubtype().getName();
		}
	}

	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
	

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
	
	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

}
