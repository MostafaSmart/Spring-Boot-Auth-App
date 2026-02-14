package com.example.demo.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExceptionReportDto {
    private String employeeName;
    private String employeeCode;
    private LocalDate date;
    private String exceptionType; // Late, Missing Out, Absent
    private String details; // e.g., "Late by 45 mins"
}
