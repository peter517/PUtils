package com.pengjun.android.eventsystem.eventbus;

import de.greenrobot.event.EventBus;

public class EBTest {

	public static void main(String[] args) {
		EventBus eventBus = new EventBus();
		eventBus.register(new EBOberver());

		eventBus.post(new EBEvent());

	}
}
