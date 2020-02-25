package fr.be.your.self.backend.dto;

import java.math.BigDecimal;
import java.util.Date;

import fr.be.your.self.common.BusinessCodeStatus;
import fr.be.your.self.common.BusinessCodeType;
import fr.be.your.self.model.BusinessCode;

public class BusinessCodeDto extends BaseDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8615171831998913884L;

	private String code;

	/**
	 * {@link BusinessCodeType#getValue()}
	 **/
	private int codeType;

	/**
	 * {@link BusinessCodeStatus#getValue()}
	 **/
	private int status;

	private Date startDate;

	private Date endDate;

	/**
	 * Beneficiary (company) for B2B
	 **/
	private String beneficiary; // company

	/**
	 * Maximum users amount for B2B
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
	 * Price paid per user (recalculated if the price of the deal or the maximum
	 * amount of users is changed)
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

	public BusinessCodeDto() {
		super();
		
		this.pricePerUser = BigDecimal.ZERO;
	}

	public BusinessCodeDto(BusinessCode domain, int priceScale) {
		super(domain);
		
		this.pricePerUser = BigDecimal.ZERO;
		
		if (domain != null) {
			this.code = domain.getCode();
			this.codeType = domain.getCodeType();
			this.status = domain.getStatus();
			this.startDate = domain.getStartDate();
			this.endDate = domain.getEndDate();
			this.beneficiary = domain.getBeneficiary();
			this.maxUserCount = domain.getMaxUserCount();
			this.usedTimes = domain.getUsedTimes();
			this.dealPrice = domain.getDealPrice();
			this.maxUsageCount = domain.getMaxUsageCount();
			this.discountPercentage = domain.getDiscountPercentage();
			this.priceGiftCard = domain.getPriceGiftCard();
			this.buyerEmailGiftCard = domain.getBuyerEmailGiftCard();
			this.userEmailGiftCard = domain.getUserEmailGiftCard();
			this.durationGiftCard = domain.getDurationGiftCard();
			
			if (this.dealPrice != null) {
				if (priceScale < 0) { 
					this.pricePerUser = this.dealPrice.divide(new BigDecimal(this.maxUserCount), BigDecimal.ROUND_HALF_UP);
				} else {
					this.pricePerUser = this.dealPrice.divide(new BigDecimal(this.maxUserCount), priceScale, BigDecimal.ROUND_HALF_UP);
				}
			}
		}
	}

	public void copyToDomain(BusinessCode domain) {
		domain.setCode(code);
		domain.setCodeType(codeType);
		domain.setStatus(status);
		domain.setStartDate(startDate);
		domain.setEndDate(endDate);
		domain.setBeneficiary(beneficiary);
		domain.setMaxUserCount(maxUserCount);
		domain.setUsedTimes(usedTimes);
		domain.setDealPrice(dealPrice);
		domain.setMaxUsageCount(maxUsageCount);
		domain.setDiscountPercentage(discountPercentage);
		domain.setPriceGiftCard(priceGiftCard);
		domain.setBuyerEmailGiftCard(buyerEmailGiftCard);
		domain.setUserEmailGiftCard(userEmailGiftCard);
		domain.setDurationGiftCard(durationGiftCard);
	}
	
	public String getId() {
		return code;
	}

	public void setId(String id) {
		this.code = id;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
