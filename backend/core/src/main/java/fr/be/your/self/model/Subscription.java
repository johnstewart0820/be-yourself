package fr.be.your.self.model;

import java.math.BigDecimal;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Subscription extends PO<Integer> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int userId;
	private int subtypeId;
	private boolean status;
	private String ipAddress;
	private int duration;
	private Date validStartDate;
	private Date validEndDate;
	private Date subscriptionStartDate;
	private Date subscriptionEndDate;
	private boolean terminationAsked;
	private BigDecimal price;
	private int paymentStatus;
	private int paymentGateway;
	private String code;
	private int codeType;
	
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getDisplay() {
		return "XXXX"; //TODO TVA use this
	}

	public int getUserId() {
		return userId;
	}
	

	public void setId(int id) {
		this.id = id;
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

	public int getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(int paymentGateway) {
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
