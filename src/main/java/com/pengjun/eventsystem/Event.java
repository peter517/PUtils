package com.pengjun.eventsystem;

public class Event {

	private String info;
	private int code;

	public Event(int code, String info) {
		this.code = code;
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return info;
	}
}
