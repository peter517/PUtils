package com.pengjun.android.camera;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pengjun.android.component.CreateLooperThread;

public class CameraMsgHandler extends Handler {

	private CameraCapture.CallBack callBack;

	private CameraMsgHandler(Looper looper, CameraCapture.CallBack callBack) {
		super(looper);
		this.callBack = callBack;
	}

	public static CameraMsgHandler newInstance(
			CameraCapture.CallBack callBack) {
		CreateLooperThread thread = new CreateLooperThread();
		thread.start();
		try {
			thread.waitForMyLooper();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new CameraMsgHandler(thread.myLooper(), callBack);
	}

	public void handleMessage(Message msg) {
		callBack.handleVideoCaptureMessage(msg);
	}
}
