package com.example.demo.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
@Component
public class MetricsController {
    
   
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    private Counter studentRequestCounter;
    
    @PostConstruct
    public void initMetrics() {
     
        studentRequestCounter = Counter.builder("student_requests_total")
                .description("Total number of requests to student endpoints")
                .tag("application", "student-management")
                .register(meterRegistry);
     
    }
    
    public void incrementStudentRequest() {
        studentRequestCounter.increment();
      
    }
    
    public Timer.Sample startTimer() {
     
        return Timer.start(meterRegistry);
    }
    
   public void stopTimer(Timer.Sample sample, String endpoint) {
 
    
    Timer timer = Timer.builder("student_request_duration")
            .description("Request duration for student endpoints")
            .tag("application", "student-management")
            .tag("endpoint", endpoint)
            .register(meterRegistry);
    
    double duration = sample.stop(timer);
    
   
}
}