package fr.be.your.self.backend.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import fr.be.your.self.model.Subscription;
import fr.be.your.self.model.SubscriptionType;
import fr.be.your.self.model.User;

public class SubscriptionDto  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4446631053119483358L;

	private int id;
	
	private int userId;
	private int subtypeId;
	private boolean status;
	private boolean terminationAsked;
	private String ipAddress;
	private int duration;
	private Date validStartDate;
	private Date validEndDate;
	private Date subscriptionStartDate;
	private Date subscriptionEndDate;
	private BigDecimal price;
	private int paymentStatus;
	private String paymentGateway;
	private String code;
	private int codeType;
	
	public SubscriptionDto() {
		
	}
	public SubscriptionDto(Subscription domain) {
		this();
		
		this.status = false;
		this.terminationAsked = false;
		
		if (domain != null) {
			this.id = domain.getId().intValue();
			this.code = domain.getCode();
			this.codeType = domain.getCodeType();
			this.duration = domain.getDuration();
			this.ipAddress = domain.getIpAddress();
			this.paymentGateway = domain.getPaymentGateway();
			this.paymentStatus = domain.getPaymentStatus();
			this.price = domain.getPrice();
			this.status = domain.isStatus();
			this.subscriptionEndDate = domain.getSubscriptionEndDate();
			this.subscriptionStartDate = domain.getSubscriptionStartDate();
			this.subtypeId = domain.getSubtype().getId();
			this.terminationAsked = domain.isTerminationAsked();
			this.userId = domain.getUser().getId();
			this.validEndDate = domain.getValidEndDate();
			this.validStartDate = domain.getValidStartDate();
		
		}
	}

	public void copyToDomain(Subscription domain) {
		domain.setCode(code);
		domain.setCodeType(codeType);
		domain.setDuration(duration);
		domain.setIpAddress(ipAddress);
		domain.setPaymentGateway(paymentGateway);
		domain.setPaymentStatus(paymentStatus);
		domain.setPrice(price);
		domain.setStatus(status);
		domain.setSubscriptionEndDate(subscriptionEndDate);
		domain.setSubscriptionStartDate(subscriptionStartDate);
		domain.setTerminationAsked(terminationAsked);
		domain.setValidEndDate(validEndDate);
		domain.setValidStartDate(validStartDate);
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getSubtypeId() {
		return subtypeId;
	}
	public void setSubtypeId(int subtypeId) {
		this.subtypeId = subtypeId;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Date getValidStartDate() {
		return validStartDate;
	}
	public void setValidStartDate(Date validStartDate) {
		this.validStartDate = validStartDate;
	}
	public Date getValidEndDate() {
		return validEndDate;
	}
	public void setValidEndDate(Date validEndDate) {
		this.validEndDate = validEndDate;
	}
	public Date getSubscriptionStartDate() {
		return subscriptionStartDate;
	}
	public void setSubscriptionStartDate(Date subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}
	public Date getSubscriptionEndDate() {
		return subscriptionEndDate;
	}
	public void setSubscriptionEndDate(Date subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}
	public boolean isTerminationAsked() {
		return terminationAsked;
	}
	public void setTerminationAsked(boolean terminationAsked) {
		this.terminationAsked = terminationAsked;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public int getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getPaymentGateway() {
		return paymentGateway;
	}
	public void setPaymentGateway(String paymentGateway) {
		this.paymentGateway = paymentGateway;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getCodeType() {
		return codeType;
	}
	public void setCodeType(int codeType) {
		this.codeType = codeType;
	}
	
	
	
}
