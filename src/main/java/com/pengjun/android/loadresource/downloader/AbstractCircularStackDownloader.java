package com.pengjun.android.loadresource.downloader;

import android.util.Log;

import com.pengjun.android.loadresource.CircularStack;
import com.pengjun.android.loadresource.LoadResourceManager;

public abstract class AbstractCircularStackDownloader implements Downloader,
		Runnable {
	private CircularStack<Request> tasks = null;
	private volatile boolean running = true;
	private Request currentRequest = null;

	private ReplyListener replyListener = null;

	public void setReplyListener(ReplyListener l) {
		replyListener = l;
	}

	final protected void notifyProgress(Request request, long bytesReceived,
			long bytesTotal) {
		if (replyListener != null) {
			replyListener
					.onDownloadProgress(request, bytesReceived, bytesTotal);
		}
	}

	final protected void notifyFinished(Request request) {
		if (replyListener != null) {
			replyListener.onDownloadFinished(request);
		}
	}

	/**
     * 
     * 
     */
	public AbstractCircularStackDownloader(int capacity) {
		tasks = new CircularStack<Request>(capacity);
	}

	/**
     * 
     * 
     */
	@Override
	public void get(Request request) {
		Request discardedRequest = null;

		synchronized (tasks) {

			discardedRequest = tasks.push(request);
			tasks.notifyAll();
		}

		if (discardedRequest != null) {
			Log.v(LoadResourceManager.TAG, "discard: " + discardedRequest.urn);
			discardedRequest.error = Error.OperationCanceled;
			notifyFinished(discardedRequest);
		}
	}

	/**
     * 
     * 
     */
	@Override
	public void abort(String urn) {
		synchronized (tasks) {
			if (currentRequest != null && currentRequest.urn.equals(urn)) {
				currentRequest.abort = true;
			}

			final int capacity = tasks.getCapacity();
			for (int i = 0; i < capacity; ++i) {
				Request request = tasks.get(i);
				if (request != null && request.urn.equals(urn)) {
					request.abort = true;
				}
			}
		}
	}

	/**
     * 
     * 
     */
	@Override
	public void abortAll() {
		synchronized (tasks) {
			if (currentRequest != null) {
				currentRequest.abort = true;
			}

			final int capacity = tasks.getCapacity();
			for (int i = 0; i < capacity; ++i) {
				Request request = tasks.get(i);
				if (request != null) {
					request.abort = true;
				}
			}
		}
	}

	/**
     * 
     * 
     */
	public boolean isRunning() {
		return running;
	}

	/**
     * 
     * 
     */
	public void start() {
		running = true;
	}

	/**
     * 
     * 
     */
	@Override
	public void stop() {
		running = false;

		synchronized (tasks) {
			tasks.notifyAll();
		}
	}

	/**
     * 
     * 
     */
	public void setCapacity(int capacity) {
		synchronized (tasks) {
			tasks.resize(capacity);
		}
	}

	/**
     * 
     * 
     */
	public int getCapacity() {
		return tasks.getCapacity();
	}

	/**
     * 
     * 
     */
	@Override
	public void run() {
		while (running) {
			Log.v(LoadResourceManager.TAG, "when run, running");
			synchronized (tasks) {
				currentRequest = null;
				while (running && (currentRequest = tasks.pop()) == null) {
					assert tasks.empty() : "tasks.empty( )";
					try {
						Log.v(LoadResourceManager.TAG, "when run, wait begin");
						tasks.wait();
						Log.v(LoadResourceManager.TAG, "when run, wait end");
					} catch (InterruptedException e) {
						Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
						return;
					}
				}
			}

			if (currentRequest != null) {
				Log.v(LoadResourceManager.TAG, "when run,begin download: "
						+ currentRequest.urn);
				assert currentRequest.isValid() : "currentRequest.isValid( )";
				download(currentRequest);
				Log.v(LoadResourceManager.TAG, "when run,end download: "
						+ currentRequest.urn);
				// Finished
				notifyFinished(currentRequest);
			}
		}
	}

	/**
     * 
     * 
     */
	protected abstract void download(Request request);
}
