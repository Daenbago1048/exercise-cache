package com.epam.epamexercises;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@SpringBootTest
class EpamExerciseApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Test
	public void whenCacheMiss_thenValueIsComputed() throws InterruptedException {
		CacheLoader<String, String> loader;
	    loader = new CacheLoader<String, String>() {
	        @Override
	        public String load(String key) {
	            return key.toUpperCase();
	        }
	    };

	    LoadingCache<String, String> cache;
	    cache = CacheBuilder.newBuilder().weakKeys().build(loader);
	}
}
