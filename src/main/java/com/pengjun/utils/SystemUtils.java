package com.pengjun.utils;

import java.util.ArrayList;
import java.util.List;

public class SystemUtils {

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
}
