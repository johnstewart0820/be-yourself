package fr.be.your.self.util;

import java.math.BigDecimal;
import java.util.Random;

public class StringUtils {
	
	private static final int FILE_SIZE_BLOCK = 1024;
	private static final String[] FILE_SIZE_TYPES = new String[] {"Bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
	
	public static final boolean isNullOrEmpty(String value) {
		return value == null || value.isEmpty();
	}
	
	public static final boolean isNullOrSpace(String value) {
		return value == null || value.trim().isEmpty();
	}
	
	public static final String formatFileSize(long value, int decimals) {
	    if (value == 0) return "0 Bytes";
	    
	    final int dm = decimals < 0 ? 0 : decimals;
	    final int index = new Double(Math.floor(Math.log(value) / Math.log(FILE_SIZE_BLOCK))).intValue();
	    
	    return new BigDecimal(value / Math.pow(FILE_SIZE_BLOCK, index)).setScale(dm, BigDecimal.ROUND_HALF_UP).toString() + " " + FILE_SIZE_TYPES[index];
	}
	
	public static final String formatFileSize(long value) {
		return formatFileSize(value, 0);
	}
	
	public static final String upperCaseFirst(String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}
	
	public static final String randomAlphabetic(int length) {
		final int leftLimit = 97; // letter 'a'
		final int rightLimit = 122; // letter 'z'
		final Random random = new Random();
	 
	    return random.ints(leftLimit, rightLimit + 1)
	      .limit(length)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	}
	
	public static final String randomAlphanumeric(int length) {
		final int leftLimit = 48; // numeral '0'
		final int rightLimit = 122; // letter 'z'
		final Random random = new Random();
	 
	    return random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(length)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	}
}
