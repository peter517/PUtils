/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package com.pengjun.utils;

/**
 * 
 * @author wb-jiangjm
 * 
 * @version $Id: ValueUtil.java, v 0.1 2010-8-12 ����03:41:21 wb-jiangjm Exp $
 */
public final class ValueUtils {

	public static int convertToInt(Object value) {
		try {
			return Integer.valueOf(value.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public static int convertToInt(Object value, int defaultValue) {
		try {
			return Integer.valueOf(value.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long convertToLong(Object value) {
		try {
			return Long.valueOf(value.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public static long convertToLong(Object value, long defaultValue) {
		try {
			return Long.valueOf(value.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float convertToFloat(Object value) {
		try {
			return Float.valueOf(value.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public static float convertToFloat(Object value, float defaultValue) {
		try {
			return Float.valueOf(value.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double convertToDouble(Object value) {
		try {
			return Double.valueOf(value.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public static double convertToDouble(Object value, double defaultValue) {
		try {
			return Double.valueOf(value.toString());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static boolean convertToBoolean(Object value) {
		try {
			String strValue = StringUtils.trimTailSpaces(value.toString());
			if (StringUtils.isEmpty(strValue)) {
				return false;
			}
			if ("true".equalsIgnoreCase(strValue)) {
				return true;
			}
			if (Integer.valueOf(strValue) != 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
