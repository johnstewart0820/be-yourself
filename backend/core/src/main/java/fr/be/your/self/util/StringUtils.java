package fr.be.your.self.util;

import java.math.BigDecimal;

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
}
