package fr.be.your.self.util;

import java.math.BigDecimal;

public class NumberUtils {
	
	public static final Integer parseInteger(Object value, Integer defaultValue) {
		if (value == null) {
    		return defaultValue;
    	}
    	
    	if (value instanceof Integer) {
    		return (Integer) value;
    	}
    	
    	if (value instanceof Double) {
    		return ((Double) value).intValue();
    	}
    	
    	if (value instanceof Float) {
    		return ((Float) value).intValue();
    	}
    	
    	if (value instanceof Long) {
    		return ((Long) value).intValue();
    	}
    	
    	try {
    		return new Integer(value.toString());
    	} catch (Exception ex) {}
    	
    	return defaultValue;
	}
	
	public static final Integer parseInteger(Object value) {
		return parseInteger(value, null);
	}
	
	public static final int parseInt(Object value, int defaultValue) {
    	return parseInteger(value, defaultValue);
	}
	
	public static final int parseInt(Object value) {
    	return parseInteger(value, 0);
	}
	
	public static final Long parseLong(Object value, Long defaultValue) {
		if (value == null) {
    		return defaultValue;
    	}
    	
    	if (value instanceof Integer) {
    		return ((Integer) value).longValue();
    	}
    	
    	if (value instanceof Long) {
    		return (Long) value;
    	}
    	
    	if (value instanceof Double) {
    		return ((Double) value).longValue();
    	}
    	
    	if (value instanceof Float) {
    		return ((Float) value).longValue();
    	}
    	
    	try {
    		return new Long(value.toString());
    	} catch (Exception ex) {}
    	
    	return defaultValue;
	}
	
	public static final long parseLong(Object value, long defaultValue) {
    	return parseLong(value, defaultValue);
	}
	
	public static final long parseLong(Object value) {
    	return parseLong(value, 0);
	}
	
	public static final BigDecimal parseNumber(Object value, BigDecimal defaultValue) {
		if (value == null) {
    		return defaultValue;
    	}
    	
    	if (value instanceof BigDecimal) {
    		return (BigDecimal) value;
    	}
    	
    	if (value instanceof Double) {
    		return new BigDecimal((Double) value);
    	}
    	
    	if (value instanceof Float) {
    		return new BigDecimal((Float) value);
    	}
    	
    	if (value instanceof Long) {
    		return new BigDecimal((Long) value);
    	}
    	
    	if (value instanceof Integer) {
    		return new BigDecimal((Integer) value);
    	}
    	
    	try {
    		return new BigDecimal(value.toString());
    	} catch (Exception ex) {}
    	
    	return defaultValue;
	}
	
	public static final BigDecimal parseNumber(Object value) {
		return parseNumber(value, null);
	}
}
