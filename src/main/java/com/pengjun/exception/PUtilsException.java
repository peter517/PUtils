package com.pengjun.exception;

@SuppressWarnings("serial")
public class PUtilsException extends Exception {

	private ExceptionCode exceptionCode = null;

	public PUtilsException(ExceptionCode errorCode) {
		super(errorCode.getExceptionInfo());
		this.exceptionCode = errorCode;

	}

	public PUtilsException(ExceptionCode errorCode, String reason) {
		super(errorCode.getExceptionInfo() + ", reason: " + reason);
	}

	public PUtilsException(ExceptionCode errorCode, Throwable throwable) {
		super(errorCode.getExceptionInfo(), throwable);
	}

	public ExceptionCode getExceptionCode() {
		return exceptionCode;
	}

}
