package fr.be.your.self.backend.dto;

public class SimpleResult {
	private String functionalityName;
	private int resStatus; //See ResultStatus
	private String message;
	
	public SimpleResult() {
		
	}
	public SimpleResult(int resStatus, String message) {
		super();
		this.setResStatus(resStatus);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getResStatus() {
		return resStatus;
	}
	public void setResStatus(int resStatus) {
		this.resStatus = resStatus;
	}
	public String getFunctionalityName() {
		return functionalityName;
	}
	public void setFunctionalityName(String functionalityName) {
		this.functionalityName = functionalityName;
	}
	
	
}
