package com.pengjun.android.loadresource.factory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ObjectFactory {

	Result decodeSize(File file, long maxSize, Map<String, Object> params)
			throws IOException;

	Result decodeFile(File file, Map<String, Object> params, Object opt)
			throws IOException;

	void destroy(Object obj, Object opt);

	boolean verify(Object obj, Map<String, Object> params, Object opt);

	public static class Result {
		public final Object obj;
		public final long size;
		public final Object opt;

		public Result(Object obj, long size, Object opt) {
			this.obj = obj;
			this.size = size;
			this.opt = opt;
		}
	}

	public static class FileParseException extends IOException {

		private static final long serialVersionUID = 8849061022466662166L;

		/**
		 * Constructs a new {@code FileParseException} with its stack trace
		 * filled in.
		 */
		public FileParseException() {
		}

		/**
		 * Constructs a new {@code FileParseException} with its stack trace and
		 * detail message filled in.
		 * 
		 * @param detailMessage
		 *            the detail message for this exception.
		 */
		public FileParseException(String detailMessage) {
			super(detailMessage);
		}

		/**
		 * Constructs a new instance of this class with detail message and cause
		 * filled in.
		 * 
		 * @param message
		 *            The detail message for the exception.
		 * @param cause
		 *            The detail cause for the exception.
		 * @since 1.6
		 */
		public FileParseException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * Constructs a new instance of this class with its detail cause filled
		 * in.
		 * 
		 * @param cause
		 *            The detail cause for the exception.
		 * @since 1.6
		 */
		public FileParseException(Throwable cause) {
			super(cause == null ? null : cause.toString(), cause);
		}
	}
}