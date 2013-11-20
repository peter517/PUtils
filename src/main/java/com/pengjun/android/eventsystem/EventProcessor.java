package com.pengjun.android.eventsystem;

public abstract class EventProcessor {

	public EventProcessor() {
		EventPublisher.getInstance().addMessageProcessor(this);
	}

	public abstract void handleEvent(Event event);

}
