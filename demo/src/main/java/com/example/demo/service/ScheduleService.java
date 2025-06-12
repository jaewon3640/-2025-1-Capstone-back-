package com.example.demo.service;


import com.example.demo.DTO.schedule.response.ScheduleResponse;
import com.example.demo.DTO.schedule.request.NonRecurringScheduleRequest;
import com.example.demo.DTO.schedule.request.RecurringScheduleRequest;
import com.example.demo.DTO.schedule.response.NonRecurringScheduleResponse;
import com.example.demo.DTO.schedule.response.RecurringScheduleResponse;
import com.example.demo.DTO.schedule.response.TodayScheduleResponse;
import com.example.demo.domain.schedule.RecurringSchedule;
import com.example.demo.domain.schedule.Schedule;
import com.example.demo.domain.schedule.ScheduleType;
import com.example.demo.exception.ExistsSameScheduleException;
import com.example.demo.exception.ScheduleNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.RecurringScheduleRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RecurringScheduleRepository recurringScheduleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CaregiverNotificationService caregiverNotificationService;

    @Transactional
    public void saveRecurring(RecurringScheduleRequest request) {
        existsProtectedUserId(request.getProtectedUserId());
        existsSameSchedule(request.getProtectedUserId(), request.getTitle());

        List<String> koreanDays = request.getDayOfWeek().stream()
                .map(this::mapDayOfWeekToKorean)
                .collect(Collectors.toList());

        RecurringSchedule recurringSchedule = RecurringSchedule.builder()
                .protectedUserId(request.getProtectedUserId())
                .title(request.getTitle())
                .type(request.getType())
                .dayOfWeek(koreanDays)
                .time(request.getTime().stream()
                        .map(LocalTime::toString)
                        .collect(Collectors.toList()))
                .endDay(request.getEndDay())
                .build();

        recurringScheduleRepository.save(recurringSchedule);

        List<Schedule> schedules = new ArrayList<>();
        LocalDate start = LocalDate.now();
        LocalDate end = request.getEndDay();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (request.getDayOfWeek().contains(date.getDayOfWeek())) {
                for (LocalTime time : request.getTime()) {
                    Schedule schedule = Schedule.builder()
                            .protectedUserId(request.getProtectedUserId())
                            .title(request.getTitle())
                            .type(request.getType())
                            .dateTime(LocalDateTime.of(date, time))
                            .completed(false)
                            .recurring(true)
                            .build();
                    schedules.add(schedule);
                }
            }
        }

        scheduleRepository.saveAll(schedules);

        notificationService.saveRecurringNotifications(schedules);
    }

    private String mapDayOfWeekToKorean(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "월";
            case TUESDAY: return "화";
            case WEDNESDAY: return "수";
            case THURSDAY: return "목";
            case FRIDAY: return "금";
            case SATURDAY: return "토";
            case SUNDAY: return "일";
            default: throw new IllegalArgumentException("올바르지 않은 요일: " + dayOfWeek);
        }
    }

    @Transactional
    public void saveNonRecurring(NonRecurringScheduleRequest request) {
        existsProtectedUserId(request.getProtectedUserId());
        existsSameSchedule(request.getProtectedUserId(), request.getTitle());

        Schedule schedule = Schedule.builder()
                .protectedUserId(request.getProtectedUserId())
                .title(request.getTitle())
                .type(request.getType())
                .dateTime(request.getDateTime())
                .completed(false)
                .recurring(false)
                .build();

        scheduleRepository.save(schedule);

        notificationService.saveNonRecurringNotification(schedule);
    }

    public List<RecurringScheduleResponse> getRecurringSchedulesByUser(Long protectedUserId) {
        existsProtectedUserId(protectedUserId);
        List<RecurringSchedule> schedules = recurringScheduleRepository.findByProtectedUserId(protectedUserId);

        LocalDate today = LocalDate.now();
        return schedules.stream()
                .filter(s -> !s.getEndDay().isBefore(today))
                .map(s -> new RecurringScheduleResponse(
                        s.getTitle(),
                        s.getType(),
                        s.getDayOfWeek().toString(),
                        s.getTime().toString(),
                        s.getEndDay()
                ))
                .collect(Collectors.toList());
    }

    public List<NonRecurringScheduleResponse> getNonRecurringSchedulesByUser(Long protectedUserId) {
        existsProtectedUserId(protectedUserId);
        List<Schedule> schedules = scheduleRepository.findByProtectedUserIdAndRecurringFalse(protectedUserId);

        LocalDate today = LocalDate.now();
        return schedules.stream()
                .filter(s -> !s.getDateTime().toLocalDate().isBefore(today))
                .sorted(Comparator.comparing(Schedule::getDateTime))
                .map(s -> new NonRecurringScheduleResponse(
                        s.getTitle(),
                        s.getType(),
                        s.getDateTime()
                ))
                .collect(Collectors.toList());
    }

    public List<String> getTitlesByType(Long protectedUserId, ScheduleType type) {
        return scheduleRepository.findDistinctTitleByProtectedUserIdAndType(protectedUserId, type);
    }

    public List<TodayScheduleResponse> getTodaySchedules(Long protectedUserId) {
        existsProtectedUserId(protectedUserId);
        LocalDate today = LocalDate.now();

        List<Schedule> schedules = scheduleRepository.findByProtectedUserIdAndDateTimeBetween(
                protectedUserId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );

        return schedules.stream()
                .map(s -> new TodayScheduleResponse(
                        s.getTitle(),
                        s.getType(),
                        formatTimeToKorean(s.getDateTime().toLocalTime())
                ))
                .collect(Collectors.toList());
    }

    public List<ScheduleResponse> getSchedulesByDate(Long protectedUserId, LocalDate date) {
        existsProtectedUserId(protectedUserId);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Schedule> schedules = scheduleRepository.findByProtectedUserIdAndDateTimeBetween(protectedUserId, start, end);

        return schedules.stream()
                .map(s -> new ScheduleResponse(
                        s.getId(),
                        s.getTitle(),
                        s.getType(),
                        formatTimeToKorean(s.getDateTime().toLocalTime()),
                        s.isCompleted()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteScheduleByTitle(Long protectedUserId, String title) {
        notificationService.deleteNotifications(
                scheduleRepository.findByTitleAndProtectedUserId(title, protectedUserId));

        scheduleRepository.deleteByProtectedUserIdAndTitle(protectedUserId, title);
        recurringScheduleRepository.deleteByProtectedUserIdAndTitle(protectedUserId, title);
    }

    //스케줄 id로 받을 수 있으면
    public void completedSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일정이 존재하지 않습니다."));

        schedule.setCompleted(true);
        scheduleRepository.save(schedule);

        try {
            caregiverNotificationService.notifyScheduleCompleted(scheduleId);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void existsProtectedUserId(Long protectedUserId) {
        userRepository.findById(protectedUserId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 존재하지 않습니다."));
    }

    private void existsSameSchedule(Long protectedUserId, String title) {
        List<Schedule> schedules = scheduleRepository.findByProtectedUserIdAndTitle(protectedUserId, title);
        if (!schedules.isEmpty()) {
            throw new ExistsSameScheduleException("이미 같은 제목의 일정이 존재합니다: " + title);
        }
    }

    private String formatTimeToKorean(LocalTime time) {
        return String.format("%02d시 %02d분", time.getHour(), time.getMinute());
    }
}