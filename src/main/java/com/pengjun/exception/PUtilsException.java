package com.pengjun.exception;

@SuppressWarnings("serial")
public class PUtilsException extends Exception {

	private ExceptionCode exceptionCode = null;
	private String reason = null;

	public PUtilsException(ExceptionCode errCode) {
		this.exceptionCode = errCode;
	}

	public PUtilsException(ExceptionCode errCode, String reason) {
		this.exceptionCode = errCode;
		this.reason = reason;
	}

	public PUtilsException(ExceptionCode errCode, Throwable throwable) {
		super(throwable);
		this.exceptionCode = errCode;
		this.reason = throwable.getMessage();
	}

	public String getMessage() {
		if (reason != null) {
			return "errorCode: " + exceptionCode + ", reason: " + reason;
		} else {
			return "errorCode: " + exceptionCode;
		}
	}

	public ExceptionCode getExceptionCode() {
		return exceptionCode;
	}

	public String getReason() {
		return reason;
	}

}
