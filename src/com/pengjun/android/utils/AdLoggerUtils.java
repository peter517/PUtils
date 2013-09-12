package com.pengjun.android.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AdLoggerUtils {

	public static void initLogger(String logPath, boolean useLogCatAppender, boolean useFileAppender) {

		LogConfigurator logConfigurator = new LogConfigurator();
		logConfigurator.setFileName(logPath);
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);

		logConfigurator.setUseLogCatAppender(useLogCatAppender);
		logConfigurator.setUseFileAppender(useFileAppender);
		logConfigurator.configure();
		Logger logger = Logger.getLogger("log");
		logger.info("logger start");
	}

}
