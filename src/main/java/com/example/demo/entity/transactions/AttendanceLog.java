package com.example.demo.entity.transactions;

import com.example.demo.entity.organizational.Employee;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {
    @Index(name = "idx_log_time", columnList = "logTime"),
    @Index(name = "idx_emp_code", columnList = "employeeCode")
})
@Data
@NoArgsConstructor
public class AttendanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeCode;
    private LocalDateTime logTime;
    private String deviceId;
    private String punchType; // Check-in / Check-out
}

