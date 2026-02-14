package com.example.demo.entity.timeRules;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer lateThreshold; // بالدقائق
    private Integer earlyLeaveThreshold; // بالدقائق
}

