package com.pengjun.android.camera;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pengjun.android.component.CreateLooperThread;

public class CameraCaptureDataHandler extends Handler {

	public static final int CAPTURE_VIDEOFRAME = 0x01;
	private CameraCapture.CallBack output;

	private CameraCaptureDataHandler(Looper looper, CameraCapture.CallBack output) {
		super(looper);
		this.output = output;
	}

	public static CameraCaptureDataHandler newInstance(
			CameraCapture.CallBack output) {
		CreateLooperThread thread = new CreateLooperThread();
		thread.start();
		try {
			thread.waitForMyLooper();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new CameraCaptureDataHandler(thread.myLooper(), output);
	}

	public void handleMessage(Message msg) {
		switch (msg.what) {
		case CAPTURE_VIDEOFRAME:
			CameraCaptureData videoCaputureData = (CameraCaptureData) msg.obj;
			output.onCameraCaptured(videoCaputureData.getData(),
					videoCaputureData.getData().length,
					videoCaputureData.getWidth(), videoCaputureData.getHeigth());
			break;
		}
	}
}
