package fr.be.your.self.backend.dto;

import java.io.Serializable;

import fr.be.your.self.model.PO;

public class BaseDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6609266907438836586L;

	private long createdTime;
	private long updatedTime;

	public BaseDto() {
		super();
	}

	public BaseDto(PO<?> domain) {
		this();
		
		if (domain != null) {
			this.createdTime = domain.getCreated() == null ? 0 : domain.getCreated().getTime();
			this.updatedTime = domain.getUpdated() == null ? 0 : domain.getUpdated().getTime();
		}
	}
	
	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}
}
