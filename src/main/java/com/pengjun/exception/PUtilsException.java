package com.pengjun.exception;

public class PUtilsException extends Exception {

	private static final long serialVersionUID = 1L;

	private ExceptionCode exceptionCode = null;
	private Exception subException = null;

	public PUtilsException(ExceptionCode errCode, Exception subException) {
		this.exceptionCode = errCode;
		this.subException = subException;
	}

	public PUtilsException(ExceptionCode errCode) {
		this.exceptionCode = errCode;
	}

	public ExceptionCode getExceptionCode() {
		return exceptionCode;
	}

	public Exception getSubException() {
		return subException;
	}

	public String getMessage() {
		if (subException != null) {
			return subException.getMessage();
		}
		return super.getMessage();
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
