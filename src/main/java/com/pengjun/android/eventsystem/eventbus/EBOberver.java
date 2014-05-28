package com.pengjun.android.eventsystem.eventbus;

public class EBOberver {
	public void onEvent(EBEvent event) {
		System.out.println("recv event");
	}
}
