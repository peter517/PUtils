package com.pengjun.eventsystem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class EventPublisher {

	private final static int MAX_EVENT_NUM = 100;
	private Set<EventProcessor> eventProcessorList = new HashSet<EventProcessor>();
	private BlockingQueue<Event> events = new ArrayBlockingQueue<Event>(
			MAX_EVENT_NUM);
	private static EventPublisher instance = null;
	private static Thread thread = null;
	private static boolean isRunning = false;

	public static EventPublisher getInstance() {
		if (instance == null) {
			instance = new EventPublisher();
		}
		return instance;
	}

	private EventPublisher() {

		thread = new Thread(new Runnable() {
			public void run() {
				isRunning = true;
				while (isRunning) {
					try {
						Event event = events.take();
						for (EventProcessor processor : eventProcessorList) {
							processor.handleEvent(event);
						}
					} catch (InterruptedException e1) {
						break;
					} catch (Exception e) {
					}
				}
			}
		}, "EventPublisher");

		thread.start();
	}

	public static void destroy() {
		if (thread != null) {
			isRunning = false;
			thread.interrupt();
		}
	}

	public synchronized void addEventProcessor(EventProcessor processor) {

		Set<EventProcessor> existEventProcessorSet = new HashSet<EventProcessor>();
		if (!eventProcessorList.contains(processor)) {
			for (EventProcessor eventProcessor : eventProcessorList) {
				if (eventProcessor.getClass().equals(processor.getClass())) {
					existEventProcessorSet.add(eventProcessor);
				}
			}

			for (EventProcessor existEventProcessor : existEventProcessorSet) {
				eventProcessorList.remove(existEventProcessor);
			}
			eventProcessorList.add(processor);
		}
	}

	public void publishEvent(Event event) {
		events.offer(event);
	}
}
