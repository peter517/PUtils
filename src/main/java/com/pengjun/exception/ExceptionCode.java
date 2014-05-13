package com.pengjun.exception;

public enum ExceptionCode {

	OK("ok");

	private String info;

	public String getExceptionInfo() {
		return info;
	}

	ExceptionCode(String info) {
		this.info = info;
	}

}
