package com.pengjun.android.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import com.pengjun.utils.StringUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

public class AdResourceUtils {

	public static final String IS_DEBUG = "debug";

	public static final int SINGLE_APP_MEMORY_LIMIT_32 = 32;

	public static int[] COLOR_ARR = new int[] { Color.BLUE, Color.MAGENTA, Color.DKGRAY, Color.CYAN,
			Color.GREEN, Color.GRAY, Color.RED, Color.WHITE, Color.LTGRAY };
	// res

	// use SharedPreferences to check first install
	public final static String SP_FIRST_START = "firstStart";

	public static String getSharedPreferencesString(Context context, String key) {
		return context.getSharedPreferences(SP_FIRST_START, 0).getString(key, StringUtils.NULL_STRING);
	}

	public static void putSharedPreferencesString(Context context, String key, String value) {
		context.getSharedPreferences(SP_FIRST_START, 0).edit().putString(key, value).commit();
	}

	public static boolean isServiceRunning(Context context, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}

		return isRunning;
	}

	public static boolean checkNetwork(Context context, boolean isNotify) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetInfo != null) {
			int netType = activeNetInfo.getType();
			switch (netType) {
			case 0:
			case 1:
			case 9:
			case 6:
			case 7:
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	public static boolean checkStorageSpace() {

		StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		if (blockSize * availableBlocks / 1024 / 1024 >= 128) {
			return true;
		}

		return false;
	}

	public static String getLocalIpAddress() {
		String localIPs = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						localIPs = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}
		return localIPs;
	}

	public static boolean hasExternalStorage() {
		String state = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static int getSingleAppMemeryLimit(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager.getMemoryClass();
	}

	public static long getSDCardIdleSpace() {
		if (!hasExternalStorage())
			return 0;

		return getPathSpace(getSDCardpath());
	}

	public static long getPathSpace(String path) {
		StatFs statFs = new StatFs(path);
		return statFs.getBlockSize() * (long) statFs.getAvailableBlocks();
	}

	public static String getSDCardpath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}