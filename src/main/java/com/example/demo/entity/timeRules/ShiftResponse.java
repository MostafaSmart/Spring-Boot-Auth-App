package com.example.demo.entity.timeRules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponse {
    private Long id;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer lateThreshold;
    private Integer earlyLeaveThreshold;
}
