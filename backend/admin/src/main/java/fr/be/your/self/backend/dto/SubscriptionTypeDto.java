package fr.be.your.self.backend.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import fr.be.your.self.model.SessionGroup;
import fr.be.your.self.model.SubscriptionType;

public class SubscriptionTypeDto  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1595202474529418132L;
	/**
	 * 
	 */
	private int id;
	private String name;
	private String description;

	private BigDecimal price;
	private String canal;
	private int duration; //month
	private boolean status;
	private boolean autoRenew;
	
	public SubscriptionTypeDto(SubscriptionType domain) {
		this();
		
		this.status = false;
		this.autoRenew = false;
		
		if (domain != null) {
			this.id = domain.getId().intValue();
			this.name = domain.getName();
			this.description = domain.getDescription();
			this.price = domain.getPrice();
			this.canal = domain.getCanal();
			this.duration = domain.getDuration();
			this.status = domain.isStatus();
			this.autoRenew = domain.isAutoRenew();
		}
	}

	public SubscriptionTypeDto() {
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public boolean isAutoRenew() {
		return autoRenew;
	}
	public void setAutoRenew(boolean autoRenew) {
		this.autoRenew = autoRenew;
	}
	public void copyToDomain(SubscriptionType domain) {
		domain.setCanal(this.canal);
		domain.setAutoRenew(this.autoRenew);
		domain.setDescription(this.description);
		domain.setDuration(this.duration);
		domain.setDuration(this.duration);
		domain.setName(this.name);
		domain.setPrice(this.price);
		domain.setStatus(this.status);
		
	}

}
