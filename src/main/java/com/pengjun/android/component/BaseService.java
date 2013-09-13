package com.pengjun.android.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class BaseService extends Service {

	protected abstract void create();

	protected abstract void destory();

	protected abstract void start();

	protected abstract void unBind();

	protected abstract IBinder bind(Intent intent);

	@Override
	public IBinder onBind(Intent intent) {
		return bind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		create();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destory();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		unBind();
		return super.onUnbind(intent);
	}

}
