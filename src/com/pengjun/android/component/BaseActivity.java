package com.pengjun.android.component;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {

	protected abstract void create(Bundle savedInstanceState);

	protected abstract void destory();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		create(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		destory();
	}
}
