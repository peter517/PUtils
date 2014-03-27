package com.pengjun.android.utils;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AdLoggerUtils {

	public static void initLogger(String projectName,
			boolean useLogCatAppender, boolean useFileAppender) {

		String logPath = Environment.getExternalStorageDirectory()
				+ File.separator + projectName + File.separator + "logs"
				+ File.separator + projectName + ".log";

		LogConfigurator logConfigurator = new LogConfigurator();

		logConfigurator.setFileName(logPath);
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%d %-5p [%c{2}] %m [%F:%L] %n");
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);

		logConfigurator.setLogCatPattern("%m  [%F:%L: " + projectName + "] %n");
		logConfigurator.setUseLogCatAppender(useLogCatAppender);
		if (AdResourceUtils.hasExternalStorage()) {
			logConfigurator.setUseFileAppender(useFileAppender);
		}
		logConfigurator.configure();
		Logger logger = Logger.getLogger(projectName);
		logger.info("-----------------logger start-----------------");

	}

	public static void printException(Logger logger, Exception e) {
		if (e == null || logger == null)
			return;
		StackTraceElement[] arrTrace = e.getStackTrace();
		if (e.getCause() != null)
			logger.error(e.getClass().getName() + " : " + e.getCause());
		if (e.getMessage() != null)
			logger.error(e.getClass().getName() + " : " + e.getMessage());
		for (StackTraceElement trace : arrTrace) {
			logger.error("\t" + trace);
		}
	}

	public static void printFromTag(String tag, String info) {
		Logger.getLogger(tag).debug(info);
	}

	public static Logger getLogger(String name) {
		return Logger.getLogger(name);
	}

}
