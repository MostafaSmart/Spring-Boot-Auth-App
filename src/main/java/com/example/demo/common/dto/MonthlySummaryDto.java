package com.example.demo.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlySummaryDto {
    private String employeeName;
    private String employeeCode;
    private int presentDays;
    private int absentDays;
    private long totalLateMinutes;
    private long totalOvertimeMinutes;
    private long totalWorkHours;
}
