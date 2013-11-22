package com.pengjun.android.loadresource.downloader;

import java.io.File;

public interface Downloader {

	public static enum Error {
		NoError, OperationCanceled, ContentAccessDenied, ContentOperationNotPermitted, ContentNotFound, NetworkError, ContentError, TimeOutError, LocalFileIOError, UnknownError
	}

	public static class Request {
		public final String urn;
		public final File file;
		public final Object params;
		public final Object tag;

		public volatile boolean abort = false;
		public volatile Error error = Error.NoError;

		public Request(String urn, File file, Object params, Object tag) {
			this.urn = urn;
			this.file = file;
			this.params = params;
			this.tag = tag;
		}

		public boolean isValid() {
			return urn != null && file != null;
		}
	}

	public interface ReplyListener {
		void onDownloadProgress(Request request, long bytesReceived,
				long bytesTotal);

		void onDownloadFinished(Request request);
	}

	void get(Request request);

	void abort(String urn);

	void abortAll();

	void stop();
}