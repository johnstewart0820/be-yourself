package fr.be.your.self.model;

import java.math.BigDecimal;

import com.opencsv.bean.CsvBindByName;

public class SubscriptionCsv {
	@CsvBindByName(column = "Civility")
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
	
}
