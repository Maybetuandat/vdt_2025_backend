package com.example.demo.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    // Cấu hình: 10 requests per minute
    private static final int CAPACITY = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    
    public boolean tryConsume(String key) {
        return getBucket(key).tryConsume(1);
    }
    
    public long getAvailableTokens(String key) {
        return getBucket(key).getAvailableTokens();
    }
    
    public long getCapacity() {
        return CAPACITY;
    }
    
    public String getWindow() {
        return "1 minute";
    }
    
    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, this::createBucket);
    }
    
    private Bucket createBucket(String key) {
        // Tạo bandwidth: 10 tokens, refill 10 tokens mỗi phút
        Bandwidth bandwidth = Bandwidth.classic(CAPACITY, Refill.intervally(CAPACITY, WINDOW));
        
        return Bucket4j.builder()
                .addLimit(bandwidth)
                .build();
    }
    
    // Method để clear buckets (optional, cho testing)
    public void clearBuckets() {
        buckets.clear();
    }
    
    // Method để get bucket info cho monitoring
    public String getBucketInfo(String key) {
        Bucket bucket = buckets.get(key);
        if (bucket == null) {
            return "No requests yet";
        }
        
        long available = bucket.getAvailableTokens();
        return String.format("Available tokens: %d/%d", available, CAPACITY);
    }
}