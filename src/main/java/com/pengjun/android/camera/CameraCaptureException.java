package com.pengjun.android.camera;

public class CameraCaptureException extends Exception {

	private static final long serialVersionUID = 1L;

	CameraCaptureCode exceptionCode;
	private Exception subException = null;

	public CameraCaptureException(CameraCaptureCode exceptionCode,
			Exception subException) {
		this.exceptionCode = exceptionCode;
		this.subException = subException;
	}

	public CameraCaptureException(CameraCaptureCode exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public CameraCaptureCode getExceptionCode() {
		return exceptionCode;
	}

	@Override
	public String toString() {
		String info = "";
		if (subException != null) {
			info = "[ExceptionCode=" + exceptionCode + "]"
					+ subException.getMessage();
		} else {
			info = "[ExceptionCode=" + exceptionCode + "]";
		}

		return info;
	}
}
