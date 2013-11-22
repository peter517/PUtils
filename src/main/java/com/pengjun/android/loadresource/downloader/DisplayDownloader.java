package com.pengjun.android.loadresource.downloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.pengjun.android.loadresource.LoadResourceManager;
import com.pengjun.android.utils.AdImageUtils;
import com.pengjun.utils.ValueUtils;

public final class DisplayDownloader extends AbstractCircularStackDownloader {
	public static final String TAG = "tag";
	public static final String URL = "url";
	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final String ADJUST = "adjust";

	private final HttpClient httpClient;

	public DisplayDownloader(int capacity) {
		super(capacity);
		httpClient = new DefaultHttpClient(createHttpParams());
	}

	@Override
	protected void download(Request request) {

		if (!isRunning() || request.abort) {
			request.error = Error.ContentAccessDenied;
			return;
		}

		Map<String, Object> paramMap = (Map<String, Object>) request.params;
		final String url = paramMap.get(URL).toString();

		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse == null) {
				request.error = Error.NetworkError;
				return;
			}

			if (httpResponse.getStatusLine() == null
					|| httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				request.error = Error.ContentAccessDenied;
				return;
			}

			HttpEntity httpEntity = httpResponse.getEntity();

			if (httpEntity == null) {
				request.error = Error.ContentNotFound;
				return;
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(request.file, false);
				InputStream is = httpEntity.getContent();

				if (is == null) {
					request.error = Error.ContentNotFound;
					return;
				}

				final long contentLength = httpEntity.getContentLength();
				byte[] buffer = new byte[8 * 1024];
				int nb = 0;
				long bytesReceived = 0;
				do {
					int offset = 0;
					while (offset < buffer.length
							&& (nb = is.read(buffer, offset, buffer.length
									- offset)) >= 0) {
						offset += nb;

						if (request.abort || !isRunning()) {
							request.error = Error.OperationCanceled;
							return;
						}
					}
					fos.write(buffer, 0, offset);
					bytesReceived += offset;
					notifyProgress(request, bytesReceived, contentLength);
				} while (nb >= 0);

				fos.flush();

				// zoom
				if (paramMap.containsKey(ADJUST)
						&& ValueUtils.convertToBoolean(paramMap.get(ADJUST))) {
					final int height = ValueUtils.convertToInt(paramMap
							.get(HEIGHT));
					final int width = ValueUtils.convertToInt(paramMap
							.get(WIDTH));
					scaleBitmap(request, height, width);
				}

			} catch (SocketTimeoutException e) {
				Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
				request.error = Error.TimeOutError;
			} catch (IOException e) {
				Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
				request.error = Error.LocalFileIOError;
			} finally {
				try {
					httpEntity.consumeContent();
				} catch (IOException e) {
					Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
				}
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
					}

					if (request.error != Error.NoError) {
						request.file.delete();
					}
				}
			}
		} catch (SocketTimeoutException e) {
			Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
			request.error = Error.TimeOutError;
		} catch (IOException e) {
			Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
			request.error = Error.NetworkError;
		}

	}

	private void scaleBitmap(Request request, int height, int width) {
		try {
			Bitmap pic = BitmapFactory.decodeStream(new FileInputStream(
					request.file));
			pic = AdImageUtils.scaleBitmap(pic, width, height);
			pic.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(
					request.file));
		} catch (FileNotFoundException e) {
			Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
		}
	}

	private static HttpParams createHttpParams() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		return params;
	}
}
