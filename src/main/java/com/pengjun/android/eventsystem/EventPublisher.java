package com.pengjun.android.eventsystem;

public class EventPublisher {

	private EventHandler messageHandler = EventHandler.getInstance();

	private static EventPublisher instance = null;

	public static EventPublisher getInstance() {
		if (instance == null) {
			instance = new EventPublisher();
		}
		return instance;
	}

	public static void destroy() {
		if (instance != null) {
			EventHandler.getInstance().destory();
			instance = null;
		}
	}

	public void start() {
		if (instance == null) {
			instance = new EventPublisher();
		}
	}

	public void stop() {
		removeAllEventProcessor();
	}

	public synchronized void addMessageProcessor(EventProcessor processor) {
		messageHandler.addEventProcessor(processor);
	}

	public void publishEvent(Event event) {
		messageHandler.publishEvent(event);
	}

	private void removeAllEventProcessor() {
		messageHandler.removeAllEventProcessor();
	}

}
