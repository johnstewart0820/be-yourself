package fr.be.your.self.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StatusResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7428596919835283969L;

	private boolean success;

	private String code;
	private String message;
	private Map<String, Serializable> data;

	public StatusResponse() {
		super();
	}

	public StatusResponse(boolean success) {
		this();
		
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Serializable> getData() {
		return data;
	}

	public void setData(Map<String, Serializable> data) {
		this.data = data;
	}
	
	public void addData(String key, Serializable value) {
		if (key == null || key.isEmpty()) {
			return;
		}
		
		if (value == null) {
			if (this.data != null) {
				this.data.remove(key);
			}
			
			return;
		}
		
		if (this.data == null) {
			this.data = new HashMap<String, Serializable>();
		}
		
		this.data.put(key, value);
	}
}
