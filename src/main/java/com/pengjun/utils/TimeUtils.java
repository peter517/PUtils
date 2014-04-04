package com.pengjun.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class TimeUtils {

	public static final String TIME_STRING_FORMT = "%d-%02d-%02d-%02d-%02d-%02d";
	public static final String DATE_STRING_FORMT = "%d-%02d-%02d";
	public static final String yyyyMMdd_FORMT = "yyyy-MM-dd";
	public static final String yyyyMM_FORMT = "yyyy-MM";
	public static final String yyyy_FORMT = "yyyy";
	public static final String HH_FORMT = "HH";
	public static final String TIME_SEPARATOR = "-";

	private static DateFormat yyyyMMddFormat = new SimpleDateFormat(
			yyyyMMdd_FORMT);
	private static DateFormat yyyyMMFormat = new SimpleDateFormat(yyyyMM_FORMT);
	private static DateFormat yyyyFormat = new SimpleDateFormat(yyyy_FORMT);
	private static DateFormat HHFormat = new SimpleDateFormat(HH_FORMT);

	private final static Calendar calendar = Calendar.getInstance();

	private static long time = System.currentTimeMillis();

	private static Random random = new Random();
	static {
		random.setSeed(System.currentTimeMillis());
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
	}

	public static void startTiming() {
		time = System.currentTimeMillis();
	}

	public static String stopTiming() {
		return String
				.valueOf((System.currentTimeMillis() - time) / 1000f + "秒");
	}

	public static Date string2yyyyMMddDate(String dateStr) {
		try {
			return yyyyMMddFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurHour() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return HHFormat.format(calendar.getTime());
	}

	public static int getDayOfWeek(long currentTimeMillis) {
		calendar.setTimeInMillis(currentTimeMillis);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static String getLastMonthOfTodayStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, -1);
		String str = yyyyMMddFormat.format(calendar.getTime());
		calendar.add(Calendar.MONTH, 1);
		return str;
	}

	public static String getLastWeekOfTodayStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		String str = yyyyMMddFormat.format(calendar.getTime());
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		return str;
	}

	public static String getLastDayStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		String str = yyyyMMddFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return str;
	}

	public static Date getCurDate() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return calendar.getTime();
	}

	public static String getLastMonthStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MONTH, -1);
		String str = yyyyMMFormat.format(calendar.getTime());
		calendar.add(Calendar.MONTH, 1);
		return str;
	}

	public static Date string2yyyyMMDate(String dateStr) {
		try {
			return yyyyMMFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDateStr(String formatStr) {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return new SimpleDateFormat(formatStr).format(calendar.getTime());
	}

	public static Date string2yyyyDate(String dateStr) {
		try {
			return yyyyFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurTimeStr() {

		calendar.setTimeInMillis(System.currentTimeMillis());
		int year = calendar.get(Calendar.YEAR);
		int mouth = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		String curTimeStr = String.format(TIME_STRING_FORMT, year, mouth + 1,
				day, hour, minute, second);

		return curTimeStr;
	}

	public static String getCurMonthYearStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return yyyyMMFormat.format(calendar.getTime());
	}

	public static String getCurDateStr() {

		calendar.setTimeInMillis(System.currentTimeMillis());
		int year = calendar.get(Calendar.YEAR);
		int mouth = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		String curTimeStr = String.format(DATE_STRING_FORMT, year, mouth + 1,
				day);

		return curTimeStr;
	}

	public static String getCurYearStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return String.valueOf(calendar.get(Calendar.YEAR));
	}

	public static String getCurWeekYearStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return String.valueOf(calendar.get(Calendar.YEAR) + " "
				+ calendar.get(Calendar.WEEK_OF_YEAR));
	}

	public static Date getDateFromLong(long time) {
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}

	public static String getCurMonthStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return String.valueOf(calendar.get(Calendar.MONTH) + 1);
	}

	public static String getCurDayStr() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static String getRandomDateStr() {
		return String.format(DATE_STRING_FORMT, 2000 + random.nextInt(13),
				1 + random.nextInt(12), 1 + random.nextInt(30));
	}

	public static float getRandomFloat() {
		return random.nextFloat();
	}

	public static int getRandomInt(int max) {
		return random.nextInt(max);
	}

	public static String getRandomTimeStr() {

		return String.format(TIME_STRING_FORMT, 2000 + random.nextInt(13),
				1 + random.nextInt(12), 1 + random.nextInt(30),
				1 + random.nextInt(24), 1 + random.nextInt(60),
				1 + random.nextInt(60));
	}

	public static String[] String2DateStrArr(String dateStr) {
		String[] date = dateStr.split(TIME_SEPARATOR);
		return date;
	}

	public static String String2MonthYearStr(String dateStr) {
		String[] date = String2DateStrArr(dateStr);
		return date[0] + "-" + date[1];
	}

	public static String String2YearStr(String dateStr) {
		String[] date = String2DateStrArr(dateStr);
		return date[0];
	}

	public static String String2DayMonthStr(String dateStr) {
		String[] date = String2DateStrArr(dateStr);
		return date[1] + "-" + date[2];
	}

	public static String getTimeStrFromLongTime(long time) {

		if (time >= 360000000) {
			return "00:00:00";
		}

		String timeCount = "";
		long hourc = time / 3600000;
		String hour = "0" + hourc;
		hour = hour.substring(hour.length() - 2, hour.length());

		long minuec = (time - hourc * 3600000) / (60000);
		String minue = "0" + minuec;
		minue = minue.substring(minue.length() - 2, minue.length());

		long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
		String sec = "0" + secc;
		sec = sec.substring(sec.length() - 2, sec.length());
		timeCount = hour + ":" + minue + ":" + sec;
		return timeCount;
	}

}
