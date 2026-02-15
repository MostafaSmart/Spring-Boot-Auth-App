package com.example.demo.entity.timeRules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {
    private Long id;

    //employee
    private Long employeeId;
    private String employeeName;
    private String employeeCode;

    //shift
    private Long shiftId;
    private String shiftName;
    private String shiftStart;
    private String shiftEnd;


    private List<DayOfWeek> daysOfWeek;





}
