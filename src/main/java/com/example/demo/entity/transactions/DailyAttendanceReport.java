package com.example.demo.entity.transactions;

import com.example.demo.entity.organizational.Employee;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor
public class DailyAttendanceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    
    private Long workDurationMinutes;
    private Integer lateMinutes;
    private Integer earlyOutMinutes;
    private Integer overtimeMinutes;
    
    private String status; // Present, Absent, Leave, Holiday
}
