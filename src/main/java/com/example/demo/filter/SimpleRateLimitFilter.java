package com.example.demo.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class SimpleRateLimitFilter implements Filter {
    
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        
        if (httpRequest.getRequestURI().startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }
        
        String clientIP = getClientIP(httpRequest);
        Bucket bucket = getBucket(clientIP);
        
        if (bucket.tryConsume(1)) {
        
            chain.doFilter(request, response);
        } else {
        
            httpResponse.setStatus(409);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            
            String jsonResponse = 
                "{\"error\": \"Rate limit exceeded\", " +
                "\"message\": \"Too many requests - limit: 10 requests per minute\", " +
                "\"code\": 409}";
            
            httpResponse.getWriter().write(jsonResponse);
        }
    }
    
    private Bucket getBucket(String clientIP) {
        return buckets.computeIfAbsent(clientIP, this::createBucket);
    }
    
    private Bucket createBucket(String key) {
        
        Bandwidth bandwidth = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(bandwidth).build();
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}