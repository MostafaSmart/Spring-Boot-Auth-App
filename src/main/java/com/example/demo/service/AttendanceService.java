package com.example.demo.service;

import com.example.demo.entity.Exceptions.LeaveRequest;
import com.example.demo.entity.Exceptions.LeaveRequestStatus;
import com.example.demo.entity.organizational.Employee;
import com.example.demo.entity.timeRules.Schedule;
import com.example.demo.entity.timeRules.Shift;
import com.example.demo.entity.transactions.AttendanceLog;
import com.example.demo.entity.transactions.DailyAttendanceReport;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleRepository scheduleRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final DailyAttendanceReportRepository dailyAttendanceReportRepository;

    /**
     * Finds min and max logs for an employee on a given date.
     * Compares with Schedule (Shift).
     * Calculates Late, Early, Overtime, Actual Work Hours.
     * Handles Absent / Missing Punch / Leave.
     */
    @Transactional
    public void calculateDailyAttendance(LocalDate date) {
        List<Employee> employees = employeeRepository.findAll();

        for (Employee emp : employees) {
            processEmployeeAttendance(emp, date);
        }
    }

    private void processEmployeeAttendance(Employee emp, LocalDate date) {
        // 1. Check if employee has a schedule for this day
        Optional<Schedule> scheduleOpt = scheduleRepository.findByEmployeeAndDayOfWeek(emp.getEmployeeCode(), date.getDayOfWeek());
        
        if (scheduleOpt.isEmpty()) {
            // No schedule (Weekend or Off day)
            // Optionally we can check if they came to work anyway
            return; 
        }

        Schedule schedule = scheduleOpt.get();
        Shift shift = schedule.getShift();

        // 2. Fetch Logs (Min and Max)
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        Optional<LocalDateTime> checkInOpt = attendanceLogRepository.findFirstLog(emp.getEmployeeCode(), startOfDay, endOfDay);
        Optional<LocalDateTime> checkOutOpt = attendanceLogRepository.findLastLog(emp.getEmployeeCode(), startOfDay, endOfDay);

        DailyAttendanceReport report = dailyAttendanceReportRepository
                .findByEmployee_EmployeeCodeAndDate(emp.getEmployeeCode(), date)
                .orElse(new DailyAttendanceReport());
        
        report.setEmployee(emp);
        report.setDate(date);

        // 3. Logic for ABSENT / LEAVE
        if (checkInOpt.isEmpty()) {
            // Check for Leave
            Optional<LeaveRequest> leaveOpt = leaveRequestRepository.findApprovedLeave(emp.getEmployeeCode(), date);
            if (leaveOpt.isPresent()) {
                report.setStatus("LEAVE");
            } else {
                report.setStatus("ABSENT");
            }
            report.setLateMinutes(0);
            report.setEarlyOutMinutes(0);
            report.setOvertimeMinutes(0);
            report.setWorkDurationMinutes(0L);
            
            dailyAttendanceReportRepository.save(report);
            return;
        }

        // 4. Logic for PRESENT / MISSING_OUT
        report.setCheckIn(checkInOpt.get());
        
        if (checkOutOpt.isEmpty() || checkInOpt.get().equals(checkOutOpt.get())) {
            // Only one punch or check-in == check-out (bounced)
            report.setStatus("MISSING_OUT");
            report.setCheckOut(null);
            dailyAttendanceReportRepository.save(report);
            return;
        }

        report.setCheckOut(checkOutOpt.get());
        report.setStatus("PRESENT");

        // 5. Calculate Metrics
        calculateMetrics(report, shift, date);

        dailyAttendanceReportRepository.save(report);
    }

    private void calculateMetrics(DailyAttendanceReport report, Shift shift, LocalDate date) {
        LocalDateTime checkIn = report.getCheckIn();
        LocalDateTime checkOut = report.getCheckOut();
        
        LocalDateTime shiftStart = date.atTime(shift.getStartTime());
        LocalDateTime shiftEnd = date.atTime(shift.getEndTime());

        // Late Minutes
        long lateMinutes = 0;
        if (checkIn.isAfter(shiftStart.plusMinutes(shift.getLateThreshold()))) {
             lateMinutes = Duration.between(shiftStart, checkIn).toMinutes();
        }
        report.setLateMinutes((int) Math.max(0, lateMinutes));

        // Early Out Minutes
        long earlyOutMinutes = 0;
        if (checkOut.isBefore(shiftEnd.minusMinutes(shift.getEarlyLeaveThreshold()))) {
            earlyOutMinutes = Duration.between(checkOut, shiftEnd).toMinutes();
        }
        report.setEarlyOutMinutes((int) Math.max(0, earlyOutMinutes));

        // Overtime Minutes
        long overtimeMinutes = 0;
        if (checkOut.isAfter(shiftEnd)) {
            overtimeMinutes = Duration.between(shiftEnd, checkOut).toMinutes();
        }
        report.setOvertimeMinutes((int) Math.max(0, overtimeMinutes));

        // Actual Work Duration
        long duration = Duration.between(checkIn, checkOut).toMinutes();
        report.setWorkDurationMinutes(duration);
    }

    @Transactional
    public void processRawLog(String employeeCode, LocalDateTime logTime, String deviceId) {
        AttendanceLog log = new AttendanceLog();
        log.setEmployeeCode(employeeCode);
        log.setLogTime(logTime);
        log.setDeviceId(deviceId);
        log.setPunchType("UNKNOWN"); // Determine logic if needed
        attendanceLogRepository.save(log);
    }
}
