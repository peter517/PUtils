package com.pengjun.android.component;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ThreadHandler extends Handler {

	private ThreadHandler(Looper looper) {
		super(looper);
	}

	public static ThreadHandler newInstance() {
		MessageThread thread = new MessageThread();
		thread.start();
		try {
			thread.waitForMyLooper();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new ThreadHandler(thread.myLooper());
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		}
	}
}

class MessageThread extends Thread {

	private volatile Looper myLooper = null;

	public Looper myLooper() {
		return myLooper;
	}

	public Looper waitForMyLooper() throws InterruptedException {
		if (myLooper == null) {
			synchronized (this) {
				if (myLooper == null) {
					this.wait();
				}
			}
		}
		return myLooper;
	}

	@Override
	public void run() {
		Looper.prepare();
		synchronized (this) {
			myLooper = Looper.myLooper();
			this.notify();
		}
		Looper.loop();
	}
}
