package com.pengjun.android.loadresource.downloader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

import com.pengjun.android.loadresource.LoadResourceManager;

public abstract class AbstractOrderDownloader implements Downloader, Runnable {
	private Queue<Request> tasks = null;
	private volatile boolean running = true;
	private Request currentRequest = null;

	/**
     * 
     * 
     */
	public AbstractOrderDownloader() {
		tasks = new LinkedList<Request>();
	}

	/**
     * 
     * 
     */
	public void get(Request request) {
		synchronized (tasks) {

			boolean success = tasks.offer(request);
			assert success == true : "success==true";
			tasks.notifyAll();
		}
	}

	/**
     * 
     * 
     */
	public void abort(String urn) {
		synchronized (tasks) {
			if (currentRequest != null && currentRequest.urn.equals(urn)) {
				currentRequest.abort = true;
			}

			Iterator<Request> iter = tasks.iterator();
			while (iter.hasNext()) {
				Request request = iter.next();
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
	public void abortAll() {
		synchronized (tasks) {
			if (currentRequest != null) {
				currentRequest.abort = true;
			}

			Iterator<Request> iter = tasks.iterator();
			while (iter.hasNext()) {
				Request request = iter.next();
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
	public void run() {
		while (running) {
			Log.v(LoadResourceManager.TAG, "when run, running");
			synchronized (tasks) {
				currentRequest = null;
				while (running && (currentRequest = tasks.poll()) == null) {
					assert tasks.isEmpty() : "tasks.isEmpty( )";
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
			}
		}
	}

	/**
     * 
     * 
     */
	protected abstract void download(Request request);

	/**
     * 
     * 
     */
	protected abstract void discard(Request request);
}
