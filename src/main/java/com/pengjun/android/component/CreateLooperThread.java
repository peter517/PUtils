package com.pengjun.android.component;

import android.os.Looper;

public class CreateLooperThread extends Thread {
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

	public void quit() {
		if (myLooper != null) {
			myLooper.quit();
		}
	}
}
