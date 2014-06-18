package com.pengjun.android.component;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pengjun.android.utils.AdImageUtils;

public class VideoViewScale {

	public static class ViewSize {

		public int width;
		public int height;

		public ViewSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

	}

	public interface VideoViewScaleListener {
		void onVideoViewScaleFinished();
	}

	private String TAG = VideoViewScale.class.getSimpleName();

	private FrameLayout flVideo;
	private SurfaceView svVideo;
	private ScaleAnimation smallerScaleAnimation;
	private ScaleAnimation largerScaleAnimation;
	private Animation alphaAnimation;
	private ImageView ivCapturedFrame;
	private Handler handler;
	public Activity activity;
	private Bitmap capturedFrameBmp;

	private final int SMALLER_CAPTURE_FRAME = 0;
	private final int RESIZE_SMALLER_VIDEO_VIEW = 1;
	private final int RESIZE_SMALLER_CAPTURE_FRAME_AND_FADE_OUT = 2;

	private final int LARGER_CAPTURE_FRAME = 3;
	private final int RESIZE_LARGER_CAPTURE_FRAME_AND_FADE_OUT_AND_RESIZE_LARGER_VIDEO_VIEW = 4;
	private final int REMOVE_CAPTURE_FRAME_VIEW = 5;
	private static String ANIMATION_FINISH = "animation_tag";
	private ViewSize iniSize;
	private ViewSize dstSize;
	private Rect marginRect;
	private VideoViewScaleListener videoViewScaleListener;
	private ScaleState scaleMode = ScaleState.UNINIT;

	enum ScaleState {
		UNINIT, SMALLER_MODE, LARGER_MODE
	};

	public void addVideoViewScaleListener(
			VideoViewScaleListener videoViewScaleListener) {
		this.videoViewScaleListener = videoViewScaleListener;
	}

