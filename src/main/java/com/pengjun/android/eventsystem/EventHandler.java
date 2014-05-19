package com.pengjun.android.eventsystem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.pengjun.android.component.CreateLooperThread;

public class EventHandler extends Handler {

	private static EventHandler instance = null;
	private Set<EventProcessor> eventProcessorSet = Collections
			.synchronizedSet(new HashSet<EventProcessor>());
	private static CreateLooperThread thread = new CreateLooperThread();

	private EventHandler(Looper looper) {
		super(looper);
	}

	public static EventHandler getInstance() {

		if (instance == null) {
			thread.start();
			try {
				thread.waitForMyLooper();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return new EventHandler(thread.myLooper());
		}
		return instance;

	}

	public static void destory() {
		if (instance != null) {
			thread.quit();
			instance = null;
		}
	}

	public synchronized void addEventProcessor(EventProcessor processor) {

		Set<EventProcessor> existEventProcessorSet = new HashSet<EventProcessor>();
		if (!eventProcessorSet.contains(processor)) {
			for (EventProcessor eventProcessor : eventProcessorSet) {
				if (eventProcessor.getClass().equals(processor.getClass())) {
					existEventProcessorSet.add(eventProcessor);
				}
			}

			for (EventProcessor rmEventProcessor : existEventProcessorSet) {
				eventProcessorSet.remove(rmEventProcessor);
			}
			eventProcessorSet.add(processor);
		}

	}

	public void removeAllEventProcessor() {
		eventProcessorSet.clear();
	}

	public void publishEvent(Event event) {
		Message msg = new Message();
		msg.obj = event;
		msg.what = event.getCode();
		sendMessage(msg);
	}

	public void handleMessage(Message msg) {
		for (EventProcessor eventProcessor : eventProcessorSet) {
			eventProcessor.handleEvent((Event) msg.obj);
			// removeMessages(msg.what);
		}
	}
}
