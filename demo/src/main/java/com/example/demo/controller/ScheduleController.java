package com.example.demo.controller;


import com.example.demo.DTO.schedule.response.ScheduleResponse;
import com.example.demo.DTO.schedule.request.NonRecurringScheduleRequest;
import com.example.demo.DTO.schedule.request.RecurringScheduleRequest;
import com.example.demo.DTO.schedule.response.NonRecurringScheduleResponse;
import com.example.demo.DTO.schedule.response.RecurringScheduleResponse;
import com.example.demo.DTO.schedule.response.TodayScheduleResponse;
import com.example.demo.domain.schedule.ScheduleType;
import com.example.demo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/recurring")
    public ResponseEntity<Void> saveRecurring(@RequestBody RecurringScheduleRequest request) {
        scheduleService.saveRecurring(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/nonRecurring")
    public ResponseEntity<Void> saveNonRecurring(@RequestBody NonRecurringScheduleRequest request) {
        scheduleService.saveNonRecurring(request);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{protectedUserId}/{type}")
    public ResponseEntity<List<String>> getTitlesByType(
            @PathVariable Long protectedUserId,
            @PathVariable ScheduleType type) {
        List<String> titles = scheduleService.getTitlesByType(protectedUserId, type);
        return ResponseEntity.ok(titles);
    }


    //스케줄 id로 받을 수 있으면
    @PutMapping("complete/{id}")
    public ResponseEntity<Void> completeSchedule(@PathVariable Long id) {
        scheduleService.completedSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recurring/{protectedUserId}")
    public ResponseEntity<List<RecurringScheduleResponse>> getRecurringSchedules(@PathVariable Long protectedUserId) {
        return ResponseEntity.ok(scheduleService.getRecurringSchedulesByUser(protectedUserId));
    }

    @GetMapping("/nonRecurring/{protectedUserId}")
    public ResponseEntity<List<NonRecurringScheduleResponse>> getNonRecurringSchedules(@PathVariable Long protectedUserId) {
        return ResponseEntity.ok(scheduleService.getNonRecurringSchedulesByUser(protectedUserId));
    }

    @GetMapping("/{protectedUserId}/date/{date}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByDate(
            @PathVariable Long protectedUserId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesByDate(protectedUserId, date);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/today/{protectedUserId}")
    public ResponseEntity<List<TodayScheduleResponse>> getTodaySchedules(@PathVariable Long protectedUserId) {
        List<TodayScheduleResponse> schedules = scheduleService.getTodaySchedules(protectedUserId);
        return ResponseEntity.ok(schedules);
    }

    @DeleteMapping("/{protectedUserId}/{title}")
    public ResponseEntity<Void> deleteSchedules(
            @PathVariable Long protectedUserId,
            @PathVariable String title) {
        scheduleService.deleteScheduleByTitle(protectedUserId, title);
        return ResponseEntity.noContent().build();
    }

}