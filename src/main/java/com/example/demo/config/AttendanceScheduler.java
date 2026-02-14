package com.example.demo.config;

import com.example.demo.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private final AttendanceService attendanceService;

    // Runs every day at 2:00 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleDailyAttendance() {
        log.info("Starting scheduled daily attendance calculation...");
        LocalDate yesterday = LocalDate.now().minusDays(1);
        attendanceService.calculateDailyAttendance(yesterday);
        log.info("Completed daily attendance calculation for {}", yesterday);
    }
}
