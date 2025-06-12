package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class ScheduledTaskService {

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void registerTask(Long notificationId, ScheduledFuture<?> future) {
        scheduledTasks.put(notificationId, future);
    }

    public void cancelTask(Long notificationId) {
        ScheduledFuture<?> future = scheduledTasks.remove(notificationId);
        if (future != null) {
            future.cancel(false);
        }
    }

    private void printScheduledTasks() {
        Set<Long> keys = scheduledTasks.keySet();
        log.info("scheduledTasks: {}", keys);
    }
}