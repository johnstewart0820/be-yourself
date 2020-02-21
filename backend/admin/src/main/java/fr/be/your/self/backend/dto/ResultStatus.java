package fr.be.your.self.backend.dto;

public enum ResultStatus {
	ERROR(0),
	SUCCESS(1),
	UNKNOWN(-1);
	
	
	private final int value;
	
	ResultStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	

	public static final ResultStatus parse(int value) {
		final ResultStatus[] values = ResultStatus.values();
		
		for (int i = 0; i < values.length; i++) {
			if (values[i].value == value) {
				return values[i];
			}
		}
		
		return UNKNOWN;
	}
	
}
