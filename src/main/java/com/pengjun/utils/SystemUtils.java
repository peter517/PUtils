package com.pengjun.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

public class SystemUtils {

	private final static int PROCESS_READ_BUF = 8 * 1024;

	public static List<StringBuffer> getCurThreadListInfo() {
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		while (group.getParent() != null) {
			group = group.getParent();
		}
		Thread[] threads = new Thread[group.activeCount()];
		group.enumerate(threads);

		List<StringBuffer> sbList = new ArrayList<StringBuffer>();
		for (Thread thread : threads) {
			if (thread == null) {
				continue;
			}
			try {
				StringBuffer buf = new StringBuffer();
				ThreadGroup tgroup = thread.getThreadGroup();
				String groupName = tgroup == null ? "null" : tgroup.getName();
				buf.append("ThreadGroup:").append(groupName).append(", ");
				buf.append("Id:").append(thread.getId()).append(", ");
				buf.append("Name:").append(thread.getName()).append(", ");
				buf.append("isDaemon:").append(thread.isDaemon()).append(", ");
				buf.append("isAlive:").append(thread.isAlive()).append(", ");
				buf.append("Priority:").append(thread.getPriority());
				sbList.add(buf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sbList;
	}

	public static List<String> runShellCommand(String cmd) throws IOException {
		Process process = Runtime.getRuntime().exec(cmd);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()),
				PROCESS_READ_BUF);
		List<String> resultList = new ArrayList<String>();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			resultList.add(line);
		}

		return resultList;
	}

	public static List<String> runShellCommand(String cmd, String keyword)
			throws IOException {
		Process process = Runtime.getRuntime().exec(cmd);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()),
				PROCESS_READ_BUF);
		List<String> resultList = new ArrayList<String>();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.contains(keyword)) {
				resultList.add(line);
			}
		}

		return resultList;
	}

	/**
	 * 计算已使用内存的百分比，并返回。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 已使用内存的百分比，以字符串形式返回。
	 */
	public static int getMemoryUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
					"\\D+", ""));
			long availableSize = getAvailableMemory(context) / 1024;
			int percent = (int) ((totalMemorySize - availableSize)
					/ (float) totalMemorySize * 100);
			return percent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 当前可用内存。
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		return mi.availMem;
	}
}
