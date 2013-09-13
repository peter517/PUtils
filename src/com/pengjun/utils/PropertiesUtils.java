package com.pengjun.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

	public final static String FILENAME = "setting.properties";
	private static Properties props = new Properties();
	static {
		try {
			InputStream in = new FileInputStream(FILENAME);
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getInteger(String key, int defaultValue) {
		String value = (String) props.get(key);
		return value == null ? defaultValue : Integer.valueOf(value);
	}

	public static String getString(String key, String defaultValue) {
		String value = (String) props.get(key);
		return value == null ? defaultValue : value;
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = (String) props.get(key);
		return value == null ? defaultValue : Boolean.valueOf(value);
	}

}
