package com.pengjun.android.camera;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pengjun.android.utils.AdLoggerUtils;

public class CameraCapture implements PreviewCallback {

	public static Logger logger = AdLoggerUtils.getLogger("cameraCapture");

	public interface CallBack {

		public void handleVideoCaptureMessage(Message msg);

		public void onCameraCaptured(byte[] data, int len, int width, int height);

	}

	private Camera camera = null;
	private boolean running = false;
	private final boolean isSupportCameraOrientation = false;

	private final int orientation = 90;
	private int degrees = 0;

	private final Map<CallBack, CameraCaptureDataHandler> caputureRecvHandlerMap = new HashMap<CallBack, CameraCaptureDataHandler>();

	private PreviewSurfaceView surfaceView;
	private Context parentContext;
	private int displayOrientation;
	private int srcWidth;
	private int srcHeight;
	private CameraMsgHandler videoCaptureHandler;
	public final static int OPEN_CAMERA_FAILED = 01;

	private static SurfaceTexture surfaceTexture;
	static {
		if (Build.VERSION.SDK_INT >= 14) {
			surfaceTexture = new SurfaceTexture(0);
		}
	}

	public void setCallBack(CallBack callBack) {
		videoCaptureHandler = CameraMsgHandler.newInstance(callBack);
	}

	public void registerRawVideoReceiver(CallBack receiver) {
		if (receiver != null) {
			if (caputureRecvHandlerMap.get(receiver) == null) {
				caputureRecvHandlerMap.put(receiver,
						CameraCaptureDataHandler.newInstance(receiver));
			}
		}

	}

	public void deregisterRawVideoReceiver(CallBack receiver) {
		if (receiver != null) {
			caputureRecvHandlerMap.remove(receiver);
		}
	}

	public SurfaceView startCapture(Context context, boolean usingFront, int w,
			int h, int displayOrientation) throws CameraCaptureException {

		surfaceView = new PreviewSurfaceView(context);
		startCapture(context, usingFront, w, h, displayOrientation, surfaceView);
		return surfaceView;
	}

	public void startCaptureWithoutShow(boolean usingFront, int w, int h,
			int displayOrientation) throws CameraCaptureException {
		logger.debug("startCapture without show");
		startCapture(null, usingFront, w, h, displayOrientation, null);
	}

	public SurfaceView startCaptureWithoutSize(Context context,
			boolean usingFront, int displayOrientation)
			throws CameraCaptureException {
		logger.debug("startCapture without size");
		surfaceView = new PreviewSurfaceView(context);
		startCapture(context, usingFront, -1, -1, displayOrientation,
				surfaceView);
		return surfaceView;
	}

	private SurfaceView startCapture(Context context, boolean usingFront,
			int w, int h, int displayOrientation, PreviewSurfaceView surfaceView)
			throws CameraCaptureException {

		logger.info("start startCapture : usingFront=" + usingFront + ", w="
				+ w + " h=" + h);

		if (camera != null) {
			logger.warn("camera is not null");
			throw new CameraCaptureException(
					CameraCaptureCode.Camera_Is_Running);
		}

		try {
			if (camera == null) {
				int camera_num = Camera.getNumberOfCameras();
				if (camera_num < 1) {
					logger.warn("startCapture, but no camera");
					throw new CameraCaptureException(
							CameraCaptureCode.No_Camera);
				}
				if (camera_num < 2) {
					usingFront = false;
				}

				degrees = getCameraOrientation(orientation, displayOrientation);
				camera = Camera.open(usingFront ? 1 : 0);

				Parameters params = camera.getParameters();
				if (w != -1 && h != -1) {
					params.setPreviewSize(w, h);
				}
				params.setPreviewFormat(ImageFormat.NV21);

				camera.setParameters(params);

				if (surfaceView != null) {
					surfaceView.getHolder().setType(
							SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
					surfaceView.getHolder().addCallback(surfaceView);
				} else {
					// not return SurfaceView
					openCamera(null);
				}

			}
			if (camera == null) {
				logger.error("failed to open "
						+ (usingFront ? "front" : "back") + " camera");

				throw new CameraCaptureException(CameraCaptureCode.Unknown);
			}

			camera.setPreviewCallback(this);

			running = true;
			parentContext = context;
			srcWidth = w;
			srcHeight = h;

			logger.info("leave start startCapture");

			return running ? surfaceView : null;
		} catch (Exception e) {
			releaseCamera();
			e.printStackTrace();
			throw new CameraCaptureException(CameraCaptureCode.Unknown);
		}
	}

	public void pauseCapture() {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallbackWithBuffer(null);
		}
	}

