package com.epam.epamexercises;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class Main {

	public static void main(String[] args) {
		Listener listener = new Listener();
		CacheServiceLFU cache = new CacheServiceLFU();
		cache.addListener(listener);
		
		MyObject object;
		for (int i = 0; i < 100_002; i++) {
			object = new MyObject("MyOb"+i);
			cache.set(object);
		}
		
		//System.out.println(cache);
		System.out.println("Number of evictions: "+cache.getNumberOfEvictions());
		System.out.println("Average time of new values in seconds: "+cache.getAverageTimeOfSet());

		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				Listener listener = new Listener();
				CacheServiceLFU cache = new CacheServiceLFU();
				cache.addListener(listener);
				Random random = new Random();
				int value = random.nextInt(100_010 -100_001 + 1) + 100_001;
				
				MyObject object;
				for (int i = 0; i < value ; i++) {
					object = new MyObject("MyOb"+i);
					cache.set(object);
				}
				
				//System.out.println(cache);
				System.out.println("Number of evictions: "+ cache.getNumberOfEvictions());
				System.out.println("Average time of new values in seconds: "+cache.getAverageTimeOfSet());
			}
		};
		
		ExecutorService executer = Executors.newFixedThreadPool(3);
		
		executer.execute(r);
		executer.execute(r);
		executer.execute(r);
		executer.shutdown();
        
		/*---------------GUAVA CACHE----------------*/
        
        CacheLoader<MyObject, String> loader;
        loader = new CacheLoader<MyObject, String>() {
			@Override
			public String load(MyObject key) throws Exception {
				 return key.getValue();
			}
        };
        
        
        Runnable r2 = new Runnable() {
			@Override
			public void run() {
				LoadingCache<MyObject, String> cacheGuava = CacheBuilder.newBuilder()
		          .expireAfterAccess(5000,TimeUnit.MILLISECONDS)
		          .maximumSize(100_000)
		          .build(loader);
		        
		        Random random = new Random();
				int value = random.nextInt(100_010 -100_001 + 1) + 100_001;
				
				MyObject object;
				for (int i = 0; i < value ; i++) {
					object = new MyObject("MyOb"+i);
					cacheGuava.getUnchecked(object);
				}
				
				System.out.println(""+cacheGuava.size());
				
			}
		};
        
        
        ExecutorService executerG = Executors.newFixedThreadPool(3);
        executerG.execute(r2);
        executerG.execute(r2);
        executerG.execute(r2);
		executer.shutdown();
		executerG.shutdownNow();
	}

}
