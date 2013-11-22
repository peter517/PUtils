package com.pengjun.android.loadresource.cache;

import android.os.Looper;
import android.os.MessageQueue.IdleHandler;

public final class LruDiskCacheProxy extends LruDiskCache implements
		IdleHandler {

	private boolean isAdded = false;

	public LruDiskCacheProxy(int maximumCacheSize) {
		super(maximumCacheSize);
	}

	protected long expire() {
		if (!isAdded) {
			isAdded = true;
			Looper.myQueue().addIdleHandler(this);
		}
		return currentCacheSize;
	}

	@Override
	public boolean queueIdle() {
		currentCacheSize = super.expire();
		isAdded = false;
		return false;
	}
}
