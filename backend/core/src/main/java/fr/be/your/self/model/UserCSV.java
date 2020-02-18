package fr.be.your.self.model;

import com.opencsv.bean.CsvBindByName;

public class UserCSV {
	@CsvBindByName(column = "Title")
	private String title;
	
	@CsvBindByName(column = "Last Name")
	private String lastName;
	
	@CsvBindByName(column = "First Name")
	private String firstName;
	
	@CsvBindByName(column = "Email")	
	private String email;

	@CsvBindByName(column = "Status")	
	private int status;
	
	@CsvBindByName(column = "Referral Code")	
	private String referralCode;
	
	@CsvBindByName(column = "User Type")	
	private String userType;


	
	public UserCSV() {
		
	}
	
	public UserCSV(User user) {
		this.title = user.getTitle();
		this.lastName = user.getLastName();
		this.firstName = user.getFirstName();
		this.email = user.getEmail();
		this.status = user.getStatus();
		this.referralCode = user.getReferralCode();
		this.userType = user.getUserType();
	}
	
	//private String subscriptionType;
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
	

	
	
	
}
