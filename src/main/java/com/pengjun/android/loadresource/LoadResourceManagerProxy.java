package com.pengjun.android.loadresource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pengjun.android.component.CreateLooperThread;
import com.pengjun.android.loadresource.LoadResourceManager.LoadRequest;
import com.pengjun.android.loadresource.LoadResourceManager.ReplyListener;
import com.pengjun.android.loadresource.downloader.DisplayDownloader;
import com.pengjun.android.loadresource.downloader.Downloader.Error;
import com.pengjun.android.loadresource.factory.ImageFactory;
import com.pengjun.utils.FileUtils;

public final class LoadResourceManagerProxy {

	public static enum DownloaderIndex {
		Display
	}

	public static int displayDownloaderDefaultCapacity = 30;
	public static int maximumDiskCacheSize = 1024 * 1024 * 50;
	public static int maximumMemeryCacheSize = 1024 * 1024 * 10;
	public static String mimeType = "image";

	private final static CreateLooperThread createLooperThread = new CreateLooperThread();

	private final Thread displayDownloaderThread;

	static {
		try {
			createLooperThread.start();
			createLooperThread.waitForMyLooper();
		} catch (InterruptedException e) {
			Log.e(LoadResourceManager.TAG, "Caught: " + e, e);
		}
	}

	public LoadResourceManagerProxy(Context context) {

		LoadResourceManager resourceManager = LoadResourceManager.create(
				createLooperThread.myLooper(), context);

		resourceManager.setCacheDirectory(FileUtils.getAppCachePath(context));
		resourceManager.registerObjectFactory(mimeType, new ImageFactory());
		resourceManager.setMaximumDiskCacheSize(maximumDiskCacheSize);
		resourceManager.setMaximumMemoryCacheSize(maximumMemeryCacheSize);

		// resourceManager.clearDiskCache();

		DisplayDownloader displayDownloader = new DisplayDownloader(
				displayDownloaderDefaultCapacity);
		displayDownloader.setReplyListener(resourceManager);
		resourceManager.registerDownloader(DownloaderIndex.Display.ordinal(),
				displayDownloader);

		displayDownloaderThread = new Thread(displayDownloader);
		displayDownloaderThread.start();

	}

	public void destroy() {
		LoadResourceManager.destroy();
	}

	/**
	 * Example Code
	 * 
	 */
	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.obj != null) {
				Bitmap bitmap = (Bitmap) msg.obj;
				// ImageView iv = new ImageView(MainActivity.this);
				// iv.setBackgroundDrawable(new BitmapDrawable(bitmap));
				// rl.addView(iv);
				// rl.invalidate();
			}
		}
	};

	public static void main(String[] args) {

		String uri = "http://icons.iconarchive.com/icons/pelfusion/christmas-shadow-2/512/Apple-icon.png";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(DisplayDownloader.URL, uri);
		LoadRequest request = new LoadRequest(uri, map,
				LoadResourceManagerProxy.mimeType,
				LoadResourceManagerProxy.DownloaderIndex.Display.ordinal(),
				new ReplyListener() {

					@Override
					public void onDownloadProgress(LoadRequest request,
							long bytesReceived, long bytesTotal) {
					}

					@Override
					public void onFinished(LoadRequest request, Error error) {
						Message msg = new Message();
						try {
							msg.obj = LoadResourceManager.getSingleton().get(
									request);
						} catch (IOException e) {
							e.printStackTrace();
						}
						handler.sendMessage(msg);
					}
				});

		try {
			Bitmap bitmap = (Bitmap) LoadResourceManager.getSingleton().get(
					request);
			if (bitmap != null) {
				Message msg = new Message();
				msg.obj = bitmap;
				handler.sendMessage(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
