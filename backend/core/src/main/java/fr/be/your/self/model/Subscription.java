package fr.be.your.self.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Subscription extends PO<Integer> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userID")
    private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subtypeId")
	private SubscriptionType subtype;
	
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
	private String paymentGateway;
	private String code;
	private int codeType;
	private String canal;
	private boolean autoRenew;


	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SubscriptionType getSubtype() {
		return subtype;
	}

	public void setSubtype(SubscriptionType subtype) {
		this.subtype = subtype;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getDisplay() {
		return "XXXX"; //TODO TVA use this
	}
	

	public void setId(int id) {
		this.id = id;
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

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public boolean isAutoRenew() {
		return autoRenew;
	}

	public void setAutoRenew(boolean autoRenew) {
		this.autoRenew = autoRenew;
	}

}
