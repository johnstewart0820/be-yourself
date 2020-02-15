package fr.be.your.self.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.be.your.self.common.UserType;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    /**
     * {@link UserType#getValue() }
     **/
    private int type;
    
    private String email;
    
    @JsonIgnore
    private String password;
    
    private String fullname;
    
    private String socialId;
    
    private String referralCode;

    
    public User() {
    }

    public User(String email, String password, String fullname) {
    	//this.type = SocialType.INTERNAL.getValue();
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
    
    public User(UserType type, String socialId, String email, String password, String fullname) {
    	//this.type = type.getValue();
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.socialId = socialId;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

	public String getReferralCode() {
		return referralCode;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}
}