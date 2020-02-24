package fr.be.your.self.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import fr.be.your.self.common.BusinessCodeStatus;
import fr.be.your.self.common.BusinessCodeType;

@Entity
public class BusinessCode extends PO<String> {
	
	@Id
	private String code;
	
	/**
	 * {@link BusinessCodeType#getValue()}
	 **/
	private int codeType;
	
	/**
	 * {@link BusinessCodeStatus#getValue()}
	 **/
	private int status;
	
	private Date validityStartDate;
	
	private Date validityEndDate;
	
	/**
	 * Beneficiary (company) for B2B
	 **/
	private String beneficiary;		// company
	
	/**
	 * Maximum users amount	for B2B
	 **/
	private int maxUserCount;
	
	/**
	 * Number of times the code has been used for B2B
	 **/
	private int usedTimes;
	
	/**
	 * Price of the deal (euro) for B2B
	 **/
	private BigDecimal dealPrice;
	
	/**
	 * Price paid per user (recalculated if the price of the deal or the maximum amount of users is changed)
	 **/
	private BigDecimal pricePerUser;
	
	/**
	 * The maximum amount of usage is 1 for B2C100
	 **/
	private int maxUsageCount;
	
	/**
	 * A percentage of reduction for B2C, with B2C100 is 100 always
	 **/
	private BigDecimal discountPercentage;
	
	private BigDecimal priceGiftCard;
	
	private String buyerEmailGiftCard;
	
	private String userEmailGiftCard;
	
	private long durationGiftCard;
	
	@Override
	public String getId() {
		return this.code;
	}

	@Override
	public String getDisplay() {
		return this.code;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getValidityStartDate() {
		return validityStartDate;
	}

	public void setValidityStartDate(Date validityStartDate) {
		this.validityStartDate = validityStartDate;
	}

	public Date getValidityEndDate() {
		return validityEndDate;
	}

	public void setValidityEndDate(Date validityEndDate) {
		this.validityEndDate = validityEndDate;
	}

	public String getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}

	public int getMaxUserCount() {
		return maxUserCount;
	}

	public void setMaxUserCount(int maxUserCount) {
		this.maxUserCount = maxUserCount;
	}

	public int getUsedTimes() {
		return usedTimes;
	}

	public void setUsedTimes(int usedTimes) {
		this.usedTimes = usedTimes;
	}

	public BigDecimal getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(BigDecimal dealPrice) {
		this.dealPrice = dealPrice;
	}

	public BigDecimal getPricePerUser() {
		return pricePerUser;
	}

	public void setPricePerUser(BigDecimal pricePerUser) {
		this.pricePerUser = pricePerUser;
	}

	public int getMaxUsageCount() {
		return maxUsageCount;
	}

	public void setMaxUsageCount(int maxUsageCount) {
		this.maxUsageCount = maxUsageCount;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public BigDecimal getPriceGiftCard() {
		return priceGiftCard;
	}

	public void setPriceGiftCard(BigDecimal priceGiftCard) {
		this.priceGiftCard = priceGiftCard;
	}

	public String getBuyerEmailGiftCard() {
		return buyerEmailGiftCard;
	}

	public void setBuyerEmailGiftCard(String buyerEmailGiftCard) {
		this.buyerEmailGiftCard = buyerEmailGiftCard;
	}

	public String getUserEmailGiftCard() {
		return userEmailGiftCard;
	}

	public void setUserEmailGiftCard(String userEmailGiftCard) {
		this.userEmailGiftCard = userEmailGiftCard;
	}

	public long getDurationGiftCard() {
		return durationGiftCard;
	}

	public void setDurationGiftCard(long durationGiftCard) {
		this.durationGiftCard = durationGiftCard;
	}
}
