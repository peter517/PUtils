package com.pengjun.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtils {

	static {
		PropertyConfigurator.configure("log4j.properties");
	}

	public static Logger getLogger(String tag) {
		return Logger.getLogger(tag);
	}

}
