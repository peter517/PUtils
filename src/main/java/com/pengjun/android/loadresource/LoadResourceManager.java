package com.pengjun.android.loadresource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.pengjun.android.loadresource.cache.ConcurrentLruDiskCacheProxy;
import com.pengjun.android.loadresource.cache.ConcurrentLruMemoryCache;
import com.pengjun.android.loadresource.cache.ConcurrentLruMemoryCache.OnRemovedListener;
import com.pengjun.android.loadresource.downloader.Downloader;
import com.pengjun.android.loadresource.factory.ObjectFactory;
import com.pengjun.android.loadresource.factory.ObjectFactory.FileParseException;
import com.pengjun.utils.StringUtils;

public final class LoadResourceManager extends Handler implements
		OnRemovedListener, Downloader.ReplyListener {

	private final ConcurrentLruMemoryCache<String, CacheObject> memoryCache = new ConcurrentLruMemoryCache<String, CacheObject>(
			2 * 1024 * 1024);
	private final ConcurrentLruDiskCacheProxy diskCache = new ConcurrentLruDiskCacheProxy(
			50 * 1024 * 1024);
	private final HashMap<Integer, Downloader> downloaderMap = new HashMap<Integer, Downloader>();
	private final HashMap<String, ObjectFactory> objectFactoryMap = new HashMap<String, ObjectFactory>();
	private final HashMap<String, HashSet<ReplyListener>> callbackMap = new HashMap<String, HashSet<ReplyListener>>();
	public final static String TAG = "loadresource";

	public static class LoadRequest implements Cloneable {
		public final String urn;
		public final Map<String, Object> params;
		public final String mimeType;
		public ReplyListener replyListener;
		public final int downloaderIndex;
		public final Uri uri;
		public final boolean asyncDecode;

		public LoadRequest(String urn, Map<String, Object> params,
				String mimeType, int downloaderIndex) {
			this(urn, params, mimeType, downloaderIndex, null, null, false);
		}

		public LoadRequest(String urn, Map<String, Object> params,
				String mimeType, int downloaderIndex,
				ReplyListener replyListener) {
			this(urn, params, mimeType, downloaderIndex, replyListener, null,
					false);
		}

		public LoadRequest(String urn, Map<String, Object> params,
				String mimeType, int downloaderIndex,
				ReplyListener replyListener, Uri uri, boolean asyncDecode) {
			this.urn = StringUtils.createMd5(urn);
			this.params = params;
			this.mimeType = mimeType;
			this.downloaderIndex = downloaderIndex;
			this.replyListener = replyListener;
			this.uri = uri;
			this.asyncDecode = asyncDecode;
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		@Override
		public String toString() {
			return urn;
		}
	}

	public static interface ReplyListener {
		void onDownloadProgress(LoadRequest request, long bytesReceived,
				long bytesTotal);

		void onFinished(LoadRequest request, Downloader.Error error);
	}

	private static LoadResourceManager singleton = null;

	/**
     * 
     */
	public static LoadResourceManager create(Looper looper, Context context) {
		assert singleton == null : "singleton==null";
		singleton = new LoadResourceManager(looper, context);
		return singleton;
	}

	/**
     * 
     */
	public static void destroy() {
		if (singleton != null) {
			singleton.stopAll();
			singleton.clearMemoryCache();
			singleton = null;
		}
	}

	/**
     * 
     */
	public static LoadResourceManager getSingleton() {
		return singleton;
	}

	private static class CacheObject {
		public final Object obj; // public SoftReference<Object> obj;
		public final String mimeType;
		public final Object opt;

		public CacheObject(Object obj, String mimeType, Object opt) {
			this.obj = obj;
			this.mimeType = mimeType;
			this.opt = opt;
		}
	}

	/**
     * 
     */
	private LoadResourceManager(Looper looper, Context context) {
		super(looper);
		memoryCache.setOnRemovedListener(this);
	}

	/**
     * 
     */
	public void setCacheDirectory(String dir) {
		diskCache.setCacheDirectory(dir);
	}

	/**
	 * 
	 */
	public Object get(LoadRequest request) throws IOException {
		assert request.urn != null : "request.urn!=null";

		Object obj = getFromMemoryCache(request.urn, request.params);
		if (isInSameThread()) {
			if (obj != null) {
				updateFileDate(request.urn);
			} else {
				obj = getFromPersistence(request, true);
			}
		} else {
			if (obj != null) {
				postUpdateFileDate(request.urn);
			} else if (!request.asyncDecode) {
				obj = getFromPersistence(request, false);
				if (obj == null) {
					postGetFromPersistence(request);
				}
			} else {
				verifyFileExists(request);
				postGetFromPersistence(request);
			}
		}

		return obj;
	}

	/**
	 * 
	 */
	public void postDownload(LoadRequest request) {
		Message msg = this.obtainMessage(MessageWhat.Download, request);
		if (!this.sendMessage(msg)) {
			throw new RuntimeException("send message failed");
		}
	}

	/**
     * 
     */
	public Downloader registerDownloader(int key, Downloader value) {
		assert key >= 0 && value != null : "key>=0 && value!=null";
		return downloaderMap.put(key, value);
	}

	/**
     * 
     */
	public ObjectFactory registerObjectFactory(String mimeType,
			ObjectFactory objectFactory) {
		assert objectFactory != null : "objectFactory!=null";
		return objectFactoryMap.put(mimeType, objectFactory);
	}

	/**
     * 
     */
	public void clearMemoryCache() {
		memoryCache.clear();
	}

	/**
     * 
     */
	public void clearDiskCache() {
		diskCache.clear();
	}

	/**
     * 
     */
	public void setMaximumDiskCacheSize(long size) {
		diskCache.setMaximumCacheSize(size);
	}

	/**
     * 
     */
	public void setMaximumMemoryCacheSize(int size) {
		memoryCache.setMaxCost(size);
	}

	public boolean insertMemoryCache(String urn, Object obj, long memorySize,
			String mimeType, Object option) {
		CacheObject cacheObj = new CacheObject(obj, mimeType, option);
		return memoryCache.insert(urn, cacheObj, memorySize);
	}

	public Object getFromMemoryCache(String urn) {
		CacheObject cacheObj = memoryCache.object(urn);

		return cacheObj == null ? null : cacheObj.obj;
	}

	/**
     * 
     */
	public void abort(LoadRequest request) {
		this.removeMessages(MessageWhat.GetFromPersistence, request);
		this.removeMessages(MessageWhat.UpdateFileDate, request);
		abortDownload(request);
	}

	/**
     * 
     */
	public void abortAll() {
		this.removeMessages(MessageWhat.GetFromPersistence);
		this.removeMessages(MessageWhat.UpdateFileDate);
		for (Downloader downloader : downloaderMap.values()) {
			downloader.abortAll();
		}
	}

	/**
     * 
     */
	public void stopAll() {
		this.removeMessages(MessageWhat.GetFromPersistence);
		this.removeMessages(MessageWhat.UpdateFileDate);
		for (Downloader downloader : downloaderMap.values()) {
			downloader.stop();
		}
	}

	/**
     * 
     */
	private void download(String urn, ReplyListener replyListener,
			int downloaderIndex, Object params, Object tag) throws IOException {

		if (replyListener != null) {
			HashSet<ReplyListener> replyListenerSet = callbackMap.get(urn);
			if (replyListenerSet == null) {
				replyListenerSet = new HashSet<ReplyListener>();
				callbackMap.put(urn, replyListenerSet);
			}
			replyListenerSet.add(replyListener);
		}

		File file = diskCache.prepare(urn);
		if (file == null) {
			return;
		}

		Downloader downloader = downloaderMap.get(downloaderIndex);
		assert downloader != null : "downloader!=null";

		Downloader.Request request = new Downloader.Request(urn, file, params,
				tag);
		downloader.get(request);
	}

	private void updateFileDate(String urn) {
		diskCache.getData(urn);
	}

	/**
     * 
     */
	private Object getFromMemoryCache(String urn, Map<String, Object> params) {
		CacheObject cacheObj = memoryCache.object(urn);
		if (cacheObj == null) {
			return null;
		}

		Object obj = cacheObj.obj;// Object obj = cacheObj.obj.get();

		ObjectFactory objectFactory = objectFactoryMap.get(cacheObj.mimeType);
		assert objectFactory != null : "objectFactory!=null";

		if (objectFactory.verify(obj, params, cacheObj.opt)) {
			return obj;
		} else {
			memoryCache.remove(urn);
			return null;
		}
	}

	/**
	 * 
	 */
	private ObjectFactory.Result decodeFile(File file, String mimeType,
			Map<String, Object> params) throws IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}

		ObjectFactory objectFactory = objectFactoryMap.get(mimeType);
		assert objectFactory != null : "objectFactory!=null";

		ObjectFactory.Result result = objectFactory.decodeSize(file,
				memoryCache.getMaxCost(), params);
		memoryCache.reserved(result.size);
		return objectFactory.decodeFile(file, params, result.opt);
	}

	/**
	 * 
	 */
	private ObjectFactory.Result getFromDiskCache(String urn, String mimeType,
			Map<String, Object> params) throws IOException {
		File file = diskCache.getData(urn);
		try {
			return decodeFile(file, mimeType, params);
		} catch (FileParseException e) {
			// if (!diskCache.remove(urn)) {
			// Log.w(LogConst.TAG_RESOURCE,
			// "delete file that can't decoded failed: "
			// + file.getAbsolutePath());
			// }
			throw e;
		}
	}

	/**
	 * 
	 */
	private ObjectFactory.Result getDirectFromFile(String path,
			String mimeType, Map<String, Object> params) throws IOException {
		File file = new File(path);
		return decodeFile(file, mimeType, params);
	}

	/**
	 * 
	 */
	private Object getFromPersistence(LoadRequest request, boolean needDownload)
			throws IOException {

		ObjectFactory.Result result;
		if (request.uri != null
				&& "file".equalsIgnoreCase(request.uri.getScheme())) {
			result = getDirectFromFile(request.uri.getPath(), request.mimeType,
					request.params);
		} else {
			try {
				result = getFromDiskCache(request.urn, request.mimeType,
						request.params);
			} catch (FileNotFoundException e) {
				if (needDownload) {
					if (request.downloaderIndex >= 0 && isInSameThread()) {
						download(request.urn, request.replyListener,
								request.downloaderIndex, request.params,
								request);
						return null;
					} else {
						throw e;
					}
				} else {
					return null;
				}
			}
		}

		assert result != null && result.obj != null && result.size > 0 : "result!=null && result.obj != null && result.size > 0";
		Object obj = result.obj;// obj = new
		// SoftReference<Object>(result.obj);

		if (insertMemoryCache(request.urn, obj, result.size, request.mimeType,
				result.opt)) {
			return result.obj;
		} else {
			ObjectFactory objectFactory = objectFactoryMap
					.get(request.mimeType);
			assert objectFactory != null : "objectFactory!=null";
			objectFactory.destroy(result.obj, result.opt);

			throw new FileParseException(request + " decode size out of memory");
		}
	}

	private void verifyFileExists(LoadRequest request)
			throws FileNotFoundException {
		if (request.uri != null
				&& "file".equalsIgnoreCase(request.uri.getScheme())) {
			File file = new File(request.uri.getPath());
			if (!file.exists()) {
				throw new FileNotFoundException(file.getAbsolutePath());
			}
		}
	}

	private boolean isInSameThread() {
		return this.getLooper().getThread().getId() == Thread.currentThread()
				.getId();
	}

	/**
     * 
     */
	@Override
	public void onRemoved(Object key, Object value) {
		CacheObject cacheObj = (CacheObject) value;
		Object obj = cacheObj.obj;// Object obj = cacheObj.obj.get();

		ObjectFactory objectFactory = objectFactoryMap.get(cacheObj.mimeType);
		assert objectFactory != null : "objectFactory!=null";
		objectFactory.destroy(obj, cacheObj.opt);

	}

	private static class MessageWhat {
		// async call
		public static final int GetFromPersistence = 0;
		public static final int UpdateFileDate = 1;
		public static final int Download = 2;
	}

	/**
     * 
     */
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MessageWhat.GetFromPersistence: {
			assert msg.obj != null : "msg.obj!=null";
			final LoadRequest request = (LoadRequest) msg.obj;
			final ReplyListener l = request.replyListener;
			try {
				Object obj = getFromMemoryCache(request.urn, request.params);
				if (obj == null) {
					obj = getFromPersistence(request, true);
				}
				if (l != null && obj != null) {
					l.onFinished(request, Downloader.Error.NoError);
				}
			} catch (FileParseException e) {
				Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
				if (l != null) {
					l.onFinished(request, Downloader.Error.ContentError);
				}
			} catch (FileNotFoundException e) {
				Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
				if (l != null) {
					l.onFinished(request, Downloader.Error.ContentNotFound);
				}
			} catch (IOException e) {
				if (l != null) {
					l.onFinished(request, Downloader.Error.LocalFileIOError);
				}
			}
			this.removeMessages(msg.what, msg.obj);
		}
			break;
		case MessageWhat.UpdateFileDate: {
			assert msg.obj != null : "msg.obj!=null";
			final String urn = (String) msg.obj;
			updateFileDate(urn);
			this.removeMessages(msg.what, msg.obj);
		}
			break;
		case MessageWhat.Download: {
			assert msg.obj != null : "msg.obj!=null";
			final LoadRequest request = (LoadRequest) msg.obj;
			final File file = diskCache.getData(request.urn);
			final ReplyListener l = request.replyListener;
			if (!file.exists()) {
				try {
					download(request.urn, request.replyListener,
							request.downloaderIndex, request.params, request);
				} catch (IOException e) {
					if (l != null) {
						l.onFinished(request, Downloader.Error.LocalFileIOError);
					}
				}
			} else {
				if (l != null) {
					l.onFinished(request, Downloader.Error.NoError);
				}
			}
			this.removeMessages(msg.what, msg.obj);
		}
			break;
		default:
			Log.e(LoadResourceManager.TAG, "msg.what unkown");
			break;
		}

		super.handleMessage(msg);
	}

	private void postGetFromPersistence(LoadRequest request) {
		Message msg = this.obtainMessage(MessageWhat.GetFromPersistence,
				request);
		if (!this.sendMessage(msg)) {
			throw new RuntimeException("send message failed");
		}
	}

	private void postUpdateFileDate(String urn) {
		Message msg = this.obtainMessage(MessageWhat.UpdateFileDate, urn);
		if (!this.sendMessage(msg)) {
			throw new RuntimeException("send message failed");
		}
	}

	private void abortDownload(final LoadRequest request) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				HashSet<ReplyListener> replyListenerSet = callbackMap
						.get(request.urn);
				if (replyListenerSet != null) {
					replyListenerSet.remove(request.replyListener);
					if (replyListenerSet.isEmpty()) {
						callbackMap.remove(request.urn);
						replyListenerSet = null;
					}
				}

				if (replyListenerSet == null) {
					Downloader downloader = downloaderMap
							.get(request.downloaderIndex);
					if (downloader != null) {
						downloader.abort(request.urn);
					}
				}
			}
		};

		if (isInSameThread()) {
			r.run();
		} else {
			this.postAtFrontOfQueue(r);
		}
	}

	@Override
	public void onDownloadProgress(final Downloader.Request request,
			final long bytesReceived, final long bytesTotal) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				final HashSet<ReplyListener> replyListenerSet = callbackMap
						.get(request.urn);
				if (replyListenerSet != null) {
					for (ReplyListener item : replyListenerSet) {
						item.onDownloadProgress((LoadRequest) request.tag,
								bytesReceived, bytesTotal);
					}
				}
			}
		};
		this.post(r);
	}

	@Override
	public void onDownloadFinished(final Downloader.Request request) {
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				final HashSet<ReplyListener> replyListenerSet = callbackMap
						.get(request.urn);
				if (request.error == Downloader.Error.NoError) {
					diskCache.insert(request.file);
				} else {
					if (!diskCache.cancel(request.file)) {
						throw new RuntimeException(
								"disk cache cancel mission failed: "
										+ request.file.getAbsolutePath());
					}
				}

				if (replyListenerSet != null) {
					for (ReplyListener item : replyListenerSet) {
						item.onFinished((LoadRequest) request.tag,
								request.error);
					}

					callbackMap.remove(request.urn)/* .clear( ) */;
				}
			}
		};
		this.post(r);
	}
}
