package com.pengjun.android.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.provider.Settings;
import android.provider.Settings.Secure;

import com.pengjun.utils.StringUtils;

public class AdResourceUtils {

	public static int[] COLOR_ARR = new int[] { Color.BLUE, Color.MAGENTA,
			Color.DKGRAY, Color.CYAN, Color.GREEN, Color.GRAY, Color.RED,
			Color.WHITE, Color.LTGRAY };
	// res
	private static List<String> listSystemBuildProperty = new ArrayList<String>();
	public static final int SINGLE_APP_MEMORY_LIMIT_32 = 32;

	public static void setStrictModeOn() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectNetwork().penaltyLog().penaltyDialog()
				.permitDiskWrites().permitDiskReads().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
				.penaltyLog().build());
	}

	public static synchronized List<String> getSystemBuildProperties() {
		if (!listSystemBuildProperty.isEmpty())
			return listSystemBuildProperty;

		String fileName = "/system/build.prop";
		String line;
		BufferedReader localBufferedReader = null;
		try {
			localBufferedReader = new BufferedReader(new FileReader(fileName),
					8192);
			line = localBufferedReader.readLine();
			while (line != null) {
				line = line.trim().replace("\n\r", "");
				listSystemBuildProperty.add(line);
				line = localBufferedReader.readLine();
			}
		} catch (IOException e) {
		} finally {
			if (localBufferedReader != null)
				try {
					localBufferedReader.close();
				} catch (IOException e) {
				}
		}

		return listSystemBuildProperty;
	}

	public static String parseSystemBuildProperty(String field) {
		if (field == null)
			return "";
		List<String> listProperty = getSystemBuildProperties();
		for (String property : listProperty) {
			if (property.startsWith(field)) {
				String[] arr = property.split("=");
				if (arr.length > 1)
					return arr[1].trim();
			}
		}

		return "";
	}

	// use SharedPreferences to check first install
	public final static String SP_FIRST_START = "firstStart";

	public static String getSharedPreferencesString(Context context, String key) {
		return context.getSharedPreferences(SP_FIRST_START, 0).getString(key,
				"");
	}

	public static void putSharedPreferencesString(Context context, String key,
			String value) {
		context.getSharedPreferences(SP_FIRST_START, 0).edit()
				.putString(key, value).commit();
	}

	public static boolean isServiceRunning(Context context, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);

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

	public static boolean checkNetwork(Context context) {
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

		StatFs stat = new StatFs(Environment.getDataDirectory()
				.getAbsolutePath());
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
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ip = inetAddress.getHostAddress().toString();
						if (ip.matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*")) {
							localIPs = ip;
							break;
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return localIPs;
	}

	public static String getBase64StrFromIp(String localIp) {
		if (localIp.equals("")) {
			return "";
		}

		String[] ipArr = localIp.split("\\.");
		// base three ip block
		int ipTwoBlock = Integer.valueOf(ipArr[1]);
		int ipThreeBlock = Integer.valueOf(ipArr[2]);
		int ipFourBlock = Integer.valueOf(ipArr[3]);

		return StringUtils.encodeBase64(new byte[] { (byte) (ipTwoBlock),
				(byte) (ipThreeBlock), (byte) (ipFourBlock) });
	}

	public static String getIpFromBase64Byte(byte[] ipByteArr) {

		int[] ipFormatArr = new int[ipByteArr.length];
		int i = 0;
		for (byte ipBlock : ipByteArr) {
			if (ipBlock < 0) {
				ipFormatArr[i++] = ipBlock + 256;
			} else {
				ipFormatArr[i++] = ipBlock;
			}
		}
		return String.format("%d.%d.%d", ipFormatArr[0], ipFormatArr[1],
				ipFormatArr[2]);
	}

	public static String getAndroidId(Context context) {
		String androidId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return androidId;
	}

	public static String getDisplayName(Context context) {

		String displayName = null;
		try {
			displayName = Settings.System.getString(
					context.getContentResolver(), "device_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (displayName == null || displayName.length() == 0) {
			displayName = "未命名设备";
		}
		return displayName;
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

	public static void setSpeakerVolume(Context context, int level) {
		if (context == null) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (audioManager != null) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
		}
	}

	public static int getCurrnetSpeakerVolume(Context context) {

		if (context == null) {
			return -1;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public static int getMaxSpeakerVolume(Context context) {

		if (context == null) {
			return -1;
		}

		int level = -1;
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (audioManager != null) {
			level = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		return level;
	}

}
