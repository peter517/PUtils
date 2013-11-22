package com.pengjun.android.loadresource.cache;

import android.os.AsyncTask;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;

public final class ConcurrentLruDiskCacheProxy extends ConcurrentLruDiskCache
		implements IdleHandler {

	private volatile boolean isAdded = false;

	private ExpireAsyncTask expireAsyncTask = null;

	public ConcurrentLruDiskCacheProxy(int maximumCacheSize) {
		super(maximumCacheSize);
	}

	@Override
	protected long expire() {
		if (!isAdded) {
			isAdded = true;
			Looper.myQueue().addIdleHandler(this);
		}
		return currentCacheSize.get();
	}

	@Override
	public boolean queueIdle() {
		if (expireAsyncTask == null) {
			expireAsyncTask = new ExpireAsyncTask();
			expireAsyncTask.execute();
		}
		return false;
	}

	public void expireAsync() {
		currentCacheSize.set(super.expire());
	}

	private void onFinished() {
		expireAsyncTask = null;
		isAdded = false;
	}

	private class ExpireAsyncTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... params) {
			ConcurrentLruDiskCacheProxy.this.expireAsync();
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			ConcurrentLruDiskCacheProxy.this.onFinished();
		}
	}
}
