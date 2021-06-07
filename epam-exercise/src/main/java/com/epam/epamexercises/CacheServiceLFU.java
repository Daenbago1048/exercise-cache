package com.epam.epamexercises;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheServiceLFU {
	
	private final  int MAX_CAPACITY =  100_000;
	private Map<MyObject,CacheNode> data = new ConcurrentHashMap<>();
	private Map<MyObject,AtomicInteger> counts = new ConcurrentHashMap<>();
	private ConcurrentSkipListMap<Integer,Queue<MyObject>> frequencies = new ConcurrentSkipListMap<>();
	private Listener listener;
	private volatile AtomicInteger numberOfEvictions = new AtomicInteger(0);
	private long timeElapsed = 0L;
	
	public MyObject get(MyObject key) {
        if (!data.containsKey(key)) {
            return null;
        }
        CacheNode node = data.get(key);
        AtomicInteger frequency = counts.get(key);
        frequencies.get(frequency.get()).remove(key);
        removeIfListEmpty(frequency.get());
        frequencies.computeIfAbsent(frequency.get() + 1, k -> new LinkedBlockingQueue<MyObject>()).add(node.getKey());

        counts.putIfAbsent(key, new AtomicInteger(frequency.get()+1));
        return data.get(key).getKey();    
    }

    public void set(MyObject key) {
    	Instant start = Instant.now();
        if (!data.containsKey(key)) {
            if (data.size() == MAX_CAPACITY) {

                int lowestCount = frequencies.firstKey();   // smallest frequency
                MyObject nodeTodelete = frequencies.get(lowestCount).poll();
                frequencies.get(lowestCount).remove(nodeTodelete);

                removeIfListEmpty(lowestCount);
                data.remove(nodeTodelete, data.get(nodeTodelete));
                counts.remove(nodeTodelete);
                listener.doTask(nodeTodelete);
                numberOfEvictions.incrementAndGet();
            }
            CacheNode node =  new CacheNode(key,new Date()); 
            data.putIfAbsent(key, node);
            counts.putIfAbsent(key, new AtomicInteger(1));
            frequencies.computeIfAbsent(1, k -> new LinkedBlockingQueue<MyObject>()).add(node.getKey());
        }
        Instant finish = Instant.now();
        timeElapsed += Duration.between(start, finish).toMillis();
    }

    private void removeIfListEmpty(int frequency) {
        if (frequencies.get(frequency).isEmpty()) {
            frequencies.remove(frequency);
        }
    }
	
    @Override
    public String toString() {
    	StringBuffer values = new StringBuffer();
    	for(MyObject obj:data.keySet()) {
    		values.append(obj.getValue()+"\n");
    	}
    	return values.toString();
    }
    
    public double getAverageTimeOfSet() {
    	long total = data.size() + numberOfEvictions.get();
    	return (double)((double)timeElapsed/total)/1000;
    }
    
    public void addListener(Listener listener) {
    	this.listener = listener;
    }
    
    public int getNumberOfEvictions() {
    	return numberOfEvictions.get();
    }
}
