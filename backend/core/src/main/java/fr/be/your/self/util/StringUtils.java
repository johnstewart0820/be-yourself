package fr.be.your.self.util;

public class StringUtils {
	
	public static final boolean isNullOrEmpty(String value) {
		return value == null || value.isEmpty();
	}
	
	public static final boolean isNullOrSpace(String value) {
		return value == null || value.trim().isEmpty();
	}
}