	public VideoViewScale(Activity a, FrameLayout fl, SurfaceView sv,
			ViewSize iniS, ViewSize dstS, Rect marginR) {

		this.flVideo = fl;
		this.activity = a;
		this.svVideo = sv;
		this.iniSize = iniS;
		this.dstSize = dstS;
		this.marginRect = marginR;

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				FrameLayout.LayoutParams llParams = null;
				Log.d(TAG, "VideoViewScale handleMessage:" + msg.what);
				switch (msg.what) {
				case SMALLER_CAPTURE_FRAME:

					ivCapturedFrame = new ImageView(activity);
					llParams = new FrameLayout.LayoutParams(iniSize.width,
							iniSize.height);

					if (capturedFrameBmp != null) {
						ivCapturedFrame.setBackgroundDrawable(AdImageUtils
								.bitmap2Drawable(capturedFrameBmp));
					}

					flVideo.addView(ivCapturedFrame, llParams);

					ivCapturedFrame.startAnimation(smallerScaleAnimation);

					break;
				case RESIZE_SMALLER_VIDEO_VIEW:
					llParams = new FrameLayout.LayoutParams(dstSize.width,
							dstSize.height);
					llParams.setMargins(marginRect.left, marginRect.top,
							marginRect.bottom, marginRect.right);
					if (svVideo != null)
						svVideo.setLayoutParams(llParams);

					break;
				case RESIZE_SMALLER_CAPTURE_FRAME_AND_FADE_OUT:
					llParams = new FrameLayout.LayoutParams(dstSize.width,
							dstSize.height);
					llParams.setMargins(marginRect.left, marginRect.top,
							marginRect.bottom, marginRect.right);
					ivCapturedFrame.setLayoutParams(llParams);

					ivCapturedFrame.startAnimation(alphaAnimation);

					break;
				case LARGER_CAPTURE_FRAME:

					ivCapturedFrame = new ImageView(activity);
					llParams = new FrameLayout.LayoutParams(dstSize.width,
							dstSize.height);
					llParams.setMargins(marginRect.left, marginRect.top,
							marginRect.bottom, marginRect.right);

					if (capturedFrameBmp != null) {
						ivCapturedFrame.setBackgroundDrawable(AdImageUtils
								.bitmap2Drawable(capturedFrameBmp));
					}

					flVideo.addView(ivCapturedFrame, llParams);
					ivCapturedFrame.startAnimation(largerScaleAnimation);

					break;
				case RESIZE_LARGER_CAPTURE_FRAME_AND_FADE_OUT_AND_RESIZE_LARGER_VIDEO_VIEW:

					llParams = new FrameLayout.LayoutParams(iniSize.width,
							iniSize.height);
					ivCapturedFrame.setLayoutParams(llParams);
					ivCapturedFrame.startAnimation(alphaAnimation);

					llParams = new FrameLayout.LayoutParams(iniSize.width,
							iniSize.height);
					if (svVideo != null)
						svVideo.setLayoutParams(llParams);

					break;
				case REMOVE_CAPTURE_FRAME_VIEW:
					flVideo.removeView(ivCapturedFrame);
					if (videoViewScaleListener != null) {
						videoViewScaleListener.onVideoViewScaleFinished();
					}
					break;
				}

			}

		};

		alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Message msg = new Message();
				msg.what = REMOVE_CAPTURE_FRAME_VIEW;
				handler.sendMessageDelayed(msg, 0);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		alphaAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);

	}

	public void smaller(Bitmap capturedFrameBmp, int duration) {

		Log.d(TAG, "smallerScale");

		this.capturedFrameBmp = capturedFrameBmp;
		smallerScaleAnimation = new ScaleAnimation(1.0f, (float) dstSize.width
				/ iniSize.width, 1.0f, (float) dstSize.height / iniSize.height,
				Animation.RELATIVE_TO_SELF, (float) (marginRect.left)
						/ dstSize.width
						/ ((float) iniSize.width / dstSize.width - 1),
				Animation.RELATIVE_TO_SELF, (float) marginRect.top
						/ dstSize.height
						/ ((float) iniSize.height / dstSize.height - 1));
		smallerScaleAnimation.setDuration(duration);
		smallerScaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				Message msg = new Message();
				msg.what = RESIZE_SMALLER_VIDEO_VIEW;
				handler.sendMessageDelayed(msg, 0);

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				Message msg = new Message();
				msg.what = RESIZE_SMALLER_CAPTURE_FRAME_AND_FADE_OUT;
				handler.sendMessageDelayed(msg, 0);
				Intent intent = new Intent();
				intent.setAction(ANIMATION_FINISH);
				activity.sendBroadcast(intent);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		handler.sendEmptyMessage(SMALLER_CAPTURE_FRAME);
	}

	public void larger(Bitmap capturedFrameBmp, int duration) {

		this.capturedFrameBmp = capturedFrameBmp;

		largerScaleAnimation = new ScaleAnimation(1.0f, (float) (iniSize.width)
				/ dstSize.width, 1.0f, (float) iniSize.height / dstSize.height,
				Animation.RELATIVE_TO_SELF, (float) marginRect.left
						/ dstSize.width
						/ ((float) iniSize.width / dstSize.width - 1),
				Animation.RELATIVE_TO_SELF, (float) marginRect.top
						/ dstSize.height
						/ ((float) iniSize.height / dstSize.height - 1));

		largerScaleAnimation.setDuration(duration);
		largerScaleAnimation.setFillAfter(true);
		largerScaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Message msg = new Message();
				msg.what = RESIZE_LARGER_CAPTURE_FRAME_AND_FADE_OUT_AND_RESIZE_LARGER_VIDEO_VIEW;
				handler.sendMessageDelayed(msg, 0);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		handler.sendEmptyMessage(LARGER_CAPTURE_FRAME);

	}

	public void resetCameraPreview(Rect marginRect, int width, int height) {
		this.marginRect = marginRect;
		FrameLayout.LayoutParams llParams = null;
		llParams = new FrameLayout.LayoutParams(width, height);
		llParams.setMargins(marginRect.left, marginRect.top, marginRect.bottom,
				marginRect.right);
		if (svVideo != null)
			svVideo.setLayoutParams(llParams);
	}

	public void stopAnimation() {
		Log.d(TAG, "stopAnimation");
		scaleMode = ScaleState.UNINIT;
		if (ivCapturedFrame != null) {
			// not work onAnimationEnd still called
			ivCapturedFrame.clearAnimation();
		}
		handler.removeMessages(SMALLER_CAPTURE_FRAME);
		handler.removeMessages(RESIZE_SMALLER_VIDEO_VIEW);
		handler.removeMessages(RESIZE_SMALLER_CAPTURE_FRAME_AND_FADE_OUT);
		handler.removeMessages(LARGER_CAPTURE_FRAME);
		handler.removeMessages(RESIZE_LARGER_CAPTURE_FRAME_AND_FADE_OUT_AND_RESIZE_LARGER_VIDEO_VIEW);
	}

}
