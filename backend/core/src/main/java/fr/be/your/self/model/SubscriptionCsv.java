package fr.be.your.self.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import com.opencsv.bean.CsvBindByName;

public class SubscriptionCsv {
	public static String DATE_FORMAT = "dd-MM-yyyy";

	@CsvBindByName(column = "Title")
	private String title;

	@CsvBindByName(column = "Last name")
	private String lastName;
	
	@CsvBindByName(column = "First name")
	private String firstName;
	
	@CsvBindByName(column = "Email")
	private String email;
	
	@CsvBindByName(column = "Subscription type")
	private String subtype;
	
	@CsvBindByName(column = "canal")
	private String canal;
	
	@CsvBindByName(column = "code")
	private String code;
	
	@CsvBindByName(column = "code type")	
	private String codeType;
	
	@CsvBindByName(column = "duration")
	private int duration;
	
	@CsvBindByName(column = "start date")
	private String startDate;
	
	@CsvBindByName(column = "end date")	
	private String endDate;
	
	@CsvBindByName(column = "termination asked")	
	private boolean terminationAsked;
	
	@CsvBindByName(column = "price")
	private BigDecimal price;
	
	@CsvBindByName(column = "gift card")
	private boolean giftCard;
	
	@CsvBindByName(column = "payment gateway")
	private String paymenGateway;
	
	public SubscriptionCsv() {}
	
	public SubscriptionCsv(Subscription subscription) {
		this.title = subscription.getUser().getTitle();
		this.firstName = subscription.getUser().getFirstName();
		this.lastName = subscription.getUser().getLastName();
		this.email = subscription.getUser().getEmail();
		this.subtype = subscription.getSubtype().getName();
		this.canal = subscription.getCanal();
		this.code = subscription.getCode();
		this.codeType = "XXX"; //TODO TVA add this
		this.duration = subscription.getDuration();
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.startDate = sdf.format(subscription.getSubscriptionStartDate());
		this.endDate = sdf.format(subscription.getSubscriptionEndDate());
		
		this.terminationAsked = subscription.isTerminationAsked();
		this.price = subscription.getPrice();
		this.paymenGateway = subscription.getPaymentGateway();
	}

	public static String getDATE_FORMAT() {
		return DATE_FORMAT;
	}

	public static void setDATE_FORMAT(String dATE_FORMAT) {
		DATE_FORMAT = dATE_FORMAT;
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

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
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

	public boolean isGiftCard() {
		return giftCard;
	}

	public void setGiftCard(boolean giftCard) {
		this.giftCard = giftCard;
	}

	public String getPaymenGateway() {
		return paymenGateway;
	}

	public void setPaymenGateway(String paymenGateway) {
		this.paymenGateway = paymenGateway;
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	
	
	
}