	public void resumeCapture() {
		if (camera != null) {
			camera.startPreview();
			camera.setPreviewCallback(this);
		}
	}

	public void stopCapture() {

		logger.debug("stopCapture");
		try {
			if (camera != null) {
				camera.stopPreview();
				camera.setPreviewCallbackWithBuffer(null);
				releaseCamera();
			} else {
				logger.warn("camera is null, why try to stop?");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			running = false;
		}
		logger.debug("leave stopCapture");
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.release();
			camera = null;
		}

	}

	public boolean isPreviewing() {
		return running;
	}

	private void openCamera(SurfaceHolder holder) {
		logger.debug("preview camera openCamera");
		try {
			if (camera != null) {
				camera.stopPreview();
				if (holder != null) {
					camera.setPreviewDisplay(holder);
				} else {
					if (surfaceTexture != null) {
						camera.setPreviewTexture(surfaceTexture);
					}
				}
				if (isSupportCameraOrientation) {
					camera.setDisplayOrientation(degrees);
				}
				camera.setPreviewCallback(this);
				camera.startPreview();
			}
		} catch (Exception e) {
			releaseCamera();
			logger.debug("open camera failed");
			if (videoCaptureHandler != null) {
				Message msg = new Message();
				msg.what = CameraCapture.OPEN_CAMERA_FAILED;
				videoCaptureHandler.sendMessage(msg);
			}
		}
	}

	public void switchCamera(boolean usingFrontCamera)
			throws CameraCaptureException {
		if (surfaceView != null) {
			stopCapture();
			startCapture(this.parentContext, usingFrontCamera, srcWidth,
					srcHeight, displayOrientation, surfaceView);

			openCamera(surfaceView.getHolder());
		}

	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Message msg = new Message();
		for (Map.Entry<CallBack, CameraCaptureDataHandler> entry : caputureRecvHandlerMap
				.entrySet()) {
			CameraCaptureDataHandler handler = entry.getValue();
			msg.obj = new CameraCaptureData(data, srcWidth, srcHeight);
			msg.what = CameraCaptureDataHandler.CAPTURE_VIDEOFRAME;
			handler.sendMessage(msg);

		}
	}

	class PreviewSurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		public PreviewSurfaceView(Context context) {
			super(context);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			logger.debug("preview camera surface changed, holder:" + holder
					+ "; format:" + format + "; w:" + w + "; h:" + h);

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			logger.debug("preview camera surfaceCreated");
			openCamera(holder);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			logger.debug("preview camera surfaceDestroyed");
			try {
				if (camera != null) {
					camera.stopPreview();
					camera.setPreviewDisplay(null);
				}
			} catch (IOException e) {
				logger.error("Failed to clear preview surface!", e);
			}
		}

	}

	public static int getCameraOrientation(int cameraOrientation,
			int displayRotation) {
		int degrees = 0;
		switch (displayRotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result = 0;
		if (cameraOrientation > 180) {
			result = (cameraOrientation + degrees) % 360;
		} else {
			result = (cameraOrientation - degrees + 360) % 360;
		}
		return result;
	}

	public void setCamearaOrientation(int displayOrientation) {
		logger.debug("to set camera orientation, display orientation:"
				+ displayOrientation);
		this.displayOrientation = displayOrientation;
		try {
			if (camera == null)
				return;
			degrees = getCameraOrientation(orientation, displayOrientation);
			logger.warn("to set camera orientation, display orientation:"
					+ degrees);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("failed to set display orientation, degrees:" + degrees);
		}
	}

}
