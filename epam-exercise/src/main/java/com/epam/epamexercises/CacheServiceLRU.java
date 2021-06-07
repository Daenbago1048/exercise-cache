package com.epam.epamexercises;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheServiceLRU {

	private static Queue<MyObject> queue = new LinkedBlockingQueue<>();
	private final int CACHE_CAPACITY = 100_000;
	private Listener listener;
	private volatile AtomicInteger numberOfEvictions = new AtomicInteger(0);

	public MyObject get(MyObject key) {
		if (queue.contains(key)) {
			queue.remove(key);
			queue.add(key);
			return key;
		}
		return null;
	}

	public void set(MyObject object) {

		if (queue.size() == CACHE_CAPACITY) {
			listener.doTask(queue.poll());
			numberOfEvictions.getAndIncrement();
		}

		queue.add(object);
	}

	public String toString() {
		StringBuffer values = new StringBuffer();
		for (MyObject obj : queue) {
			values.append(obj.getValue() + "\n");
		}
		return values.toString();
	}
	
	public void addListener(Listener listener) {
    	this.listener = listener;
    }
}
