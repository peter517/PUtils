package com.pengjun.android.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.util.Log;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class AdLoggerUtils {

	public static void initLogger(String logPath, boolean useLogCatAppender,
			boolean useFileAppender) {

		if (AdResourceUtils.hasExternalStorage()) {
			LogConfigurator logConfigurator = new LogConfigurator();

			logConfigurator.setFileName(logPath);
			logConfigurator.setRootLevel(Level.DEBUG);
			logConfigurator.setLevel("org.apache", Level.ERROR);
			logConfigurator.setFilePattern("%d %-5p [%c{2}] %m [%F:%L] %n");
			logConfigurator.setMaxFileSize(1024 * 1024 * 5);
			logConfigurator.setImmediateFlush(true);

			logConfigurator.setLogCatPattern("%m  [%F:%L:mmpc] %n");
			logConfigurator.setUseLogCatAppender(useLogCatAppender);
			logConfigurator.setUseFileAppender(useFileAppender);
			logConfigurator.configure();
			Logger logger = Logger.getLogger("log");
			logger.info("logger start");
		}

	}

	public static void printException(Logger logger, Exception e) {
		if (e == null || logger == null)
			return;
		StackTraceElement[] arrTrace = e.getStackTrace();
		if (e.getCause() != null)
			logger.error("cause:" + e.getCause());
		if (e.getMessage() != null)
			logger.error("cause:" + e.getMessage());
		for (StackTraceElement trace : arrTrace) {
			logger.error("\t" + trace);
		}
	}

	public static Logger getCameraLogger(String tag) {
		return Logger.getLogger(tag);
	}

	public static void printFromPJ(String info) {
		Log.i("pj", info);
	}

	public static void printFromPJ(String tag, String info) {
		Logger.getLogger(tag).info(info);
	}

}
