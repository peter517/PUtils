package com.pengjun.android.utils;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.os.Environment;
import android.util.Log;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AdLoggerUtils {

	public static final String milestonePrefix = "milestone: ";
	private static String exceptionTag = "appName_exception";

	public static void initLogger(String appName, boolean useLogCatAppender,
			boolean useFileAppender) {

		String logPath = Environment.getExternalStorageDirectory()
				+ File.separator + appName + File.separator + "logs"
				+ File.separator + appName + ".log";
		exceptionTag = appName + "_exception";

		LogConfigurator logConfigurator = new LogConfigurator();

		logConfigurator.setFileName(logPath);
		logConfigurator.setRootLevel(Level.DEBUG);
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.setFilePattern("%d %-5p [%c{2}] %m [%F:%L] %n");
		logConfigurator.setMaxFileSize(1024 * 1024 * 5);
		logConfigurator.setImmediateFlush(true);

		logConfigurator.setLogCatPattern("%m  [%F:%L: " + appName + "] %n");
		logConfigurator.setUseLogCatAppender(useLogCatAppender);
		if (AdResourceUtils.hasExternalStorage()) {
			logConfigurator.setUseFileAppender(useFileAppender);
		}
		try {
			logConfigurator.configure();
		} catch (Exception e) {
			// exception when sdcard could not be written
			logConfigurator.setUseFileAppender(false);
			logConfigurator.configure();
			printException(Logger.getLogger(appName), e);
		}
		Logger logger = Logger.getLogger(appName);
		logger.info("-----------------logger start-----------------");

	}

	public static void printException(Logger logger, Throwable throwable) {
		if (throwable == null || logger == null)
			return;
		logger.debug(exceptionTag, throwable);
	}

	public static void printFromTag(String tag, String info) {
		Logger.getLogger(tag).debug(info);
	}

	public static Logger getLogger(String name) {
		return Logger.getLogger(name);
	}

	public static void markMilestoneLog(Logger logger, String str) {
		logger.debug(milestonePrefix + str);
	}

	public static void debug(String info) {
		Log.d("pj", info);
	}

}
