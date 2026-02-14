package com.example.demo.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DailyDetailDto {
    private String employeeName;
    private String employeeCode;
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String shiftName;
    private String status;
    private int lateMinutes;
    private int earlyOutMinutes;
    private int overtimeMinutes;
}
